package test;

import scanner.Scanner;
import parser.Parser;
import parser.SyntacticException;
import scanner.LexicalException;

public class TestParser {
    public static void main(String[] args) {
        try {
            // 1. Inizializziamo lo scanner con il file semplice
            Scanner scanner = new Scanner("test_semplice.ac");
            
            // 2. Creiamo il parser passandogli lo scanner
            Parser parser = new Parser(scanner);
            
            System.out.println("Inizio parsing...");
            
            // 3. Avviamo il parsing
            parser.parse();
            
            System.out.println("Parsing completato con successo! Il file è sintatticamente corretto.");

        } catch (LexicalException e) {
            System.err.println("ERRORE LESSICALE: " + e.getMessage());
        } catch (SyntacticException e) {
            System.err.println("ERRORE SINTATTICO: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}