package parser;

public class SyntacticException extends Exception {
    public SyntacticException(String message) {
        super(message);
    }

    // Nuovo costruttore per il chaining (necessario per pagina 4/5)
    public SyntacticException(String message, Throwable cause) {
        super(message, cause);
    }
}