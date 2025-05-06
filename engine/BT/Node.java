package engine.BT;

public abstract class Node {
    public enum NodeState {
        SUCCESS,
        FAILURE,
        RUNNING
    }

    protected NodeState state = NodeState.RUNNING;

    public abstract NodeState evaluate();

    public void reset() {
        state = NodeState.RUNNING;
    }

    public NodeState getState() {
        return state;
    }
}
