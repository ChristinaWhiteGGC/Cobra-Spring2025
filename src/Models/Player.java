package Models;

import java.lang.reflect.Array;
import java.util.*;

/*
    the Models.Player class represents the player of the game and maintains
    their current room and what items they have in their inventory
 */
public class Player extends Character {

   private final HashMap<String, Artifact> inventoryList;

   // Hashmap of active effects via artifacts equipped. Key = Artifact Name, Value = effect from Artifact instance
   private final Map<String, String> artifactEffects = new HashMap<>();

   // Hashmap of currently equipped artifacts by type
   private final Map<String, Artifact> equippedArtifacts = new HashMap<>();

   private final ArrayList<Artifact> keys = new ArrayList<>();
   private int crookOfOsirisUses = 0;

   public int getCrookOfOsirisUses() {
      return crookOfOsirisUses;
   }

   public void incrementCrookOfOsirisUses() {
      crookOfOsirisUses++;
   }

   private boolean isResurrectable = false;

   public Player(String name, int baseHp, int baseStrength, int baseDefense) {
      super(name, baseHp, baseStrength, baseDefense);
      this.inventoryList = new HashMap<>();
      this.setBaseHealth(100);
      this.setBaseStrength(1);
      this.setBaseDefense(1);
   }

   public void clearInventory() {
       inventoryList.clear();
    }

   public Collection<Artifact> getInventory() {
      return inventoryList.values();
   }

   public Artifact getInventoryArtifactByName(String name) {
      for (Artifact artifact : inventoryList.values()) {
         if (artifact.getName().equals(name)) {
            return artifact;
         }
      }
      return null;
   }
   public ArrayList<Artifact> getKeys() {
      return keys;
   }

   public boolean addToInventory(Artifact a) {
      try {
         if (a.getType().equals("key")) {
          this.keys.add(a);
         } else {
            String artifactType = a.getType();
            inventoryList.put(artifactType, a);
         }
         return true;
      } catch (Exception ignored) {
      }
      return false;
   }

   public boolean removeFromInventory(Artifact a) {
      try {
         String artifactType = a.getType();
         if (artifactType.equals("key")) {
            this.keys.add(a);
         } else {
            inventoryList.remove(artifactType);
         }
         return true;
      } catch (Exception ignored) {
      }
      return false;
   }

   public void removeArtifactEffects(Artifact a) {
      artifactEffects.remove(a.getName());
   }

   public Artifact getArtifactByType(String type) {
      for (Artifact a : getInventory()) {
         if (a.getType().equalsIgnoreCase(type)) {
            return a;
         }
      }
      return null;
   }

   @Override
   public int getDef() {
      return this.getBaseDefense() + this.defense;
   }

   @Override
   public int getStr() {
      return this.getBaseStrength() + this.strength;
   }

   @Override
   public void takeDamage(int damage) {
      long damageReduction = Math.round(damage * 0.05) * this.getDef();
      long effectiveDamage = damage - damageReduction;
      if (health < 0){
         health = 0;
      }
      health -= (int) effectiveDamage;
      if (health < 0){
         health = 0;
      }
      System.out.println(name + " has taken " + damage + " damage. " + name + " now has " + health + " health.");
   }

   public void setResurrectable(boolean value) {
      this.isResurrectable = value;
   }
}

