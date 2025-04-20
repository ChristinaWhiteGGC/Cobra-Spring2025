package Models;
public abstract class Attack {
    protected String name;
    protected int damage;

    public Attack(String name, int damage) {
        this.name = name;
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public abstract void execute(Player player);
}

class ImmediateAttack extends Attack {
    public ImmediateAttack(String name, int damage) {
        super(name, damage);
    }

    @Override
    public void execute(Player player) {
        System.out.println(name + " deals " + damage + " damage.");
        player.takeDamage(damage);
    }
}

class DelayedAttack extends Attack {
    private final int delayTurns;
    private int remainingDelayTurn;

    public DelayedAttack(String name, int damage, int delayTurns) {
        super(name, damage);
        this.delayTurns = delayTurns;
        this.remainingDelayTurn = delayTurns;
    }

    public int getRemainingDelayTurn() {
        return remainingDelayTurn;
    }

    public void setRemainingDelayTurn(int remainingDelayTurn) {
        this.remainingDelayTurn = remainingDelayTurn;
    }

    @Override
    public void execute(Player player) {
        System.out.println(name + " will deal " + damage + " damage after " + delayTurns + " turns.");
        if (remainingDelayTurn > 0){
            System.out.println(name + " will be used after " + remainingDelayTurn + " turns.");
            remainingDelayTurn--;
        }
        else{
            player.takeDamage(damage);
            System.out.println(remainingDelayTurn);
            System.out.println(player + " takes " + damage + " damage.");
            remainingDelayTurn = delayTurns;
        }
    }

    public boolean delayIsReady(){
        return delayTurns == 0;
    }

    public void decrementDelay(){
        if (remainingDelayTurn > 0) {
            remainingDelayTurn--;
        }
    }
}

class ConditionalAttack extends Attack {
    private String condition;

    public ConditionalAttack(String name, int damage, String condition) {
        super(name, damage);
        this.condition = condition;
    }

    @Override
    public void execute(Player player) {
        System.out.println(name + " deals " + damage + " damage unless " + condition + ".");
        // Implement condition logic here (for simplicity, we'll assume the condition is met)
        player.takeDamage(damage);
    }
}
