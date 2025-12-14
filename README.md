# 2D Java Game

A simple 2D tile-based game map made using pure Java.  

## 🖼️ Screenshot / Code Preview

![Screenshot](<img.png>
)


## 💡 How It Works

- **Main.java** → Starts the game window and main loop  
- **GamePanel.java** → Handles drawing, updating, and player input  
- **TileManager.java** → Loads the map from `.txt` files and renders tiles  
- **CollisionChecker.java** → Detects collision between the player and tiles  
- **Entity.java** → Base class for all game objects (player, NPCs, etc.)  
- **ObjectManager.java** → Manages items or obstacles in the map  


## 🕹️ How to Run

1. Open the project in your IDE (Eclipse, IntelliJ, or VS Code)
2. Make sure you have JDK 17 or later installed  
3. Run the `Main.java` file

Or from terminal:
```bash
javac Main.java
java Main

