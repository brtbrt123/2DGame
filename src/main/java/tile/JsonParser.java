package tile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static TiledMapData parseTiledMap(String filePath, int scale) {
        try (InputStream is = JsonParser.class.getResourceAsStream(filePath)) {
            JsonNode root = mapper.readTree(is);
            TiledMapData mapData = new TiledMapData();
            mapData.width = root.get("width").asInt();
            mapData.height = root.get("height").asInt();
            mapData.tileWidth = root.get("tilewidth").asInt();
            mapData.tileHeight = root.get("tileheight").asInt();
            mapData.layers = new ArrayList<>();
            mapData.objects = new ArrayList<>();

            // Parse layers
            JsonNode layers = root.get("layers");
            for (JsonNode layer : layers) {
                String type = layer.get("type").asText();
                if ("tilelayer".equals(type)) {
                    LayerData layerData = new LayerData();
                    layerData.name = layer.get("name").asText();
                    layerData.width = layer.get("width").asInt();
                    layerData.height = layer.get("height").asInt();
                    JsonNode data = layer.get("data");
                    layerData.tiles = new int[data.size()];
                    for (int i = 0; i < data.size(); i++) {
                        layerData.tiles[i] = data.get(i).asInt();
                    }
                    mapData.layers.add(layerData);
                } else if ("objectgroup".equals(type)) {
                    // Parse objects
                    JsonNode objects = layer.get("objects");
                    for (JsonNode obj : objects) {
                        ObjectData objectData = new ObjectData();
                        objectData.name = obj.has("name") ? obj.get("name").asText() : "collision_object";
                        objectData.x = (int) (obj.get("x").asDouble() * scale);  // Scale to game pixels
                        objectData.y = (int) (obj.get("y").asDouble() * scale);
                        objectData.width = (int) (obj.get("width").asDouble() * scale);
                        objectData.height = (int) (obj.get("height").asDouble() * scale);
                        mapData.objects.add(objectData);
                    }
                }
            }
            return mapData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}