package visitor;

import ast.*;
import symbolTable.SymbolTable;
import symbolTable.SymbolTable.Attributes;
import symbolTable.Registri;

public class CodeGeneratorVisitor implements IVisitor {

    private String codiceDc = ""; // Qui si accumula l'istruzione corrente
    private String log = "";      // Qui segniamo se finiamo i registri

    // Metodi per recuperare il risultato finale
    public String getCodice() { return codiceDc; }
    public String getLog() { return log; }

    @Override
    public void visit(NodeProgram node) {
        Registri.init(); // Resettiamo i registri (partiamo dalla 'a')
        StringBuilder programCode = new StringBuilder();

        // Visitiamo ogni riga del programma
        for (NodeDecSt decSt : node.getDecSts()) {
            if (!log.isEmpty()) break; // Se c'è un errore (es. registri finiti), ci fermiamo!
            
            decSt.accept(this);
            
            // Aggiungiamo il codice generato per questa riga al programma intero
            if (codiceDc != null && !codiceDc.isEmpty()) {
                programCode.append(codiceDc).append(" ");
            }
        }
        
        // Alla fine, codiceDc conterrà tutto il programma tradotto
        codiceDc = programCode.toString().trim();
    }

    @Override
    public void visit(NodeDecl node) {
        if (!log.isEmpty()) return;

        String varName = node.getId().getName();
        Attributes attr = SymbolTable.lookup(varName);

        // 1. Chiediamo una nuova lettera alla classe Registri
        char reg = Registri.newRegister();
        if (reg == '\0') {
            log = "Errore: Registri esauriti! Non ci sono abbastanza lettere da a-z.";
            return;
        }
        // 2. La assegnamo alla variabile nella Symbol Table
        attr.setRegistro(reg);

        // 3. Se c'è una inizializzazione (es. int x = 5;), la traduciamo
        if (node.getInit() != null) {
            node.getInit().accept(this); // Genera il codice del valore
            String initCode = codiceDc;
            codiceDc = initCode + " s" + reg; // "s" + lettera significa "Salva nel registro"
        } else {
            codiceDc = ""; // Nessun codice da generare per una dichiarazione vuota
        }
    }

    @Override
    public void visit(NodeAssign node) {
        if (!log.isEmpty()) return;

        // 1. Traduciamo la parte a destra dell'uguale
        node.getExpr().accept(this);
        String exprCode = codiceDc;

        // 2. Scopriamo in quale registro vive la variabile a sinistra
        String varName = node.getId().getName();
        char reg = SymbolTable.lookup(varName).getRegistro();

        // 3. Uniamo le due cose: "calcola l'espressione" e "salva (s) nel registro"
        codiceDc = exprCode + " s" + reg;
    }

    @Override
    public void visit(NodePrint node) {
        if (!log.isEmpty()) return;

        String varName = node.getId().getName();
        char reg = SymbolTable.lookup(varName).getRegistro();

        // "l" + reg -> carica(load) sullo stack
        // "p" -> printa a schermo
        // "P" -> rimuovilo dallo stack (Pop)
        codiceDc = "l" + reg + " p P";
    }

    @Override
    public void visit(NodeBinOp node) {
        if (!log.isEmpty()) return;

        node.getLeft().accept(this);
        String leftCode = codiceDc;

        node.getRight().accept(this);
        String rightCode = codiceDc;

        String opCode = "";
        switch (node.getOp()) {
            case PLUS: opCode = "+"; break;
            case MINUS: opCode = "-"; break;
            case TIMES: opCode = "*"; break;
            case DIV: opCode = "/"; break;
            case DIV_FLOAT: opCode = "5 k / 0 k"; break; // Slide 4/9: La divisione float in dc
        }

        // Sintassi Polacca Inversa: prima i numeri, poi l'operatore (es. 3 5 +)
        codiceDc = leftCode + " " + rightCode + " " + opCode;
    }

    @Override
    public void visit(NodeDeref node) {
        if (!log.isEmpty()) return;
        String varName = node.getId().getName();
        char reg = SymbolTable.lookup(varName).getRegistro();
        codiceDc = "l" + reg; // Leggi (load) dal registro
    }

    @Override
    public void visit(NodeCost node) {
        if (!log.isEmpty()) return;
        codiceDc = node.getValue(); // Per i numeri fisso, basta sputarli fuori così come sono
    }

    @Override
    public void visit(NodeId node) {
        // Nessuna azione diretta, come nel Type Checker
    }
}