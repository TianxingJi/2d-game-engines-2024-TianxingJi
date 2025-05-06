package engine.pathfinding;

import engine.mapgeneration.Tile;
import engine.support.Vec2d;

import java.util.*;

public class AStarPathfinder {
    private Tile[][] grid; // The game world grid
    private int width, height;
    private Random random = new Random();

    public AStarPathfinder(Tile[][] grid) {
        this.grid = grid;
        this.height = grid.length;
        this.width = grid[0].length;
    }

    public List<Vec2d> findPath(Vec2d start, Vec2d end) {
        Node startNode = new Node(start);
        Node endNode = new Node(end);
        startNode.setGCost(0); // Start node has a G-cost of 0

        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(Node::getFCost));
        Set<Node> closedSet = new HashSet<>();
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            // If we reached the end node, reconstruct the path
            if (currentNode.getPosition().equals(endNode.getPosition())) {
                return reconstructPath(currentNode);
            }

            closedSet.add(currentNode);

            for (Node neighbor : getNeighbors(currentNode)) {
                if (closedSet.contains(neighbor) || !isWalkable(neighbor.getPosition())) {
                    continue; // Skip nodes that are already evaluated or non-walkable
                }

                double tentativeGCost = currentNode.getGCost() + getDistance(currentNode, neighbor);

                // Check if this path is better or the neighbor is not yet in the open list
                if (tentativeGCost < neighbor.getGCost() || !openList.contains(neighbor)) {
                    neighbor.setGCost(tentativeGCost);
                    neighbor.setHCost(getDistance(neighbor, endNode));
                    neighbor.setParent(currentNode);

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // Return an empty path if no path is found
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        Vec2d[] directions = {
                new Vec2d(0, -1),  // Up
                new Vec2d(0, 1),   // Down
                new Vec2d(-1, 0),  // Left
                new Vec2d(1, 0)    // Right
        };

        for (Vec2d direction : directions) {
            Vec2d neighborPos = node.getPosition().plus(direction);
            if (isInBounds(neighborPos) && isWalkable(neighborPos)) {
                neighbors.add(new Node(neighborPos));
            }
        }

        return neighbors;
    }

    // Method to find a random reachable point in the grid
    public Vec2d findRandomReachablePoint() {
        Vec2d randomPoint;

        // Loop until a walkable point is found
        do {
            // Generate random coordinates within the map bounds
            int x = random.nextInt(width); // Ensure within map width
            int y = random.nextInt(height); // Ensure within map height

            randomPoint = new Vec2d(x, y);
        } while (!isWalkable(randomPoint)); // Ensure the point is walkable

        return randomPoint;
    }

    public boolean isWalkable(Vec2d position) {
        int x = (int) position.x;
        int y = (int) position.y;

        // Ensure we are within bounds before checking the tile type
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        Tile tile = grid[y][x];

        // Make both WALL and END tiles impassable
        return tile.getType() != Tile.TileType.WALL && tile.getType() != Tile.TileType.END;
    }

    private boolean isInBounds(Vec2d position) {
        return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height;
    }

    private double getDistance(Node a, Node b) {
        // Manhattan distance for grid-based pathfinding
        return Math.abs(a.getPosition().x - b.getPosition().x) + Math.abs(a.getPosition().y - b.getPosition().y);
    }

    private List<Vec2d> reconstructPath(Node endNode) {
        List<Vec2d> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != null) {
            path.add(currentNode.getPosition());
            currentNode = currentNode.getParent();
        }

        Collections.reverse(path); // Reverse the path to get it from start to end
        return path;
    }

}
