package ast;

public class NodeDecl extends NodeDecSt {
    private NodeId id;
    private LangType type;
    private NodeExpr init; // Può essere null se non c'è inizializzazione

    public NodeDecl(NodeId id, LangType type, NodeExpr init) {
        this.id = id;
        this.type = type;
        this.init = init;
    }

    @Override
    public String toString() {
        if (init == null) {
            return "Decl(" + type + ", " + id + ")";
        } else {
            return "Decl(" + type + ", " + id + ", init: " + init + ")";
        }
    }
}