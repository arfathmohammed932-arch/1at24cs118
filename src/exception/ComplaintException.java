package exception;

public class ComplaintException extends Exception {
    public ComplaintException(String message) {
        super(message);
    }

    public ComplaintException(String message, Throwable cause) {
        super(message, cause);
    }
}
