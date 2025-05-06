package engine.BT;

import java.util.List;

public class Sequence extends Composite {
    public Sequence(List<Node> children) {
        super(children);
    }

    @Override
    public NodeState evaluate() {
        for (Node child : children) {
            NodeState result = child.evaluate();
            if (result == NodeState.FAILURE) {
                state = NodeState.FAILURE;
                return state;
            }
        }
        state = NodeState.SUCCESS;
        return state;
    }
}
