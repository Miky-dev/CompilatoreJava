package token;

public class Token {
	
	
	//	DEFINIZIONE DEI CAMPI
	private TokenType type; // Il tipo del token (es. INT, ID)
    private int line;       // La riga del codice sorgente
    private String value;   // La stringa specifica letta (opzionale)
    
    
    //	IMPLEMENTAZIONE DEI COSTRUTTORI
    // Costruttore per token con valore (es. ID, INT, FLOAT)
    public Token(TokenType type, int line, String value) {
        this.type = type;
        this.line = line;
        this.value = value;
    }

    // Costruttore per token senza valore (es. PLUS, SEMI, TYINT)
    public Token(TokenType type, int line) {
        this(type, line, null);
    }
    
    
    
    //	METODO toString
    @Override
    public String toString() {
        if (value != null) {
            return "<" + type + ",r:" + line + "," + value + ">";
        } else {
            return "<" + type + ",r:" + line + ">";
        }
    }
    

    
    //	METODI GETTERS
    public TokenType getType() { return type; }
    public int getLine() { return line; }
    public String getValue() { return value; }
    
}
