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
	private Token nextTk = null;
	

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
	
	
	public Token peekToken() throws LexicalException {
	    if (nextTk == null) {
	        nextTk = nextToken(); // Se il parcheggio è vuoto, lo riempiamo
	    }
	    return nextTk; // Restituiamo il token parcheggiato
	}

	
	//ciclo che legge i caratteri "inutili" (spazi, tab, invii)
	public Token nextToken() throws LexicalException {
		// Se c'è un token già letto da peekToken, lo restituiamo e svuotiamo il parcheggio
	    if (nextTk != null) {
	        Token t = nextTk;
	        nextTk = null;
	        return t;
	    }
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
		    
		    
		 // 3. Gestione Identificatori e Parole Chiave
		    if (letters.contains(nextChar)) {
		        return scanId();
		    }
		    
		    
		 // 4. Gestione Numeri (Interi e Float)
		    if (digits.contains(nextChar)) {
		        return scanNumber();
		    }
		    
		    
		 // 5. Se arriviamo qui, il carattere non è riconosciuto
		    // Leggiamo il carattere per "consumarlo" dal buffer
		    char illegalChar = readChar(); 
		    // Lanciamo l'eccezione con il messaggio dettagliato
		    throw new LexicalException("Errore riga " + riga + ": carattere illegale '" + illegalChar + "'");
		    
		} catch (IOException e) {
			throw new LexicalException("Errore di lettura alla riga " + riga);
		}
	}

	
	private Token scanId() throws IOException {
	    StringBuilder sb = new StringBuilder();
	    char nextChar = peekChar();

	    // Leggiamo finché troviamo lettere o cifre
	    while (letters.contains(nextChar) || digits.contains(nextChar)) {
	        sb.append(readChar()); // Consumiamo e aggiungiamo allo StringBuilder
	        nextChar = peekChar();
	    }

	    String word = sb.toString();

	    // Controlliamo se la stringa è una parola chiave (print, int, float)
	    if (keyWordsTkType.containsKey(word)) {
	        return new Token(keyWordsTkType.get(word), riga);
	    }

	    // Altrimenti è un normale identificatore (ID)
	    return new Token(TokenType.ID, riga, word);
	}
	
	
	
	
	private Token scanNumber() throws IOException, LexicalException {
	    StringBuilder sb = new StringBuilder();
	    char nextChar = peekChar();

	    // Leggiamo la parte intera (sequenza di cifre)
	    while (digits.contains(nextChar)) {
	        sb.append(readChar());
	        nextChar = peekChar();
	    }

	    // Controlliamo se c'è un punto decimale
	    if (nextChar == '.') {
	        sb.append(readChar()); // Consumiamo il punto '.'
	        nextChar = peekChar();

	        // Dopo il punto DEVE esserci almeno una cifra (regola del pattern ac)
	        if (!digits.contains(nextChar)) {
	            throw new LexicalException("Errore riga " + riga + ": attesa cifra dopo il punto decimale.");
	        }

	        int count = 0;
	        // Leggiamo la parte decimale (massimo 5 cifre come da pattern)
	        while (digits.contains(nextChar)) {
	            sb.append(readChar());
	            count++;
	            nextChar = peekChar();
	            
	            if (count > 5) {
	                throw new LexicalException("Errore riga " + riga + ": un numero float può avere al massimo 5 cifre decimali.");
	            }
	        }
	        return new Token(TokenType.FLOAT, riga, sb.toString());
	    }

	    // Se non abbiamo trovato il punto, è un semplice intero
	    return new Token(TokenType.INT, riga, sb.toString());
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