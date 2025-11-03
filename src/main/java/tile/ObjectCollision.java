package tile;

import java.awt.Rectangle;

public class ObjectCollision {
    public String name;
    public int x, y, width, height;
    public Rectangle solidArea;
    public boolean collision = true;

    public ObjectCollision(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.solidArea = new Rectangle(x, y, width, height);
    }
}