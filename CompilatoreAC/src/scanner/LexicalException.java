package scanner;

public class LexicalException extends Exception {

    // Costruttore senza messaggio
    public LexicalException() {
        super();
    }

    // Costruttore che accetta un messaggio di errore specifico
    public LexicalException(String message) {
        super(message);
    }
}