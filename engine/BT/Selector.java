package engine.BT;

import java.util.List;

public class Selector extends Composite {
    public Selector(List<Node> children) {
        super(children);
    }

    @Override
    public NodeState evaluate() {
        for (Node child : children) {
            NodeState result = child.evaluate();
            if (result == NodeState.SUCCESS) {
                state = NodeState.SUCCESS;
                return state;
            }
        }
        state = NodeState.FAILURE;
        return state;
    }
}
