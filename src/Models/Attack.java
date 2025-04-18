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
    private int delayTurns;

    public DelayedAttack(String name, int damage, int delayTurns) {
        super(name, damage);
        this.delayTurns = delayTurns;
    }

    @Override
    public void execute(Player player) {
        System.out.println(name + " will deal " + damage + " damage after " + delayTurns + " turns.");
        // Delay damage by specified turns (simulated with a thread sleep for simplicity)
        try {
            Thread.sleep(delayTurns * 1000); // 1 second per turn for simplicity
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        player.takeDamage(damage);
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
