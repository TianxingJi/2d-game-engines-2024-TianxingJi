package engine.pathfinding;

import engine.support.Vec2d;
import java.util.Objects;

public class Node {
    private Vec2d position;  // The position of the node in the grid
    private double gCost;    // Cost from the start node to this node
    private double hCost;    // Heuristic cost from this node to the target
    private Node parent;     // Parent node for path reconstruction

    public Node(Vec2d position) {
        this.position = position;
        this.gCost = Double.POSITIVE_INFINITY; // Initialize with a high value
        this.hCost = 0.0; // Default heuristic cost
    }

    public Vec2d getPosition() {
        return position;
    }

    public double getFCost() {
        return gCost + hCost; // Total cost (F = G + H)
    }

    public double getGCost() {
        return gCost;
    }

    public void setGCost(double gCost) {
        this.gCost = gCost;
    }

    public double getHCost() {
        return hCost;
    }

    public void setHCost(double hCost) {
        this.hCost = hCost;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return position.equals(node.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }
}
