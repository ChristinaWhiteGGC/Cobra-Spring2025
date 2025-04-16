package Exceptions;

// A custom exception that can be caught, displays a message to the user, and resets state

public class GameException extends Exception {
    public GameException(String message) {
        super(message);
    }
}
