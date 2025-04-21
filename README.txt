ReadMe

When starting the program it will attempt to read four data files
"Rooms.txt", "Puzzles.txt", "Monsters.txt" and "Items.txt" from the src directory in the data package.
The information in the files are separated by the tilde [~] character.

Rooms.txt includes:
 - room ID
 - boolean flag to keep track if the room has been previously visited
 - room name
 - room description
 - connections to other rooms based on n,e,s,w and the connected room ID; 0 is used if no connection
 - LockCondition; used to lock rooms and number of keys needed to open room
 - ArtifactID; 0 used for random item, artifact ID for static item, -1 for no item
 - LootID; 0 used for random loot, artifact ID for static loot, -1 for no loot
 - boolean flag for if room HasMonster

Puzzles.txt includes:
 - puzzle name
 - puzzle description
 - puzzle answer
 - number of attempts to solve puzzle
 - connection to room based on room ID

Monster.txt includes:
 - Monster name
 - monster description
 - monster health
 - monster attack damage
 - connection to room based on room ID

 Items.txt includes:
  - item ID
  - item name
  - item description

After reading and parsing the data files, the game will start and the user will be placed in the
first room and given the room description.

See User Manual for instructions on how to play the game.