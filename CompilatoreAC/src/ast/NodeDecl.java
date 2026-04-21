package ast;

import visitor.IVisitor;

public class NodeDecl extends NodeDecSt {
    private NodeId id;
    private LangType type;
    private NodeExpr init; // Può essere null se non c'è inizializzazione

    public NodeDecl(NodeId id, LangType type, NodeExpr init) {
        this.id = id;
        this.type = type;
        this.init = init;
    }
    
    //  GETTERS
    public NodeId getId() {
        return id;
    }

    public LangType getType() {
        return type;
    }

    public NodeExpr getInit() {
        return init;
    }

    @Override
    public String toString() {
        if (init == null) {
            return "Decl(" + type + ", " + id + ")";
        } else {
            return "Decl(" + type + ", " + id + ", init: " + init + ")";
        }
    }
    
    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}