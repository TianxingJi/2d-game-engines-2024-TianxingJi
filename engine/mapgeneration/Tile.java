package engine.mapgeneration;

public class Tile {
    public enum TileType {
        START, END, BORN, WALL, ROOM, CORRIDOR, AIR, EMPTY, DESTRUCTIBLE
    }

    private TileType type;

    public Tile(TileType type) {
        this.type = type;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }
}

