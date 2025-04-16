package Models;
public class Weapon extends Artifact {
    private final int strBoost;

    public Weapon(String id, String name, String description, String effect, String textEffect) {
        super(id, "weapon", name, description, effect, textEffect);
        this.strBoost = Integer.parseInt(effect.replace("STR", ""));
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