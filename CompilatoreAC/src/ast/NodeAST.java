package ast;

import visitor.IVisitor; // Aggiungi questo import

public abstract class NodeAST {
    // Il metodo fondamentale del pattern Visitor
    public abstract void accept(IVisitor visitor);
}