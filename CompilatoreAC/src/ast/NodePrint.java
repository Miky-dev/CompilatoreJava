package ast;

public class NodePrint extends NodeStm {
    private NodeId id;

    public NodePrint(NodeId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Print(" + id + ")";
    }
}