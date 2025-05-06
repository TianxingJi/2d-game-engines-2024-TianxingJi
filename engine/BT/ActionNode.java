package engine.BT;

public class ActionNode extends Node {
    private Runnable action;

    public ActionNode(Runnable action) {
        this.action = action;
    }

    @Override
    public NodeState evaluate() {
        action.run();
        state = NodeState.SUCCESS;
        return state;
    }
}
