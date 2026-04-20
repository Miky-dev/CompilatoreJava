package test;

import scanner.Scanner;
import parser.Parser;
import ast.NodeProgram; // Aggiungi questo import!
import parser.SyntacticException;
import scanner.LexicalException;

public class TestParser {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner("test_semplice.ac"); // Usa il tuo file di test
            Parser parser = new Parser(scanner);
            
            System.out.println("Inizio parsing e costruzione AST...");
            
            // 1. Catturiamo la radice dell'albero (NodeProgram)
            NodeProgram ast = parser.parse();
            
            System.out.println("Parsing completato con successo!\n");
            
            // 2. Stampiamo l'albero
            System.out.println("--- AST GENERATO ---");
            System.out.println(ast.toString());
            System.out.println("--------------------");

        } catch (SyntacticException e) {
            System.err.println("ERRORE: " + e.getMessage());
            // Se c'è una causa interna (es. l'errore lessicale incapsulato), la stampiamo
            if (e.getCause() != null) {
                System.err.println("Causato da: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}