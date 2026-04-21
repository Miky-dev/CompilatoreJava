package visitor;

import ast.*;
import symbolTable.SymbolTable;
import symbolTable.SymbolTable.Attributes;

public class TypeCheckinVisitor implements IVisitor {

    // Questa variabile fa da "nastro trasportatore" per i tipi.
    // Quando visitiamo un nodo (es. 5), lui imposterà questa variabile a INT.
    private TypeDescriptor typeCheckResult; 

    @Override
    public void visit(NodeProgram node) {
        SymbolTable.init(); // 1. Accendiamo la memoria del compilatore!
        
        System.out.println("Inizio Type Checking...");
        
        // 2. Visitiamo una per una tutte le dichiarazioni e le istruzioni
        for (NodeDecSt decSt : node.getDecSts()) {
            decSt.accept(this); 
        }
        
        System.out.println("Type Checking completato.");
        // Alla fine, possiamo stampare la Symbol Table per vedere cosa ha memorizzato
        System.out.println(SymbolTable.toStr()); 
    }

    @Override
    public void visit(NodeDecl node) {
        String varName = node.getId().getName();
        LangType varType = node.getType();

        // Proviamo a registrarla nella Symbol Table
        Attributes attr = new Attributes(varType);
        boolean success = SymbolTable.enter(varName, attr);

        if (!success) {
            System.err.println("Errore Semantico: Variabile '" + varName + "' già dichiarata!");
            typeCheckResult = TypeDescriptor.ERROR;
        } else {
            typeCheckResult = TypeDescriptor.OK;
        }
        
        // Se in futuro gestirai l'inizializzazione (es. int x = 5;),
        // qui dovrai visitare node.getInit() e confrontare i tipi.
    }

    @Override
    public void visit(NodeCost node) {
        // Un numero fisso ha un tipo facile da capire
        if (node.getType() == LangType.INT) {
            typeCheckResult = TypeDescriptor.INT;
        } else {
            typeCheckResult = TypeDescriptor.FLOAT;
        }
    }

    @Override
    public void visit(NodeDeref node) {
        // Stiamo leggendo una variabile (es. dentro un calcolo). Esiste?
        String varName = node.getId().getName();
        Attributes attr = SymbolTable.lookup(varName);

        if (attr == null) {
            System.err.println("Errore Semantico: Variabile '" + varName + "' non dichiarata!");
            typeCheckResult = TypeDescriptor.ERROR;
        } else {
            // Se esiste, il risultato di questo nodo è il tipo della variabile
            if (attr.getTipo() == LangType.INT) {
                typeCheckResult = TypeDescriptor.INT;
            } else {
                typeCheckResult = TypeDescriptor.FLOAT;
            }
        }
    }

    @Override
    public void visit(NodePrint node) {
        // La stampa legge la variabile, quindi usiamo la stessa logica del Deref
        String varName = node.getId().getName();
        if (SymbolTable.lookup(varName) == null) {
            System.err.println("Errore Semantico: Impossibile stampare '" + varName + "', non è dichiarata!");
            typeCheckResult = TypeDescriptor.ERROR;
        } else {
            typeCheckResult = TypeDescriptor.OK;
        }
    }


    
    @Override
    public void visit(NodeBinOp node) {
        // 1. Visitiamo il lato sinistro e salviamo il suo tipo
        node.getLeft().accept(this);
        TypeDescriptor leftType = this.typeCheckResult;

        // 2. Visitiamo il lato destro e salviamo il suo tipo
        node.getRight().accept(this);
        TypeDescriptor rightType = this.typeCheckResult;

        // Se uno dei due ha generato un errore (es. variabile inesistente), il calcolo fallisce
        if (leftType == TypeDescriptor.ERROR || rightType == TypeDescriptor.ERROR) {
            typeCheckResult = TypeDescriptor.ERROR;
            return;
        }

        // 3. Regole di Type Checking per le operazioni
        if (leftType == TypeDescriptor.INT && rightType == TypeDescriptor.INT) {
            // INT operazione INT = INT
            typeCheckResult = TypeDescriptor.INT;
        } else {
            // Se almeno uno dei due è FLOAT, il risultato "promuove" a FLOAT
            typeCheckResult = TypeDescriptor.FLOAT;
        }
    }

    

    @Override
    public void visit(NodeAssign node) {
        String varName = node.getId().getName();
        Attributes attr = SymbolTable.lookup(varName);

        // 1. La variabile a sinistra dell'uguale esiste?
        if (attr == null) {
            System.err.println("Errore Semantico: Variabile '" + varName + "' non dichiarata!");
            typeCheckResult = TypeDescriptor.ERROR;
            return;
        }

        // 2. Visitiamo tutto ciò che c'è a destra dell'uguale per scoprirne il tipo
        node.getExpr().accept(this);
        TypeDescriptor exprType = this.typeCheckResult;

        if (exprType == TypeDescriptor.ERROR) {
            typeCheckResult = TypeDescriptor.ERROR;
            return; // Errore già segnalato dai figli
        }

        // 3. Controllo dei tipi (Type Checking!)
        LangType varType = attr.getTipo(); // Il tipo dichiarato in memoria

        if (varType == LangType.INT && exprType == TypeDescriptor.FLOAT) {
            // Errore: stai cercando di mettere un 3.14 dentro un int!
            System.err.println("Errore di Tipo alla variabile '" + varName + "': impossibile assegnare un FLOAT a un INT.");
            typeCheckResult = TypeDescriptor.ERROR;
        } else {
            // In tutti gli altri casi va bene (INT in INT, FLOAT in FLOAT, o INT in FLOAT)
            typeCheckResult = TypeDescriptor.OK;
        }
    }

    @Override
    public void visit(NodeId node) {
        // Non c'è bisogno di fare nulla qui.
        // Il tipo di un ID viene gestito dai nodi che lo contengono 
        // (NodeDeref per la lettura, NodeAssign per la scrittura, NodeDecl per la dichiarazione).
    }
}




