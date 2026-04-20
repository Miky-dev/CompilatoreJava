# Compilatore per il linguaggio "ac" (Front-End)

Questo progetto implementa il **Front-End** di un compilatore per il linguaggio didattico "ac". È stato sviluppato per il corso di *Fondamenti, Linguaggi e Traduttori*.

Il compilatore prende in input un file sorgente scritto in linguaggio `ac` ed esegue l'analisi lessicale, l'analisi sintattica e la costruzione dell'Albero Sintattico Astratto (AST).

## Struttura del Progetto

Il progetto è sviluppato in **Java** ed è diviso nei seguenti pacchetti:

### 1. Analisi Lessicale (`scanner`)
Traduce il flusso di caratteri in input in una sequenza di Token.
- `Scanner.java`: Legge il file sorgente e genera i token.
- `Token.java`: Rappresenta l'unità logica (es. ID, INT, FLOAT, operatori).
- `TokenType.java`: Enum che definisce tutti i tipi di token validi.
- `LexicalException.java`: Gestisce gli errori a livello di carattere (es. simboli non riconosciuti).

### 2. Analisi Sintattica (`parser`)
Verifica che la sequenza di token rispetti la grammatica del linguaggio tramite un approccio a **discesa ricorsiva** (Recursive-Descent Parsing).
- `Parser.java`: Verifica le produzioni grammaticali, gestisce le precedenze matematiche e l'associatività a sinistra. Costruisce e restituisce l'AST.
- `SyntacticException.java`: Gestisce gli errori grammaticali. Utilizza l'Exception Chaining per incapsulare e propagare eventuali eccezioni lessicali provenienti dallo scanner.

### 3. Abstract Syntax Tree (`ast`)
Contiene la gerarchia di classi utilizzata per rappresentare in memoria la struttura logica del programma.
- **Nodi base**: `NodeAST`, `NodeExpr`, `NodeDecSt`, `NodeStm`
- **Nodi concreti**: `NodeProgram`, `NodeDecl`, `NodeAssign`, `NodePrint`, `NodeBinOp`, `NodeDeref`, `NodeCost`, `NodeId`
- **Enum ausiliari**: `LangType` (INT, FLOAT) e `LangOper` (PLUS, MINUS, TIMES, DIV).

### 4. Testing (`test`)
- `TestParser.java`: Classe Main che avvia il compilatore, passando il file di input allo Scanner, invocando il Parser e stampando a video l'AST generato.

---

## Funzionalità Supportate

Il compilatore è in grado di elaborare con successo:
*   **Dichiarazioni di variabili:** Tipi `int` e `float` (con o senza inizializzazione).
*   **Istruzioni di stampa:** Il comando `print`.
*   **Assegnamenti:** Assegnamento semplice (`=`) e operatori di assegnamento combinato (`+=`, `-=`, `*=`, `/=`).
*   **Espressioni Aritmetiche:** Supporto per addizione, sottrazione, moltiplicazione e divisione, rispettando rigidamente:
    *   La corretta **precedenza degli operatori** (moltiplicazione/divisione hanno priorità su addizione/sottrazione).
    *   L'**associatività a sinistra** (es. `A - B - C` viene elaborato come `(A - B) - C`).

---

## Esempio di Utilizzo

**File di input (`test_semplice.ac`):**
```text
int temp;
temp += 7;
temp = 3 + 7 * 5 - 6;
