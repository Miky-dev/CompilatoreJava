package parser;

import scanner.Scanner;
import scanner.LexicalException;
import token.*;
import java.util.ArrayList;
import ast.*;

public class Parser {
    private Scanner scanner;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    // Il cuore del parser: confronta il token attuale con quello atteso
    private Token match(TokenType type) throws  SyntacticException {
    	try {
	        Token tk = scanner.peekToken();
	        if (type.equals(tk.getType())) {
	            return scanner.nextToken(); // Token corretto, lo consumiamo
	        } else {
	            // Errore: segnaliamo cosa ci aspettavamo e cosa abbiamo trovato
	            throw new SyntacticException("Errore sintattico alla riga " + tk.getLine() 
	                + ": atteso " + type + ", trovato " + tk.getType());
	        }
    	} catch (LexicalException e) {
            // Incapsuliamo l'errore lessicale dentro una SyntacticException (Chaining)
            throw new SyntacticException("Errore Lessicale durante il match", e);
        }
    }

    // Metodo pubblico per avviare tutto
    public NodeProgram parse() throws SyntacticException {
        return parsePrg();
    }

    private NodeProgram parsePrg() throws SyntacticException {
        try {
            Token tk = scanner.peekToken();
            switch (tk.getType()) {
                case TYFLOAT:
                case TYINT:
                case ID:
                case PRINT:
                case EOF:
                    ArrayList<NodeDecSt> decs = parseDSs(); // Raccoglie tutte le dichiarazioni e istruzioni
                    match(TokenType.EOF);
                    return new NodeProgram(decs); // Crea e restituisce il nodo radice
                default:
                    throw new SyntacticException("Inizio programma non valido: " + tk.getType());
            }
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale in parsePrg", e);
        }
    }
    
    
    
    private ArrayList<NodeDecSt> parseDSs() throws SyntacticException {
        ArrayList<NodeDecSt> list = new ArrayList<>();
        try {
            Token tk = scanner.peekToken();
            switch (tk.getType()) {
                case TYFLOAT:
                case TYINT:
                    list.add(parseDcl()); // Aggiunge la dichiarazione alla lista
                    list.addAll(parseDSs()); // Chiamata ricorsiva per il resto
                    break;
                case ID:
                case PRINT:
                    list.add(parseStm()); // Aggiunge l'istruzione alla lista
                    list.addAll(parseDSs()); // Chiamata ricorsiva per il resto
                    break;
                case EOF:
                    // Produzione DSs -> epsilon (non fa nulla, fine della lista)
                    break;
                default:
                    throw new SyntacticException("Token inatteso in DSs: " + tk.getType());
            }
            return list;
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale in parseDSs", e);
        }
    }
    
    
    private LangType parseTy() throws SyntacticException {
        try {
            Token tk = scanner.peekToken();
            switch (tk.getType()) {
                case TYFLOAT:
                    match(TokenType.TYFLOAT);
                    return LangType.FLOAT;
                case TYINT:
                    match(TokenType.TYINT);
                    return LangType.INT;
                default:
                    throw new SyntacticException("Atteso un tipo (int o float) alla riga " + tk.getLine());
            }
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale in parseTy", e);
        }
    }

    private NodeDecl parseDcl() throws SyntacticException {
        LangType type = parseTy();
        Token idToken = match(TokenType.ID); // Salviamo il token consumato
        NodeId id = new NodeId(idToken.getValue()); // Ne estraiamo la stringa (il nome della variabile)
        NodeExpr init = parseDclP();
        
        return new NodeDecl(id, type, init);
    }

    private NodeExpr parseDclP() throws SyntacticException {
        try {
            Token tk = scanner.peekToken();
            switch (tk.getType()) {
                case SEMI:
                    match(TokenType.SEMI);
                    return null;
                case ASSIGN:
                    match(TokenType.ASSIGN);
                    NodeExpr exp = parseExp(); // ORA CALCOLIAMO!
                    match(TokenType.SEMI);
                    return exp;
                default:
                    throw new SyntacticException("Atteso ';' o '=' alla riga " + tk.getLine());
            }
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale in parseDclP", e);
        }
    }
    
    
    
