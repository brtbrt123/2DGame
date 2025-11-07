package tile;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import Main.Jpanel;

public class TileManager {

    Jpanel gp;
    public Tile[] tile;
    public int[][][] mapTileNum;
    int numLayers = 4; 
    public ArrayList<ObjectCollision> objectCollisions = new ArrayList<>();

    public TileManager(Jpanel gp) {
        this.gp = gp;
        tile = new Tile[1600];  // Adjust based on your tileset size
        mapTileNum = new int[numLayers][gp.maxWorldCol][gp.maxWorldRow];
        
        getTileImage();  // Load tile images (unchanged)
        loadTSXTileset("/Tiles2/tile_1527.tsx");
        
        // Load from JSON instead of text files
        loadTiledMap("/map/haha.json");  // Replace with your JSON file path
    }


    

    public void debugTileLoading() {
        System.out.println("=== TILE LOADING DEBUG ===");
        int loadedTiles = 0;
        for (int i = 0; i < tile.length; i++) {
            if (tile[i] != null && tile[i].image != null) {
                loadedTiles++;
            }
        }
        System.out.println("Tiles loaded: " + loadedTiles + " out of " + tile.length);
        
        // Check first 10 tiles
        for (int i = 0; i < 10; i++) {
            if (tile[i] != null) {
                System.out.println("Tile " + i + ": " + (tile[i].image != null ? "LOADED" : "NULL IMAGE"));
            } else {
                System.out.println("Tile " + i + ": NULL");
            }
        }
    }
    
    public void loadTiledMap(String filePath) {
        TiledMapData mapData = JsonParser.parseTiledMap(filePath, gp.scale);
        if (mapData != null) {
            System.out.println("Loaded JSON map: " + mapData.layers.size() + " layers, " + mapData.objects.size() + " objects");
            
            // Load tile layers
            for (int i = 0; i < Math.min(mapData.layers.size(), numLayers); i++) {
                LayerData layer = mapData.layers.get(i);
                loadJsonLayerData(layer.tiles, i, layer.width);
            }
            
            // Load objects for collision
            objectCollisions.clear();
            for (ObjectData obj : mapData.objects) {
                objectCollisions.add(new ObjectCollision(obj.name, obj.x, obj.y, obj.width, obj.height));
            }
        } else {
            System.out.println("Failed to load JSON map");
        }
    }

