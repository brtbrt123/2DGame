package Main;

import Entity.Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import tile.ObjectCollision;

public class CollisionChecker {
    
    Jpanel gp;
    
    public CollisionChecker(Jpanel gp) {
        this.gp = gp;
    }
    
    public void checkTile(Entity entity) {
        
        entity.collisionOn = false;
        
       
        checkTileCollision(entity);
        
       
        checkObjectCollision(entity);
    }
    
    public void checkTileCollision(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
        
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;
        
        int tileNum1, tileNum2;
        
        switch(entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                if (entityTopRow < 0) {
                    entity.collisionOn = true;
                    break;
                }
                tileNum1 = gp.tileM.mapTileNum[0][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[0][entityRightCol][entityTopRow];
                if((tileNum1 != 0 && gp.tileM.tile[tileNum1 - 1].collision) || 
                   (tileNum2 != 0 && gp.tileM.tile[tileNum2 - 1].collision)) {
                    entity.collisionOn = true;
                }
                break;
                
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                if (entityBottomRow >= gp.maxWorldRow) {
                    entity.collisionOn = true;
                    break;
                }
                tileNum1 = gp.tileM.mapTileNum[0][entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[0][entityRightCol][entityBottomRow];
                if((tileNum1 != 0 && gp.tileM.tile[tileNum1 - 1].collision) || 
                   (tileNum2 != 0 && gp.tileM.tile[tileNum2 - 1].collision)) {
                    entity.collisionOn = true;
                }
                break;
                
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                if (entityLeftCol < 0) {
                    entity.collisionOn = true;
                    break;
                }
                tileNum1 = gp.tileM.mapTileNum[0][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[0][entityLeftCol][entityBottomRow];
                if((tileNum1 != 0 && gp.tileM.tile[tileNum1 - 1].collision) || 
                   (tileNum2 != 0 && gp.tileM.tile[tileNum2 - 1].collision)) {
                    entity.collisionOn = true;
                }
                break;
                
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                if (entityRightCol >= gp.maxWorldCol) {
                    entity.collisionOn = true;
                    break;
                }
                tileNum1 = gp.tileM.mapTileNum[0][entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[0][entityRightCol][entityBottomRow];
                if((tileNum1 != 0 && gp.tileM.tile[tileNum1 - 1].collision) || 
                   (tileNum2 != 0 && gp.tileM.tile[tileNum2 - 1].collision)) {
                    entity.collisionOn = true;
                }
                break;
        }
    }
    
    public void checkObjectCollision(Entity entity) {
        // Calculate entity position in world coordinates (original scale)
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
        
        // Calculate future position
        switch(entity.direction) {
            case "up": 
                entityTopWorldY -= entity.speed;
                entityBottomWorldY -= entity.speed;
                break;
            case "down": 
                entityTopWorldY += entity.speed;
                entityBottomWorldY += entity.speed;
                break;
            case "left": 
                entityLeftWorldX -= entity.speed;
                entityRightWorldX -= entity.speed;
                break;
            case "right": 
                entityLeftWorldX += entity.speed;
                entityRightWorldX += entity.speed;
                break;
        }
        
        Rectangle futureSolidArea = new Rectangle(
            entityLeftWorldX, entityTopWorldY,
            entityRightWorldX - entityLeftWorldX,
            entityBottomWorldY - entityTopWorldY
        );
        
        // Check against all objects
        for (ObjectCollision obj : gp.tileM.objectCollisions) {
            if (futureSolidArea.intersects(obj.solidArea)) {
                entity.collisionOn = true;
                System.out.println("Collision with: " + obj.name + 
                               " Entity: " + futureSolidArea + 
                               " Object: " + obj.solidArea);
                break;
            }
        }
    }
    
    public void debugPositions() {
        System.out.println("=== POSITION DEBUG ===");
        
        
        int playerWorldX = gp.player.worldX;
        int playerWorldY = gp.player.worldY;
        Rectangle playerSolidArea = new Rectangle(
            playerWorldX + gp.player.solidArea.x,
            playerWorldY + gp.player.solidArea.y,
            gp.player.solidArea.width,
            gp.player.solidArea.height
        );
        
        System.out.println("Player world position: " + playerWorldX + ", " + playerWorldY);
        System.out.println("Player collision area: " + playerSolidArea);
        
        
        for (int i = 0; i < gp.tileM.objectCollisions.size(); i++) {
            ObjectCollision obj = gp.tileM.objectCollisions.get(i);
            System.out.println("Object " + i + " (" + obj.name + "): " + 
                             obj.x + ", " + obj.y + " size: " + obj.width + "x" + obj.height);
            
           
            if (Math.abs(playerWorldX - obj.x) < 200 && Math.abs(playerWorldY - obj.y) < 200) {
                System.out.println("  Player is near this object!");
                
                
                if (playerSolidArea.intersects(obj.solidArea)) {
                    System.out.println("  *** COLLISION DETECTED! ***");
                }
            }
        }
    }
    
    
   
    public void drawCollisionAreas(Graphics2D g2) {
        // Convert world coordinates to screen coordinates for drawing
        int playerScreenX = gp.player.screenX;
        int playerScreenY = gp.player.screenY;
        int playerWorldX = gp.player.worldX;
        int playerWorldY = gp.player.worldY;
        
        // Draw object collision areas in RED
        g2.setColor(Color.RED);
        for (ObjectCollision obj : gp.tileM.objectCollisions) {
            // Convert object world coordinates to screen coordinates
            int screenX = obj.x - playerWorldX + playerScreenX;
            int screenY = obj.y - playerWorldY + playerScreenY;
            g2.drawRect(screenX, screenY, obj.width, obj.height);
            
            // Add object name
            g2.setColor(Color.WHITE);
            g2.drawString(obj.name, screenX, screenY - 5);
            g2.setColor(Color.RED);
        }
        
        // Draw current player collision area in GREEN
        g2.setColor(Color.GREEN);
        int playerCollisionScreenX = playerScreenX + gp.player.solidArea.x;
        int playerCollisionScreenY = playerScreenY + gp.player.solidArea.y;
        g2.drawRect(playerCollisionScreenX, playerCollisionScreenY, 
                   gp.player.solidArea.width, gp.player.solidArea.height);
        
        // Draw player position text
        g2.setColor(Color.YELLOW);
        g2.drawString("Player: " + playerWorldX + "," + playerWorldY, 10, 20);
        g2.drawString("Collision: " + gp.player.collisionOn, 10, 40);
    }
}