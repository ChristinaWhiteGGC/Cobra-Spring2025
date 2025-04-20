package Models;

public class StandardObject extends Artifact {
    public StandardObject(String id, String name, String description, String effect, String textEffect) {
        super(id, "object", name, description, effect, textEffect);
    }

    @Override
    public void applyEffects(Player player) { }

    @Override
    public void removeEffects(Player player) { }
}
