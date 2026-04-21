package ast;

import visitor.IVisitor;

public class NodeId extends NodeExpr { // Un ID può essere valutato come un'espressione!
    private String name;

    public NodeId(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}