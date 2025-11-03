package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.Jpanel;
import Main.KeyHandler;

public class Player extends Entity{
	
	Jpanel gp;
	KeyHandler keyH;
	
	public final int screenX;
	public final int screenY;
	
	
	public Player (Jpanel gp, KeyHandler keyH) {
	    this.gp = gp;
	    this.keyH = keyH;
	    
	    solidArea = new Rectangle(8, 16, 32, 32);
	    
	    screenX = gp.screenWidth/2 - (gp.tileSize/2);
	    screenY = gp.screenHeight/2 - (gp.tileSize/2);
	    
	    setDefaultValues();
	    getPlayerImage();
	}
	public void setDefaultValues() {
	    worldX =  478;  // 384 (consistent with scaled world)
	    worldY =  710;  // 432

		speed = 4;
		direction = "down";
	}
	
	public void getPlayerImage() {	
		try {
			
			up1 = ImageIO.read(getClass().getResourceAsStream("/player/up1.png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/player/up2.png"));
			down1 = ImageIO.read(getClass().getResourceAsStream("/player/down1.png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/player/down2.png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/player/left1.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/player/left2.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/player/right1.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/player/right2.png"));
			
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
	    if(keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {
	        
	        if(keyH.upPressed == true) {
	            direction = "up";	
	        } else if(keyH.downPressed == true) {	
	            direction = "down";
	        } else if(keyH.leftPressed == true) {
	            direction = "left";
	        } else if(keyH.rightPressed == true) {
	            direction = "right";
	        }
	        
	        
	        
	        // Check collision BEFORE moving
	        gp.cChecker.checkTile(this);
	        
	        System.out.println("Moving " + direction + ", collision: " + collisionOn);
	        
	        if(collisionOn == false) {
	            switch(direction) {
	            case "up":
	                worldY = worldY - speed;
	                break;
	            case "down":
	                worldY = worldY + speed;
	                break;
	            case "left":
	                worldX = worldX - speed;
	                break;
	            case "right":
	                worldX = worldX + speed;
	                break;
	            }
	        } else {
	            System.out.println("COLLISION DETECTED - Movement blocked");
	        }
	        
	        spriteCounter++;
	        if(spriteCounter > 15) {
	            spriteNum = (spriteNum == 1) ? 2 : 1;
	            spriteCounter = 0;
	        }
	    }
	}
	
	
	public void draw(Graphics2D g2) {
		
		BufferedImage image = null; 
		
		switch(direction) {
		case "up":
			if(spriteNum == 1) {
				image = up1;
			}
			if(spriteNum == 2) {
				image = up2;
			}
			break;
		case "down":
			if(spriteNum == 1) {
				image = down1;;
			}
			if(spriteNum == 2) {
				image = down2;;
			}
			break;
		case "left":
			if(spriteNum == 1) {
				image = left1;
			}
			if(spriteNum == 2) {
				image = left2;
			}
			break;
		case "right":
			if(spriteNum == 1) {
				image = right1;
			}
			if(spriteNum == 2) {
				image = right2;
			}
			break;
		}
		g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
	}
}