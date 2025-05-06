package engine.BT;

import java.util.List;

public abstract class Composite extends Node {
    protected List<Node> children;

    public Composite(List<Node> children) {
        this.children = children;
    }
}
