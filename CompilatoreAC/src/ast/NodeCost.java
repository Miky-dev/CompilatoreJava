package ast;

public class NodeCost extends NodeExpr {
    private String value;
    private LangType type;

    public NodeCost(String value, LangType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Cost(" + type + ":" + value + ")";
    }
}