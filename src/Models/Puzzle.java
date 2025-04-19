package Models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Puzzle {
    private final String type;
    private final String name;
    private final String description;
    private String rightAnswer;
    private List<String> rightAnswers;
    private List<String> hints;

    private boolean isSolved;


    public Puzzle(String type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public boolean solve(String answer) {
        isSolved = rightAnswer.equals(answer);
        return isSolved;
    }

    public boolean solve(String answer, int index) {
        isSolved = rightAnswers.get(index).equals(answer);
        return isSolved;
    }

    public boolean getIsSolved() {
        return isSolved;
    }

    public void setIsSolved(boolean value) {
        isSolved = value;
    }

    public boolean solve(Boolean check) {
        return check;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public void setRightAnswers(List<String> rightAnswers) {
        this.rightAnswers = rightAnswers;
    }

    public void addRightAnswer(String answer) {
        this.rightAnswers.add(answer);
    }

    public void setHints(List<String> hints) {
        this.hints = hints;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public List<String> getRightAnswers() {
        return rightAnswers;
    }

    public List<String> getHints() {
        return hints;
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
            String description = parts[2];
            Puzzle puzzle = new Puzzle(type, name, description);
            switch (type) {
                case "STND" -> puzzle.setRightAnswer(parts[3]);
                case "SEQ", "MULTI" -> puzzle.setRightAnswers(Arrays.asList(parts[3].split(",")));
            }
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
}