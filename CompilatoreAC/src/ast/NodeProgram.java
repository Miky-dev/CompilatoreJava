package ast;

import java.util.ArrayList;

import visitor.IVisitor;

public class NodeProgram extends NodeAST {
    private ArrayList<NodeDecSt> decSts;

    public NodeProgram(ArrayList<NodeDecSt> decSts) {
        this.decSts = decSts;
    }

    public ArrayList<NodeDecSt> getDecSts() {
        return decSts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (NodeDecSt node : decSts) {
            sb.append(node.toString()).append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}