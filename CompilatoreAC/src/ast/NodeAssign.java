package ast;

public class NodeAssign extends NodeStm {
    private NodeId id;
    private NodeExpr expr;

    public NodeAssign(NodeId id, NodeExpr expr) {
        this.id = id;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "Assign(" + id + " = " + expr + ")";
    }
}