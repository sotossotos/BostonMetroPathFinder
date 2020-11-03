package MultigraphADT;

/**
 * A node in a Multigraph.
 * Each node has a unique identifier and a non-unique associated name.
 */
public interface Node {
    /**
     * Returns the unique identifier of this node.
     */
    public String getID();

    /**
     * Returns the name associated with this node.
     */
    public String getName();
}
