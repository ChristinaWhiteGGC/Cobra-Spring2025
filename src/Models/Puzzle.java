package Models;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class Puzzle {
    protected String name;
    protected List<String> descriptions;
    protected List<String> hints;
    protected List<Integer> roomNumbers;
    protected boolean isSolved;

    public Puzzle(String name, List<String> descriptions) {
        this.name = name;
        this.descriptions = descriptions;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setHints(List<String> hints) {
        this.hints = hints;
    }

    public List<String> getHints() {
        return hints;
    }

    public void setRoomNumbers(List<Integer> roomNumbers) {
        this.roomNumbers = roomNumbers;
    }

    public List<Integer> getRoomNumbers() {
        return roomNumbers;
    }

    public void setIsSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }

    public boolean getIsSolved() {
        return isSolved;
    }


    public static Map<String, Puzzle> loadPuzzles(String filePath) throws IOException {
        Map<String, Puzzle> puzzleList = new LinkedHashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\|");
            String type = parts[0];
            String name = parts[1];
            List<String> descriptions = List.of(parts[2].split(";"));
            Puzzle puzzle = null;

            switch (type) {
                case "STND" -> {
                    String rightAnswer = parts[3];
                    puzzle = new StandardPuzzle(name, descriptions, rightAnswer);
                }
                case "BOOL" -> {
                    String condition = parts[3];
                    puzzle = new BooleanPuzzle(name, descriptions, condition);
                }
                case "SEQ" -> {
                    String[] answers = parts[3].split(";");
                    List<String> rightAnswers = new ArrayList<>(Arrays.asList(answers));
                    puzzle = new SequencePuzzle(name, descriptions, rightAnswers);
                }
                case "MULTI" -> {
                    List<String> rightAnswers = List.of(parts[3].split(" "));
                    puzzle = new MultiPuzzle(name, descriptions, rightAnswers);
                }
            }
            String[] locations;
            if (parts.length == 5) {
                locations = parts[4].split(",");
            } else {
                locations = parts[3].split(",");
            }
            List<Integer> roomNumbers = new ArrayList<>();
            for (String location : locations) {
                roomNumbers.add(Integer.parseInt(location.trim()));
            }
            assert puzzle != null;
            puzzle.setRoomNumbers(roomNumbers);
            puzzleList.put(name, puzzle);
        }
        br.close();

        List<Puzzle> puzzles = new ArrayList<>(puzzleList.values());

        br = new BufferedReader(new FileReader("src/data/hints.txt"));
        br.readLine();

        int i = 0;
        while ((line = br.readLine()) != null && i < 14) {
            String[] parts = line.split("\\|");
            Puzzle puzzle = puzzles.get(i++);
            puzzle.setHints(Arrays.asList(parts));
        }
        br.close();
        return puzzleList;
    }

    public static class StandardPuzzle extends Puzzle {
        private final String rightAnswer;

        public StandardPuzzle(String name, List<String> descriptions, String rightAnswer) {
            super(name, descriptions);
            this.rightAnswer = rightAnswer;
        }

        public boolean solve(String input) {
            return rightAnswer.equalsIgnoreCase(input);
        }
    }

    public static class BooleanPuzzle extends Puzzle {
        private final String condition;

        public BooleanPuzzle(String name, List<String> descriptions, String condition) {
            super(name, descriptions);
            this.condition = condition;
        }

        public String getCondition() {
            return condition;
        }

        public boolean solve(boolean check) {
            return check;
        }
    }

    public static class SequencePuzzle extends Puzzle {
        private final List<String> rightAnswers;
        private int index = 0;

        public SequencePuzzle(String name, List<String> descriptions, List<String> rightAnswers) {
            super(name, descriptions);
            this.rightAnswers = rightAnswers;
        }

        public boolean solve(String input) {
            if (index >= rightAnswers.size()) {
                return true;
            }

            if (rightAnswers.get(index).equalsIgnoreCase(input)) {
                index++;
                return true;
            }
            return false;
        }

        public String getCurrentDescription() {
            return descriptions.get(index);
        }

        public int getIndex() {
            return index;
        }

        public boolean isComplete() {
            return index >= rightAnswers.size();
        }

        public String getCurrentPrompt() {
            if (index >= rightAnswers.size()) return null;
            return rightAnswers.get(index);
        }

        public List<List<String>> generateColorTiles() {
            Random random = new Random();
            List<List<String>> colorTiles = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                String color1 = rightAnswers.get(random.nextInt(5));
                String color2 = rightAnswers.get(random.nextInt(5));
                while (color2.equals(color1)) {
                    color2 = rightAnswers.get(random.nextInt(5));
                }
                colorTiles.add(Arrays.asList(color1, color1, color2));
                for (List<String> colorGroup : colorTiles) {
                    Collections.shuffle(colorGroup);
                }
            }
            return colorTiles;
        }
    }

    public static class MultiPuzzle extends Puzzle {
        private final List<String> rightAnswers;
        private Map<String, Integer> textToWeight = new HashMap<>();

        public MultiPuzzle(String name, List<String> descriptions, List<String> rightAnswers) {
            super(name, descriptions);
            this.rightAnswers = rightAnswers;
        }

        public List<String> getRightAnswers() {
            return rightAnswers;
        }

        public int sumWeights(List<String> inputs) {
            int sum = 0;
            for (String input : inputs) {
                Integer weight = textToWeight.get(input);
                if (weight == null) return 0;
                sum += weight;
            }
            return sum;
        }

        public boolean solve(int weight) {
            return weight == 100;
        }
        public boolean solve(List<String> torches, List<String> inputs) {
            return torches.equals(inputs);
        }

        public void generateBalancePuzzle() {

            int maxWeight = 100;
            Random random = new Random();
            int weight1 = 100 - random.nextInt(maxWeight); //100 - 50 = 50
            int weight2 = weight1 - random.nextInt(weight1); // 50 - 45 = 5
            int weight3 = 100 - weight1 - weight2; // 100 - 50 - 5 = 45
            int weight4 = random.nextInt(maxWeight);
            int weight5 = random.nextInt(maxWeight);

            if (weight3 < 0) {
                weight3 -= weight3 * 2;
                weight1 -= weight3;
                weight2 -= weight3;
            } else if (weight3 == 0) {
                weight3 += 5;
                weight1 -= 5;
            }

            while (true) {
                if (weight4 == weight1 || weight4 == weight2 || weight4 == weight3) {
                    weight4 = random.nextInt(maxWeight);
                } else if (weight5 == weight1 || weight5 == weight2 || weight5 == weight3) {
                    weight5 = random.nextInt(maxWeight);
                } else {
                    break;
                }
            }

            List<Integer> weights = new ArrayList<>(List.of(weight1, weight2, weight3, weight4, weight5));
            Collections.shuffle(weights);

            int i = 0;
            for (int weight : weights) {
                textToWeight.put(rightAnswers.get(i), weight);
                i++;
            }
        }
    }
}



