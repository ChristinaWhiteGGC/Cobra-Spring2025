package Models;
public class Weapon extends Artifact {
    private final int strBoost;

    public Weapon(String id, String name, String description, int strBoost) {
        super(id, "weapon", name, description, "+" + strBoost + " STR");
        this.strBoost = strBoost;
    }

    @Override
    public void applyEffects(Player player) {
        player.setStr(player.getStr() + strBoost);
    }

    @Override
    public void removeEffects(Player player) {
        player.setStr(player.getStr() - strBoost);
    }
}