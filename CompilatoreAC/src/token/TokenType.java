package token;

public enum TokenType {
	// Letterali e Identificatori
    INT, FLOAT, ID, 
    
    // Parole Chiave
    TYINT, TYFLOAT, PRINT, 
    
    // Operatori e Assegnamento
    OP_ASSIGN, ASSIGN, PLUS, MINUS, TIMES, DIVIDE, 
    
    // Delimitatori e Fine Input
    SEMI, EOF
}
