package messagesfromserver;

import java.io.Serializable;

public class MessageAboutAttack implements Serializable {
    private String turnName;
    private int attackIndex;

    public MessageAboutAttack(String turnName, int attackIndex) {
        this.turnName = turnName;
        this.attackIndex = attackIndex;
    }

    public String getTurnName() {
        return turnName;
    }

    public int getAttackIndex() {
        return attackIndex;
    }
}
