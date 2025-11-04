# 2D Java Game

A simple 2D tile-based game made using pure Java.  
This project uses basic game loops, collision detection, and map loading from text files.


## 🖼️ Screenshot / Code Preview

![Screenshot](<img width="769" height="601" alt="image" src="https://github.com/user-attachments/assets/32676942-a930-41ad-9b73-35b15f13fe8c" />
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

