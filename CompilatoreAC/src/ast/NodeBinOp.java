package ast;

public class NodeBinOp extends NodeExpr {
    private LangOper op;
    private NodeExpr left;
    private NodeExpr right;

    public NodeBinOp(LangOper op, NodeExpr left, NodeExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "BinOp(" + left + " " + op + " " + right + ")";
    }
}