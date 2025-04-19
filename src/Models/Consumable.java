package Models;

// Subclass of Artifact for consumable items, now without magical effects.
public class Consumable extends Artifact {
    private int healValue;        // Retained for flavor text, not used.
    private int roomsUntilUsable; // Retained for consistency.
    private static final int COOLDOWN_ROOMS = 5;

    public Consumable(String id, String name, String description, int healValue) {
        super(id, "consumable", name, description, "Grants " + healValue + " stat");
        this.healValue = healValue;
        this.roomsUntilUsable = 0;
    }

    @Override
    public String useItem(Player player) {
        return name + " has no effect when used.";
    }

    @Override
    public void applyEffects(Player player) {
        // No effect on equip
        if (this.getEffect().equals("HPFULL")) {
            player.setHp(player.getBaseHealth());
        } else if (this.getEffect().startsWith("HP")) {
            player.setHp(player.getHp() + Integer.parseInt(this.getEffect().replace("HP", "")));
        } else if (this.getEffect().startsWith("DEF")) {
            player.setDef(player.getDef() + Integer.parseInt(this.getEffect().replace("DEF", "")));
        } else if (this.getEffect().startsWith("STR")) {
            player.setStr(player.getStr() + Integer.parseInt(this.getEffect().replace("STR", "")));
        }
    }

    @Override
    public void removeEffects(Player player) {
        if (this.getEffect().startsWith("DEF")) {
            player.setDef(player.getDef() - Integer.parseInt(this.getEffect().replace("DEF", "")));
        } else if (this.getEffect().startsWith("STR")) {
            player.setStr(player.getStr() - Integer.parseInt(this.getEffect().replace("STR", "")));
        }
    }

    public void decrementCooldown() {
        // No effect
    }

    public int getRoomsUntilUsable() { return roomsUntilUsable; }
    public void setRoomsUntilUsable(int rooms) { this.roomsUntilUsable = rooms; }
}