    private void loadJsonLayerData(int[] data, int layerIndex, int layerWidth) {
        int index = 0;
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                if (index < data.length) {
                    mapTileNum[layerIndex][col][row] = data[index];
                    index++;
                }
            }
        }
    }

    // Update getTileImage to include collision detection
    public void getTileImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/map/tiles.txt");
            if (is == null) {
                System.out.println("ERROR: tiles.txt not found!");
                return;
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            int index = 0;

            System.out.println("Reading tiles.txt...");
            
            while ((line = br.readLine()) != null && index < tile.length) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                tile[index] = new Tile();
                
                // Handle both formats: "filename.png" and "filename.png,true"
                String filename;
                if (line.contains(",")) {
                    String[] parts = line.split(",");
                    filename = parts[0].trim();
                    tile[index].collision = Boolean.parseBoolean(parts[1].trim());
                } else {
                    filename = line;
                    tile[index].collision = autoDetectCollision(filename);
                }
                
                // Load the image
                String imagePath = "/Tiles2/" + filename;
                InputStream imageStream = getClass().getResourceAsStream(imagePath);
                if (imageStream != null) {
                    tile[index].image = ImageIO.read(imageStream);
                    imageStream.close();
                    System.out.println("Loaded: " + filename);
                } else {
                    System.out.println("ERROR: Could not load " + imagePath);
                    tile[index].image = null;
                }
                
                index++;
            }
            br.close();
            System.out.println("Finished loading " + index + " tiles");
            
        } catch (IOException e) {
            System.out.println("ERROR in getTileImage: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void loadTSXTileset(String tsxPath) {
        try {
            // Load TSX XML from resources
            InputStream tsxStream = getClass().getResourceAsStream(tsxPath);
            if (tsxStream == null) {
                System.out.println("ERROR: TSX file not found -> " + tsxPath);
                return;
            }

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(tsxStream);

            Element tileset = (Element) doc.getElementsByTagName("tileset").item(0);
            int tileWidth = Integer.parseInt(tileset.getAttribute("tilewidth"));
            int tileHeight = Integer.parseInt(tileset.getAttribute("tileheight"));

            Element imageElement = (Element) doc.getElementsByTagName("image").item(0);
            String source = imageElement.getAttribute("source");
            String imagePath = source.startsWith("/") ? source : "/Tiles2/" + new File(source).getName();

            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream == null) {
                System.out.println("ERROR: Cannot find image resource: " + imagePath);
                return;
            }

            BufferedImage fullImage = ImageIO.read(imageStream);
            imageStream.close();

            if (fullImage == null) {
                System.out.println("Failed to load tileset image: " + imagePath);
                return;
            }

            int rows = fullImage.getHeight() / tileHeight;
            int cols = fullImage.getWidth() / tileWidth;
            int index = 0;

            System.out.println("Cutting tileset: " + imagePath);
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    if (index >= tile.length) return;

                    Tile newTile = new Tile();
                    newTile.image = fullImage.getSubimage(
                        x * tileWidth, y * tileHeight, tileWidth, tileHeight
                    );
                    newTile.collision = autoDetectCollision(imagePath);
                    tile[index++] = newTile;
                }
            }

            System.out.println("Finished slicing TSX tileset (" + index + " tiles).");

        } catch (Exception e) {
            System.out.println("Error loading TSX: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean autoDetectCollision(String fileName) {
        String lowerName = fileName.toLowerCase();
        String[] collisionKeywords = {
            "wall", "tree", "rock", "stone", "mountain", "building", 
            "house", "fence", "barrier", "block", "solid", "water", 
            "lava", "cliff", "obstacle", "pillar", "column", "statue"
        };
        
        for (String keyword : collisionKeywords) {
            if (lowerName.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    // Keep your existing methods
    public void loadMap(String filePath, int layer) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0, row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break;
                String[] numbers = line.trim().split("\\s+");
                col = 0;

                for ( col = 0; col < gp.maxWorldCol && col < numbers.length; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[layer][col][row] = num;
                }
                row++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public void draw(Graphics2D g2) {
        int tilesDrawn = 0;
        int outOfRangeTiles = 0;

        // --- Draw layer 0 (ground) ---
        if (numLayers > 0) {
            int layer = 0;
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    int tileNum = mapTileNum[layer][col][row];

                    if (tileNum == 0) continue;
                    if (tileNum > tile.length || tileNum < 1) {
                        outOfRangeTiles++;
                        continue;
                    }

                    Tile currentTile = tile[tileNum - 1];
                    if (currentTile == null || currentTile.image == null) continue;

                    int worldX = col * gp.tileSize;
                    int worldY = row * gp.tileSize;
                    int screenX = worldX - gp.player.worldX + gp.player.screenX;
                    int screenY = worldY - gp.player.worldY + gp.player.screenY;

                    g2.drawImage(currentTile.image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                    tilesDrawn++;
                }
            }
        }

        // --- Draw player between layer 0 and 1 ---
        gp.player.draw(g2);

        // --- Draw layer 1 and above (trees, walls, etc.) ---
        for (int layer = 1; layer < numLayers; layer++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    int tileNum = mapTileNum[layer][col][row];

                    if (tileNum == 0) continue;
                    if (tileNum > tile.length || tileNum < 1) {
                        outOfRangeTiles++;
                        continue;
                    }

                    Tile currentTile = tile[tileNum - 1];
                    if (currentTile == null || currentTile.image == null) continue;

                    int worldX = col * gp.tileSize;
                    int worldY = row * gp.tileSize;
                    int screenX = worldX - gp.player.worldX + gp.player.screenX;
                    int screenY = worldY - gp.player.worldY + gp.player.screenY;

                    g2.drawImage(currentTile.image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                    tilesDrawn++;
                }
            }
        }

        // --- Debug overlay ---
        g2.setColor(Color.RED);
        g2.drawString("Tiles drawn: " + tilesDrawn, 10, 20);
        g2.drawString("Out of range: " + outOfRangeTiles, 10, 40);
        g2.drawString("Player: " + gp.player.worldX + ", " + gp.player.worldY, 10, 60);
    }

    
    
}