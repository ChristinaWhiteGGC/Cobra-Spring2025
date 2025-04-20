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
        while ((line = br.readLine()) != null) {
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
    }

    public static class MultiPuzzle extends Puzzle {
        private final List<String> rightAnswers;

        public MultiPuzzle(String name, List<String> descriptions, List<String> rightAnswers) {
            super(name, descriptions);
            this.rightAnswers = rightAnswers;
    }
    public boolean solve(List<String> inputs) {
            return inputs.equals(rightAnswers);
    }
}


}