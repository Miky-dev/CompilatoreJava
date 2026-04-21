# Compilatore per il linguaggio "ac"

Questo progetto implementa un **compilatore completo** per il linguaggio didattico "ac", sviluppato per il corso universitario di *Fondamenti, Linguaggi e Traduttori*.

Il compilatore prende in input un file sorgente scritto in linguaggio `ac`, ne esegue l'analisi lessicale e sintattica, costruisce l'Albero Sintattico Astratto (AST), effettua l'analisi semantica (Type Checking) e genera infine il codice eseguibile per il calcolatore a stack `dc`.

## Struttura del Progetto

Il progetto è sviluppato in **Java** ed è strutturato nei seguenti pacchetti:

### 1. Analisi Lessicale (`scanner`)
Traduce il flusso di caratteri in input in una sequenza di Token validi.
- `Scanner.java`: Legge il file sorgente e genera i token sequenzialmente.
- `Token.java`: Rappresenta l'unità logica di base (es. ID, costanti INT/FLOAT, operatori).
- `TokenType.java`: Enum che definisce tutti i tipi di token previsti dalla grammatica.
- `LexicalException.java`: Gestisce gli errori a livello di singolo carattere (es. simboli non riconosciuti o illegali).s

### 2. Analisi Sintattica (`parser`)
Verifica che la sequenza di token rispetti rigorosamente la grammatica del linguaggio tramite un approccio a **discesa ricorsiva** (Recursive-Descent Parsing).
- `Parser.java`: Implementa le produzioni grammaticali, gestisce le precedenze matematiche e garantisce l'associatività a sinistra degli operatori. Al termine dell'analisi, costruisce e restituisce l'AST.
- `SyntacticException.java`: Gestisce gli errori grammaticali. Sfrutta l'Exception Chaining per incapsulare e propagare elegantemente le eccezioni lessicali provenienti dallo Scanner.

### 3. Abstract Syntax Tree (`ast`)
Definisce la gerarchia di classi utilizzata per rappresentare in memoria la struttura logica e semantica del programma.
- **Nodi base**: `NodeAST`, `NodeExpr`, `NodeDecSt`, `NodeStm`.
- **Nodi concreti**: `NodeProgram`, `NodeDecl`, `NodeAssign`, `NodePrint`, `NodeBinOp`, `NodeDeref`, `NodeCost`, `NodeId`. *Tutti i nodi implementano il metodo `accept()` per supportare l'ispezione tramite il Pattern Visitor.*
- **Enum ausiliari**: `LangType` (INT, FLOAT) e `LangOper` (PLUS, MINUS, TIMES, DIV, DIV_FLOAT).

### 4. Memoria e Analisi Semantica (`symbolTable`)
Gestisce il salvataggio e il recupero delle informazioni sulle variabili durante il processo di compilazione.
- `SymbolTable.java`: Tabella dei simboli che memorizza gli identificatori dichiarati, il loro tipo (`int` o `float`) e il registro di memoria fisico assegnato.
- `Registri.java`: Classe di utilità che distribuisce dinamicamente i registri (lettere da 'a' a 'z') per la macchina a stack.

### 5. Navigazione e Logica (`visitor`)
Implementa il Pattern Visitor per separare le operazioni dalla struttura dati dell'AST.
- `TypeCheckinVisitor.java`: Esegue l'analisi semantica. Verifica che le variabili esistano, previene errori di tipo (es. blocca l'assegnamento di un `float` a un `int`) e adatta dinamicamente le operazioni (es. conversione in `DIV_FLOAT`).
- `CodeGeneratorVisitor.java`: Traduce l'AST validato in istruzioni per `dc` (Notazione Polacca Inversa), gestendo i registri, lo stack e i cambi di precisione per le operazioni decimali.

### 6. Esecuzione e Testing (`test`)
- `TestParser.java`: Classe Main che orchestra l'intera pipeline. Innesca in sequenza Scanner, Parser, Type Checker e Code Generator, stampando in console l'AST, lo stato della Symbol Table e il codice macchina generato (o gli eventuali errori riscontrati).

---

## Funzionalità Supportate

Il compilatore elabora con successo le seguenti caratteristiche del linguaggio `ac`:
*   **Dichiarazioni di variabili:** Tipi `int` e `float` (con o senza assegnamento iniziale).
*   **Istruzioni di stampa:** Il comando `print`.
*   **Assegnamenti:** Assegnamento semplice (`=`) e operatori di assegnamento combinato (`+=`, `-=`, `*=`, `/=`).
*   **Espressioni Aritmetiche:** Supporto per addizione, sottrazione, moltiplicazione e divisione, garantendo:
    *   La corretta **precedenza degli operatori** (moltiplicazione e divisione hanno la priorità).
    *   L'**associatività a sinistra** (es. `A - B - C` viene elaborato come `(A - B) - C`).
*   **Analisi Semantica:** Controllo rigoroso contro variabili non dichiarate, ridichiarazioni illecite e incompatibilità di tipo (Type Checking).
*   **Generazione Codice (`dc`):** Produzione automatizzata di istruzioni per calcolatori a stack Unix, inclusa la gestione dinamica della scala decimale (comando `k`).

---

## Esempio di Utilizzo

**File di input (`test_semplice.ac`):**
```text
int a = 0;
a += 1;
int b = 6;
float temp;
temp = 1.0 / 6 + a / b;
print a;
print b;
print temp;



Inizio parsing e costruzione AST...
Parsing completato con successo!

--- AST GENERATO ---
Decl(INT, a, init: Cost(INT:0))
Assign(a = BinOp(Deref(a) PLUS Cost(INT:1)))
Decl(INT, b, init: Cost(INT:6))
Decl(FLOAT, temp)
Assign(temp = BinOp(BinOp(Cost(FLOAT:1.0) DIV_FLOAT Cost(INT:6)) PLUS BinOp(Deref(a) DIV Deref(b))))
Print(a)
Print(b)
Print(temp)
--------------------
Inizio Type Checking...
Type Checking completato.
Symbol Table:
a -> INT
b -> INT
temp -> FLOAT

--- CODICE DC GENERATO ---
0 sa la 1 + sa 6 sb 1.0 6 5 k / 0 k la lb / + sc la p P lb p P lc p P
--------------------------
