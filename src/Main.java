import Controllers.GameController;
import Models.GameStateManager;
import Models.Room;
import Views.GameView;

public class Main {
    public static void main(String[] args) {
        try {
            GameStateManager gsm = new GameStateManager();
            GameView view = new GameView();
            GameController gc = new GameController(view, gsm);
            gc.initialize();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
