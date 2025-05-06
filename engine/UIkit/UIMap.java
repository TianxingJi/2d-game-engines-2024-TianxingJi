package engine.UIkit;

import engine.mapgeneration.Tile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Set;

public class UIMap extends UIElement {
    private Tile[][] grid;
    private Set<String> revealedTiles; // To track which tiles have been revealed
    private double tileSize = 5.0; // Size of each tile on the minimap
    private int playerX, playerY; // Player's position on the minimap
    private int visionRadius;

    public UIMap(double x, double y, double width, double height, Tile[][] grid) {
        super(x, y, width, height);
        this.grid = grid;
        this.revealedTiles = new HashSet<>();
    }

    public void updateRevealedTiles(int playerX, int playerY, int visionRadius) {
        this.playerX = playerX;
        this.playerY = playerY;
        this.visionRadius = visionRadius;
        Set<String> newRevealedTiles = new HashSet<>();

        // Iterate through the vision radius to mark visible tiles
        for (int i = -visionRadius; i <= visionRadius; i++) {
            for (int j = -visionRadius; j <= visionRadius; j++) {
                int tileX = playerX + i;
                int tileY = playerY + j;

                // Ensure the tile is within the grid boundaries
                if (tileX >= 0 && tileX < grid[0].length && tileY >= 0 && tileY < grid.length) {
                    newRevealedTiles.add(tileX + "," + tileY);
                }
            }
        }

        // Retain only the tiles that are within the current vision radius
        revealedTiles.retainAll(newRevealedTiles);

        // Add the new visible tiles to the revealed set
        revealedTiles.addAll(newRevealedTiles);
    }


    @Override
    public void onDraw(GraphicsContext g) {
        // Set transparency for the minimap drawing
        g.setGlobalAlpha(0.7);
        for (int i = -visionRadius; i <= visionRadius; i++) {
            for (int j = -visionRadius; j <= visionRadius; j++) {
                int tileX = playerX + i;
                int tileY = playerY + j;

                // Ensure the tile is within the grid boundaries
                if (tileX >= 0 && tileX < grid[0].length && tileY >= 0 && tileY < grid.length) {
                    // Check if the tile has been revealed
                    if (revealedTiles.contains(tileX + "," + tileY)) {
                        Color tileColor = getTerrainColor(tileX, tileY);
                        g.setFill(tileColor);

                        // Draw each tile relative to the player's position on the minimap
                        g.fillRect(
                                this.x + (i + visionRadius) * tileSize,
                                this.y + (j + visionRadius) * tileSize,
                                tileSize,
                                tileSize
                        );
                    }
                }
            }
        }

        // Draw the player as a black dot at the center of the minimap
        g.setFill(Color.BLACK);
        g.fillOval(
                this.x + visionRadius * tileSize - tileSize / 2,
                this.y + visionRadius * tileSize - tileSize / 2,
                tileSize,
                tileSize
        );
        // Reset the transparency to fully opaque for other drawings
        g.setGlobalAlpha(1.0);
    }

    private Color getTerrainColor(int x, int y) {
        Tile tile = grid[y][x];

        // Return the color based on the tile type
        switch (tile.getType()) {
            case START:
                return Color.YELLOW; // Start point as yellow
            case END:
                return Color.PURPLE; // End point as purple
            case BORN:
                return Color.ORANGE; // Born point as orange
            case WALL:
                return Color.BLUE; // Wall as blue
            case ROOM:
                return Color.LIGHTGRAY; // Room as light gray
            case CORRIDOR:
                return Color.BEIGE; // Corridor as beige
            case EMPTY:
            default:
                return Color.DARKGRAY; // Empty or default tiles as dark gray
        }
    }

    public double getTileSize() {
        return tileSize;
    }

    public void setTileSize(double tileSize) {
        this.tileSize = tileSize;
    }
}
