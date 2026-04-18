package scanner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import token.*;

public class Scanner {
	final char EOF = (char) -1; 
	private int riga;
	private PushbackReader buffer;

	// Insiemi di caratteri
	private Set<Character> skipChars;
	private Set<Character> letters;
	private Set<Character> digits;

	// Mappe per i Token
	private Map<Character, TokenType> operTkType;
	private Map<Character, TokenType> delimTkType;
	private Map<String, TokenType> keyWordsTkType;

	public Scanner(String fileName) throws FileNotFoundException {
		this.buffer = new PushbackReader(new FileReader(fileName));
		riga = 1;
		
		// Inizializziamo tutto qui
		skipChars = new HashSet<>();
		letters = new HashSet<>();
		digits = new HashSet<>();
		operTkType = new HashMap<>();
		delimTkType = new HashMap<>();
		keyWordsTkType = new HashMap<>();
		
		inizializza();
	}
	
	private void inizializza() {
		// Caratteri da ignorare
		for (char c : new char[]{' ', '\n', '\t', '\r', EOF}) skipChars.add(c);

		// Lettere e Cifre
		for (char c = 'a'; c <= 'z'; c++) letters.add(c);
		for (char c = '0'; c <= '9'; c++) digits.add(c);

		// Operatori (+ - * /)
		operTkType.put('+', TokenType.PLUS);
		operTkType.put('-', TokenType.MINUS);
		operTkType.put('*', TokenType.TIMES);
		operTkType.put('/', TokenType.DIVIDE);

		// Delimitatori e Assegnamento (= ;)
		delimTkType.put(';', TokenType.SEMI);
		delimTkType.put('=', TokenType.ASSIGN);

		// Parole Chiave
		keyWordsTkType.put("print", TokenType.PRINT);
		keyWordsTkType.put("int", TokenType.TYINT);
		keyWordsTkType.put("float", TokenType.TYFLOAT);
	}

	
	//ciclo che legge i caratteri "inutili" (spazi, tab, invii)
	public Token nextToken() throws LexicalException {
		try {
			char nextChar = peekChar();

		    // 1. Salta i caratteri di skip (spazi, tab, ecc.)
		    while (skipChars.contains(nextChar)) {
		        if (nextChar == EOF) {
		            return new Token(TokenType.EOF, riga);
		        }
		        
		        // Se è un invio, incrementiamo il contatore riga
		        if (nextChar == '\n') {
		            riga++;
		        }
		        
		        readChar(); // Consuma il carattere di skip
		        nextChar = peekChar(); // Sbircia il prossimo
		    }
		    
		    
		 // 2. Gestione Operatori e Delimitatori
		    if (operTkType.containsKey(nextChar) || delimTkType.containsKey(nextChar)) {
		        char currentCh = readChar(); // Consumiamo il primo carattere (es. '+')

		        // Caso speciale: Operatori composti (+=, -=, *=, /=)
		        if (operTkType.containsKey(currentCh)) {
		            if (peekChar() == '=') {
		                readChar(); // Consumiamo anche l'uguale '='
		                return new Token(TokenType.OP_ASSIGN, riga, currentCh + "=");
		            } else {
		                // Operatore semplice (+, -, *, /)
		                return new Token(operTkType.get(currentCh), riga);
		            }
		        }

		        // Caso: Delimitatori semplici (; o =)
		        if (delimTkType.containsKey(currentCh)) {
		            return new Token(delimTkType.get(currentCh), riga);
		        }
		    }
			
			return null; 
		} catch (IOException e) {
			throw new LexicalException("Errore di lettura alla riga " + riga);
		}
	}

	
	
	// Metodi ausiliari forniti dalla prof
	private char readChar() throws IOException {
		return ((char) this.buffer.read());
	}

	private char peekChar() throws IOException {
		int c = buffer.read();
		buffer.unread(c);
		return (char) c;
	}
}