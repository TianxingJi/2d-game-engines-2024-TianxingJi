package engine.BT;

import java.util.function.BooleanSupplier;

public class ConditionNode extends Node {
    private BooleanSupplier condition;

    public ConditionNode(BooleanSupplier condition) {
        this.condition = condition;
    }

    @Override
    public NodeState evaluate() {
        state = condition.getAsBoolean() ? NodeState.SUCCESS : NodeState.FAILURE;
        return state;
    }
}
