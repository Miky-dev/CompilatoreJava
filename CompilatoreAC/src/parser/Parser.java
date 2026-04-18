package parser;

import scanner.Scanner;
import scanner.LexicalException;
import token.*;

public class Parser {
    private Scanner scanner;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    // Il cuore del parser: confronta il token attuale con quello atteso
    private Token match(TokenType type) throws LexicalException, SyntacticException {
        Token tk = scanner.peekToken();
        if (type.equals(tk.getType())) {
            return scanner.nextToken(); // Token corretto, lo consumiamo
        } else {
            // Errore: segnaliamo cosa ci aspettavamo e cosa abbiamo trovato
            throw new SyntacticException("Errore sintattico alla riga " + tk.getLine() 
                + ": atteso " + type + ", trovato " + tk.getType());
        }
    }

    // Metodo pubblico per avviare tutto
    public void parse() throws LexicalException, SyntacticException {
        parsePrg();
    }
    
    
    private void parsePrg() throws LexicalException, SyntacticException {
        Token tk = scanner.peekToken();
        switch (tk.getType()) {
            // Predict(Prg -> DSs $)
            case TYFLOAT:
            case TYINT:
            case ID:
            case PRINT:
            case EOF:
                parseDSs();
                match(TokenType.EOF);
                break;
            default:
                throw new SyntacticException("Inizio programma non valido: " + tk.getType());
        }
    }
    
    
    
    private void parseDSs() throws LexicalException, SyntacticException {
        Token tk = scanner.peekToken();
        switch (tk.getType()) {
            case TYFLOAT:
            case TYINT:
                parseDcl();
                parseDSs();
                break;
            case ID:
            case PRINT:
                parseStm();
                parseDSs();
                break;
            case EOF:
                // Produzione DSs -> epsilon (non fa nulla)
                break;
            default:
                throw new SyntacticException("Token inatteso in DSs: " + tk.getType());
        }
    }
    
    
    private void parseTy() throws LexicalException, SyntacticException {
        Token tk = scanner.peekToken();
        switch (tk.getType()) {
            case TYFLOAT:
                match(TokenType.TYFLOAT);
                break;
            case TYINT:
                match(TokenType.TYINT);
                break;
            default:
                throw new SyntacticException("Atteso un tipo (int o float) alla riga " + tk.getLine());
        }
    }
    
    
    
    private void parseDcl() throws LexicalException, SyntacticException {
        parseTy();           // Legge int o float
        match(TokenType.ID); // Legge il nome della variabile
        parseDclP();         // Legge il finale della dichiarazione
    }

    private void parseDclP() throws LexicalException, SyntacticException {
        Token tk = scanner.peekToken();
        switch (tk.getType()) {
            case SEMI: // Produzione 5: DclP -> ;
                match(TokenType.SEMI);
                break;
            case ASSIGN: // Produzione 6: DclP -> = Exp ;
                match(TokenType.ASSIGN);
                parseExp(); // Dobbiamo ancora scrivere questo metodo!
                match(TokenType.SEMI);
                break;
            default:
                throw new SyntacticException("Atteso ';' o '=' dopo l'identificatore alla riga " + tk.getLine());
        }
    }
    
    
    
    private void parseStm() throws LexicalException, SyntacticException {
        Token tk = scanner.peekToken();
        if (tk.getType() == TokenType.PRINT) { // Produzione 8
            match(TokenType.PRINT);
            match(TokenType.ID);
            match(TokenType.SEMI);
        } else if (tk.getType() == TokenType.ID) { // Produzione 7: Stm -> id Op Exp ;
            match(TokenType.ID);
            parseOp();  // Nuovo metodo per = o +=
            parseExp(); // Nuovo metodo per i calcoli
            match(TokenType.SEMI);
        } else {
            throw new SyntacticException("Istruzione non valida alla riga " + tk.getLine());
        }
    }
    
    
    
    
 // Exp -> Tr ExpP
    private void parseExp() throws LexicalException, SyntacticException {
        parseTr();
        parseExpP();
    }

    // ExpP -> + Tr ExpP | - Tr ExpP | epsilon
    private void parseExpP() throws LexicalException, SyntacticException {
        Token tk = scanner.peekToken();
        if (tk.getType() == TokenType.PLUS || tk.getType() == TokenType.MINUS) {
            match(tk.getType()); // Consuma + o -
            parseTr();
            parseExpP();
        } 
        // Caso epsilon: non facciamo nulla, lasciamo che il metodo finisca
    }
    
 // Tr -> Val TrP
    private void parseTr() throws LexicalException, SyntacticException {
        parseVal();
        parseTrP();
    }

    // TrP -> * Val TrP | / Val TrP | epsilon
    private void parseTrP() throws LexicalException, SyntacticException {
        Token tk = scanner.peekToken();
        if (tk.getType() == TokenType.TIMES || tk.getType() == TokenType.DIVIDE) {
            match(tk.getType()); // Consuma * o /
            parseVal();
            parseTrP();
        }
        // Caso epsilon
    }
    
    
 // Val -> intVal | floatVal | id
    private void parseVal() throws LexicalException, SyntacticException {
        Token tk = scanner.peekToken();
        switch (tk.getType()) {
            case INT:
                match(TokenType.INT);
                break;
            case FLOAT:
                match(TokenType.FLOAT);
                break;
            case ID:
                match(TokenType.ID);
                break;
            default:
                throw new SyntacticException("Atteso un valore (numero o variabile) alla riga " + tk.getLine());
        }
    }
    
    
    private void parseOp() throws LexicalException, SyntacticException {
        Token tk = scanner.peekToken();
        if (tk.getType() == TokenType.ASSIGN) {
            match(TokenType.ASSIGN);
        } else if (tk.getType() == TokenType.OP_ASSIGN) {
            match(TokenType.OP_ASSIGN);
        } else {
            throw new SyntacticException("Atteso un operatore di assegnamento alla riga " + tk.getLine());
        }
    }
    
}