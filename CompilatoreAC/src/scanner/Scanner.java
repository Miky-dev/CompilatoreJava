package scanner;

import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.Map;
import token.Token;
import token.TokenType;

public class Scanner {
    private PushbackReader buffer;
    private int riga = 1; // Per sapere sempre a che riga siamo
    
    // Mappe per velocizzare il riconoscimento
    private Map<String, TokenType> keywords = new HashMap<>();
    private Map<Character, TokenType> symbols = new HashMap<>();

    public Scanner(String fileName) throws IOException {
        this.buffer = new PushbackReader(new FileReader(fileName));
        inizializzaMappe();
    }

    private void inizializzaMappe() {
        // Qui riempiremo le mappe con i dati della tabella di pagina 3
    }

    public Token nextToken() throws IOException {
        // Questo sarà il metodo più complesso da scrivere!
        return null; 
    }
}