package ast;

public class NodeDeref extends NodeExpr {
    private NodeId id;

    public NodeDeref(NodeId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Deref(" + id + ")";
    }
}