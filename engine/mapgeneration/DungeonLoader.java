package engine.mapgeneration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DungeonLoader {
    private Tile[][] grid;
    private int width;
    private int height;

    public DungeonLoader(String filePath) throws IOException {
        loadDungeon(filePath);
    }

    // Load dungeon from a text file
    private void loadDungeon(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        System.out.println("sucessfully loaded dungeon");

        // First, read the entire file into a list of strings (rows of the dungeon)
        String line;
        List<String> rows = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            rows.add(line);
        }
        reader.close();

        // Initialize grid size based on rows
        height = rows.size();
        width = rows.get(0).length();
        grid = new Tile[height][width];

        // Fill the grid with tiles
        for (int y = 0; y < height; y++) {
            String row = rows.get(y);
            for (int x = 0; x < width; x++) {
                char tileChar = row.charAt(x);
                grid[y][x] = charToTile(tileChar); // Convert char to Tile
            }
        }
    }

    // Convert a character from the text file into a Tile object
    private Tile charToTile(char c) {
        switch (c) {
            case '#':
                return new Tile(Tile.TileType.WALL);
            case 'R':
                return new Tile(Tile.TileType.ROOM);
            case 'C':
                return new Tile(Tile.TileType.CORRIDOR);
            case 'E':
                return new Tile(Tile.TileType.END);
            case 'S':
                return new Tile(Tile.TileType.START); //
            case 'B':
                return new Tile(Tile.TileType.BORN);
            case 'A':
                return new Tile(Tile.TileType.AIR);
                case 'D':
                    return new Tile(Tile.TileType.DESTRUCTIBLE);
            default:
                return new Tile(Tile.TileType.EMPTY); // Default empty space
        }
    }

    public Tile[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
