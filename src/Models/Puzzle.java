package Models;

import java.util.List;

public class Puzzle {
    private final String type;
    private final String name;
    private final String description;
    private String rightAnswer;
    private List<String> rightAnswers;
    private String hint;


    public Puzzle(String type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public boolean solve(String answer) {
            return rightAnswer.equals(answer);
    }
    public boolean solve(String answer, int index) {
        return rightAnswers.get(index).equals(answer);
    }
    public boolean solve(Boolean check) {
        return check;
    }

    public void setAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }
    public void setRightAnswers(List<String> rightAnswers) {
        this.rightAnswers = rightAnswers;
    }
    public void addRightAnswer(String answer) {
        this.rightAnswers.add(answer);
    }
    public void setHint(String hint) {
        this.hint = hint;
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
    public String getHint() {
        return hint;
    }
}
