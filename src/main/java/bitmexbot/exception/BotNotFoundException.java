package bitmexbot.exception;

public class BotNotFoundException extends RuntimeException {
    public BotNotFoundException(String message) {
        super(message);
    }
}
