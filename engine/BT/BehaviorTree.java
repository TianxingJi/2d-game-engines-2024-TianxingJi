package engine.BT;

public class BehaviorTree {
    private Node rootNode;

    public BehaviorTree(Node rootNode) {
        this.rootNode = rootNode;
    }

    public void onTick() {
        if (rootNode != null) {
            rootNode.evaluate();
        }
    }
}
