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

    public abstract void execute(Player player, boolean isBlocking);
}

class ImmediateAttack extends Attack {

    public ImmediateAttack(String name, int damage){
        super(name, damage);
    }
    public void execute(Player player, boolean isBlocking) {
        int damage = getDamage();
        if (isBlocking) {
            damage /= 2; // Reduce damage by half if the player is blocking
            System.out.println("The attack is blocked! Damage is reduced.");
        }
        player.setHp(player.getHp() - damage);
    }
}


class DelayedAttack extends Attack {
    private int delayTurns;
    private int remainingDelayTurn;

    public DelayedAttack(String name, int damage, int delayTurns) {
        super(name, damage);
        this.delayTurns = delayTurns;
        this.remainingDelayTurn = delayTurns; // Initialize remainingDelayTurn
    }

    @Override
    public void execute(Player player, boolean isBlocking) {
        if (remainingDelayTurn > 0) {
            System.out.println(name + " will be used after " + remainingDelayTurn + " turns.");
            decrementDelay();
        } else {
            if (isBlocking) {
                player.takeDamage(damage/2);
                System.out.println(player.getName() + " takes " + damage + " damage.");
                remainingDelayTurn = delayTurns; // Reset delay for future use
            }
            else {
                player.takeDamage(damage);
                System.out.println(player.getName() + " takes " + damage + " damage.");
                remainingDelayTurn = delayTurns; // Reset delay for future use
            }
        }
    }

    public void decrementDelay() {
        if (remainingDelayTurn > 0) {
            System.out.println("remaining turns: " + remainingDelayTurn);
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
    public void execute(Player player, boolean isBlocking) {
        System.out.println(name + " deals " + damage + " damage unless " + condition + ".");
        // Implement condition logic here (for simplicity, we'll assume the condition is met)
        player.takeDamage(damage);
    }
}

class DelayedConditionalAttack extends Attack{
    private int delayTurns;
    private int remainingDelayTurn;
    private String condition;

    public DelayedConditionalAttack(String name, int damage, int delayTurns, String condition) {
        super(name, damage);
        this.delayTurns = delayTurns;
        this.remainingDelayTurn = delayTurns;
        this.condition = condition;
    }

    public boolean isFinished() {
        return remainingDelayTurn == 0;
    }
    public void decrementDelay() {}

    @Override
    public void execute(Player player, boolean isBlocking) {
        if (remainingDelayTurn > 0) {
            System.out.println(name + " will be used after " + remainingDelayTurn + " turns unless " + condition + ".");
            remainingDelayTurn--;
        } else {
            if (isBlocking) {
                player.takeDamage(damage / 2);
                System.out.println(player.getName() + " takes " + damage / 2 + " damage.");
            } else {
                player.takeDamage(damage);
                System.out.println(player.getName() + " takes " + damage + " damage.");
            }
            remainingDelayTurn = delayTurns;
        }
    }
}
