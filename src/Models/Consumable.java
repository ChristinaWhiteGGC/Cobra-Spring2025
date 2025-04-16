package Models;

// Subclass of Artifact representing consumable items.
// Consumables can be used to apply effects (e.g., heal HP) with a cooldown.
public class Consumable extends Artifact {
    // The amount of HP this consumable heals when used.
    private int healValue;
    // Tracks the number of new rooms the player must visit before using this consumable again.
    private int roomsUntilUsable;
    // Constant defining the cooldown period (5 new rooms).
    private static final int COOLDOWN_ROOMS = 5;

    // Constructor to initialize a consumable artifact.
    public Consumable(String id, String name, String description, String effect, String textEffect) {
        super(id, "consumable", name, description, effect, textEffect);
        String effectValue = effect.replace("HP", "");
        if (!effectValue.equals("FULL")) {
            this.healValue = Integer.parseInt(effect.replace("HP", ""));
        }
        this.roomsUntilUsable = 0;
    }

    // FR18: Handles the "Use Item" command for consumables.
    // Heals the player if the cooldown is zero; otherwise, shows remaining cooldown.
    @Override
    public String useItem(Player player) {
        if (roomsUntilUsable > 0) {
            return getName() + " is on cooldown. Visit " + roomsUntilUsable + " more new rooms.";
        }
        player.setHp(player.getHp() + healValue);
        roomsUntilUsable = COOLDOWN_ROOMS;
        return "Used " + getName() + ". Restored " + healValue + " HP.";
    }

    // Consumables don’t apply effects on equip (only on use).
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

    // Consumables don’t have persistent effects to remove.
    @Override
    public void removeEffects(Player player) {
        if (this.getEffect().startsWith("DEF")) {
            player.setDef(player.getDef() - Integer.parseInt(this.getEffect().replace("DEF", "")));
        } else if (this.getEffect().startsWith("STR")) {
            player.setStr(player.getStr() - Integer.parseInt(this.getEffect().replace("STR", "")));
        }
    }

    // Decrements the cooldown when the player visits a new room.
    public void decrementCooldown() {
        if (roomsUntilUsable > 0) {
            roomsUntilUsable--;
        }
    }

    // Getter for the remaining cooldown.
    public int getRoomsUntilUsable() {
        return roomsUntilUsable;
    }

    // Setter for the cooldown (used during save/load).
    public void setRoomsUntilUsable(int rooms) {
        this.roomsUntilUsable = rooms;
    }
}