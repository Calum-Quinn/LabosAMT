package ch.heigvd.amt.wiki;

public class HaltProcessingException extends RuntimeException {
    public HaltProcessingException(String message) {
        super(message);
    }
}
