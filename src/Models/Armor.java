package Models;

public class Armor extends Artifact {
    private int defBoost;

    public Armor(String id, String name, String description, String effect, String textEffect) {
        super(id, "armor", name, description, effect, textEffect);
        this.defBoost = Integer.parseInt(effect.replace("DEF", ""));
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