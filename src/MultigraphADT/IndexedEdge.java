package MultigraphADT;

import java.util.Objects;

public class IndexedEdge implements Edge {
    private final Node startNode;
    private final Node endNode;
    private final String label;

    public IndexedEdge(Node startNode, Node endNode, String label) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.label = label;
    }

    public Node getStartNode() {
        return this.startNode;
    }

    public Node getEndNode() {
        return this.endNode;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return String.format("%s --%s--> %s",
                             this.startNode.toString(),
                             this.label,
                             this.endNode.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.startNode, this.endNode, this.label);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        final IndexedEdge other = (IndexedEdge) obj;
        if (!Objects.equals(this.getLabel(), other.getLabel())) return false;
        if (!Objects.equals(this.getStartNode(), other.getStartNode())) return false;
        if (!Objects.equals(this.getEndNode(), other.getEndNode())) return false;
        return true;
    }

    public Edge revert() {
        return new IndexedEdge(this.getEndNode(), this.getStartNode(), getLabel());
    }
}
