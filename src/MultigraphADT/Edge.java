package MultigraphADT;

/**
 * A directed edge in a Multigraph.
 * An edge is made of a start node, an end node, and a label.
 * Edges can have their direction reversed.
 */
public interface Edge {
    public Node getStartNode();
    public Node getEndNode();
    public String getLabel();

    /**
     * Creates a new Edge by swapping the start and end node of the current one.
     * The label remains unchanged.
     */
    public Edge revert();
}
