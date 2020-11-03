package MultigraphADT;

import java.util.List;
import java.util.Set;


/**
 * Directed multigraph interface with path searching capabilities.
 * Edges are directed, but addBidirectionalEdge() can be used to simulate an
 * undirected graph.
 */
public interface Multigraph {
    /**
     * Creates a new Node.
     *
     * @param id the unique identifier for the node.
     * @param name non-unique name for the node.
     * @return newly created Node.
     */
    public Node createNode(String id, String name);

    /**
     * Creates a new Edge.
     *
     * @param startNode start node.
     * @param endNode end node.
     * @param label non-unique label.
     * @return newly created Edge.
     */
    public Edge createEdge(Node startNode, Node endNode, String label);

    /**
     * Add a Node to the graph.
     * If the node is already part of the graph, the graph is left unchanged.
     */
    public void addNode(Node node);

    /**
     * Add an Edge to the graph.
     * If the edge is already part of the graph, the graph is left unchanged.
     * The start and end nodes of the edge are automatically added to the graph
     * too.
     */
    public void addEdge(Edge edge);

    /**
     * Add an Edge and its reversion to the graph.
     * If the edge is already part of the graph, the graph is left unchanged.
     * The start and end nodes of the edge are automatically added to the graph
     * too.
     * The original edge and its reversion are added to the graph.
     */
    public void addBidirectionalEdge(Edge edge);

    /**
     * Get the set of all nodes in the graph.
     */
    public Set<Node> getNodes();

    /**
     * Get the set of all edges in the graph.
     */
    public Set<Edge> getEdges();

    /**
     * Get the set of all edges that depart from the given node.
     */
    public Set<Edge> getOutgoingEdges(Node node);

    /**
     * Searches for the shortest paths between the startNode and the endNode.
     *
     * @param startNode origin node.
     * @param endNode target node.
     * @return set of paths, represented by lists of edges.
     */
    public Set<List<Edge>> searchShortest(Node startNode, Node endNode);

    /**
     * Searches for the shortest path between the startNode and the endNode.
     * If multiple paths are equal in length, the path with less changes in its
     * edges' labels is returned.
     *
     * @param startNode origin node.
     * @param endNode target node.
     * @return best path, represented by a list of edges, or null if there is no
     * path.
     */
    public List<Edge> searchBest(Node startNode, Node endNode);
}
