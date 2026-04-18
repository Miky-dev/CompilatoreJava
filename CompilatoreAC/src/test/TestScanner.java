package test;

import scanner.Scanner;
import scanner.LexicalException;
import token.Token;
import token.TokenType;

public class TestScanner {
    public static void main(String[] args) {
        try {
            // Carichiamo il file creato prima
            Scanner scanner = new Scanner("test.ac");
            Token t;
            
            System.out.println("Inizio scansione del file...");
            System.out.println("---------------------------");

            // Leggiamo i token uno alla volta finché non arriviamo alla fine (EOF)
            do {
                t = scanner.nextToken();
                System.out.println(t);
            } while (t.getType() != TokenType.EOF);
            
            System.out.println("---------------------------");
            System.out.println("Scansione completata con successo!");

        } catch (LexicalException e) {
            // Se c'è un errore nel codice (es. un carattere strano) lo vedremo qui
            System.err.println("ERRORE LESSICALE: " + e.getMessage());
        } catch (Exception e) {
            // Per altri errori (file non trovato, ecc.)
            e.printStackTrace();
        }
    }
}