    private NodeStm parseStm() throws SyntacticException {
        try {
            Token tk = scanner.peekToken();
            if (tk.getType() == TokenType.PRINT) {
                match(TokenType.PRINT);
                Token idTk = match(TokenType.ID);
                match(TokenType.SEMI);
                return new NodePrint(new NodeId(idTk.getValue()));
            } else if (tk.getType() == TokenType.ID) {
                Token idTk = match(TokenType.ID);
                NodeId idNode = new NodeId(idTk.getValue());
                
                LangOper op = parseOp(); // Controlliamo se è = o +=
                NodeExpr rightExpr = parseExp(); // Calcoliamo l'espressione a destra
                match(TokenType.SEMI);
                
                if (op == null) {
                    // Caso: x = 5;
                    return new NodeAssign(idNode, rightExpr);
                } else {
                    // Caso: x += 5; (Diventa: x = x + 5)
                    NodeDeref deref = new NodeDeref(idNode); // Leggiamo il valore vecchio di x
                    NodeBinOp binOp = new NodeBinOp(op, deref, rightExpr); // Facciamo x + 5
                    return new NodeAssign(idNode, binOp); // Lo riassegniamo a x
                }
            } else {
                throw new SyntacticException("Istruzione non valida alla riga " + tk.getLine());
            }
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale in parseStm", e);
        }
    }
    
    
    
    
 // Exp -> Tr ExpP
    private NodeExpr parseExp() throws SyntacticException {
        NodeExpr tr = parseTr();
        return parseExpP(tr); // Passiamo il termine come "figlio sinistro"
    }

    private NodeExpr parseExpP(NodeExpr left) throws SyntacticException {
        try {
            Token tk = scanner.peekToken();
            if (tk.getType() == TokenType.PLUS || tk.getType() == TokenType.MINUS) {
                LangOper op = (tk.getType() == TokenType.PLUS) ? LangOper.PLUS : LangOper.MINUS;
                match(tk.getType());
                NodeExpr right = parseTr(); // Il prossimo termine
                NodeBinOp binOp = new NodeBinOp(op, left, right); // Uniamo sinistro e destro
                return parseExpP(binOp); // Ricorsione per calcoli multipli (es. 5 + 2 - 3)
            }
            return left; // Caso epsilon
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale in parseExpP", e);
        }
    }
    
 // Tr -> Val TrP
    private NodeExpr parseTr() throws SyntacticException {
        NodeExpr val = parseVal();
        return parseTrP(val); // Passiamo il valore come "figlio sinistro"
    }

    private NodeExpr parseTrP(NodeExpr left) throws SyntacticException {
        try {
            Token tk = scanner.peekToken();
            if (tk.getType() == TokenType.TIMES || tk.getType() == TokenType.DIVIDE) {
                LangOper op = (tk.getType() == TokenType.TIMES) ? LangOper.TIMES : LangOper.DIV;
                match(tk.getType());
                NodeExpr right = parseVal(); // Il prossimo numero
                NodeBinOp binOp = new NodeBinOp(op, left, right); // Uniamo sinistro e destro
                return parseTrP(binOp); // Ricorsione per calcoli multipli (es. 5 * 2 * 3)
            }
            return left; // Caso epsilon: non ci sono moltiplicazioni, ritorniamo solo il nodo sinistro
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale in parseTrP", e);
        }
    }
    
    
 // Val -> intVal | floatVal | id
    private NodeExpr parseVal() throws SyntacticException {
        try {
            Token tk = scanner.peekToken();
            switch (tk.getType()) {
                case INT:
                    Token intTk = match(TokenType.INT);
                    return new NodeCost(intTk.getValue(), LangType.INT);
                case FLOAT:
                    Token floatTk = match(TokenType.FLOAT);
                    return new NodeCost(floatTk.getValue(), LangType.FLOAT);
                case ID:
                    Token idTk = match(TokenType.ID);
                    return new NodeDeref(new NodeId(idTk.getValue())); // Deref = leggo il valore della variabile
                default:
                    throw new SyntacticException("Atteso un valore (numero o variabile) alla riga " + tk.getLine());
            }
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale in parseVal", e);
        }
    }
    
    
    private LangOper parseOp() throws SyntacticException {
        try {
            Token tk = scanner.peekToken();
            if (tk.getType() == TokenType.ASSIGN) {
                match(TokenType.ASSIGN);
                return null; // Assegnamento semplice
            } else if (tk.getType() == TokenType.OP_ASSIGN) {
                Token opTk = match(TokenType.OP_ASSIGN);
                String val = opTk.getValue();
                // Capiamo quale operazione nasconde (es: "+=")
                if (val != null && val.contains("+")) return LangOper.PLUS;
                if (val != null && val.contains("-")) return LangOper.MINUS;
                if (val != null && val.contains("*")) return LangOper.TIMES;
                if (val != null && val.contains("/")) return LangOper.DIV;
                return LangOper.PLUS; // Fallback
            } else {
                throw new SyntacticException("Atteso '=' o operatore combinato alla riga " + tk.getLine());
            }
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale in parseOp", e);
        }
    }
    
}