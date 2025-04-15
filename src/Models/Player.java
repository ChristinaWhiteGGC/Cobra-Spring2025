package Models;

import Exceptions.GameException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

   public Player(String name, int baseHp, int baseStrength, int baseDefense) {
      super(name, baseHp, baseStrength, baseDefense);
      this.inventoryList = new HashMap<String, Artifact>();
   }
   public void clearInventory() {
       inventoryList.clear();
    }

   public List<Artifact> getInventory() {
      return (List<Artifact>) inventoryList.values();
   }

   public boolean addToInventory(Artifact a) {
      String artifactType = a.getType();
      inventoryList.put(artifactType, a);
      return true;
   }

   public void removeArtifactEffects(Artifact a) {
      artifactEffects.remove(a.getName());
   }

   public Artifact getArtifactByType(String type) {
      return equippedArtifacts.get(type);
   }
}

