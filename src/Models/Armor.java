package Models;

public class Armor extends Artifact {
    private int defBoost;

    public Armor(String id, String name, String description, int defBoost) {
        super(id, "armor", name, description, "+" + defBoost + " DEF");
        this.defBoost = defBoost;
    }

    @Override
    public void applyEffects(Player player) {
        player.setDef(player.getDef() + defBoost);
    }

    @Override
    public void removeEffects(Player player) {
        player.setDef(player.getDef() - defBoost);
    }
}