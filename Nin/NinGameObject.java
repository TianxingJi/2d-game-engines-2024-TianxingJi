package Nin;

import engine.gameobjects.GameObject;

public class NinGameObject extends GameObject {
    private boolean isGrounded = false;

    public NinGameObject(int id) {
        super(id);
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public void setGrounded(boolean grounded) {
        isGrounded = grounded;
    }
}
