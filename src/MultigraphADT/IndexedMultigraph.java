package MultigraphADT;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Maintains edges in the graph indexed in a map by their origin nodes.
 */
public class IndexedMultigraph implements Multigraph {
    /**
     * Edges indexed by their starting node
     */
    private Map<Node, Set<Edge>> edges;

    public IndexedMultigraph() {
        this.edges = new HashMap<>();
    }

    public Node createNode(String id, String name) {
        return new IndexedNode(id,name);
    }

    public Edge createEdge(Node startNode, Node endNode, String label) {
        return new IndexedEdge(startNode, endNode, label);
    }

    public Set<Node> getNodes() {
        return this.edges.keySet();
    }

    public Set<Edge> getEdges() {
        return this.edges.values().stream()
                                  .flatMap(Set::stream)
                                  .collect(Collectors.toSet());
    }

    public Set<Edge> getOutgoingEdges(Node node) {
    	//returning a clone of our set to avoid affecting graph unintentionally
        return new HashSet<>(this.edges.get(node));
    }

    public void addNode(Node node) {
        // Break early and loudly
        if (node == null) {
            throw new IllegalArgumentException("Cannot add a null pointer as node.");
        }
        // checks if the node is already in the map. If not, adds a node, computes lambda 
        // expression taking node as a parameter and returning new empty set.
    	this.edges.computeIfAbsent(node, n -> new HashSet<>());
    }

    public void addEdge(Edge edge) {
        // Break early and loudly
        if (edge == null) {
            throw new IllegalArgumentException("Cannot add a null pointer as edge.");
        }
    	/* We use these nodes to ensure a set of edges exists
    	 * (if it does not we add a blank one)
    	 * for the edge being passed in
    	 */
    	this.addNode(edge.getStartNode());
    	this.addNode(edge.getEndNode());

    	/* Adding this edge to our Set
    	 * Current edge starting node as the key for the map
    	 * Then add the edge to the set under this key
    	 */
        this.edges.get(edge.getStartNode()).add(edge);
    }

    public void addBidirectionalEdge(Edge edge) {
        this.addEdge(edge);
        this.addEdge(edge.revert());
    }

    /**
     * Returns the set of all possible paths with the given start and end nodes.
     *
     * @param parents a map that represents for each node k all the edges ending at k.
     * @param start   the node at which all paths have to start.
     * @param end     the node at which all paths have to end.
     */
    private Set<List<Edge>> backtrack(Map<Node,Set<Edge>> parents, Node start, Node end) {
        Set<List<Edge>> paths = new HashSet<>();

        // We arrived at our destination
        if (start.equals(end)) {
            paths.add(new LinkedList<>());
        }

        Set<Edge> endParents = parents.get(end);

        // There is no way of getting there
        if (endParents == null) {
            return paths;
        }

        // For every edge that ends at this node
        for (Edge e : endParents) {
            // Get all the possible paths that get to the start of that edge
            Set<List<Edge>> preceding = this.backtrack(parents, start, e.getStartNode());
            // Append to each of those paths the edge that ends in the target
            for (List<Edge> path : preceding) {
                path.add(e);
                // Add to our bag of results
                paths.add(path);
            }
        }
        return paths;
    }

    public Set<List<Edge>> searchShortest(Node startNode, Node endNode) {
        // Keep track of the already evaluated nodes
        Set<Node> evaluated = new HashSet<>();

        // Keep track of the edges that got us to each node
        Map<Node,Set<Edge>> parents = new HashMap<>();

        // Keep track of the nodes we have to check the next batch
        Set<Node> batch = new HashSet<>();

        // We have to start somewhere
        batch.add(startNode);

        // Each loop evaluates a set of nodes at distance n
        while (!batch.isEmpty()) {
            Set<Node> nextBatch = new HashSet<>();
            boolean lastBatch = false;
            for (Node node : batch) {
                evaluated.add(node);
                // Evaluate possible candidates for the next batch
                for (Edge edge : this.getOutgoingEdges(node)) {
                    Node next = edge.getEndNode();
                    // Prevent evaluating already evaluated end nodes
                    if (evaluated.contains(next)) continue;
                    // Mark this batch as the last one if the end node is found
                    if (next.equals(endNode)) lastBatch = true;
                    // Fill next batch if this isn't the last one
                    if (!lastBatch) nextBatch.add(next);
                    // Annotate the edge we took to arrive to this node
                    parents.computeIfAbsent(next, n -> new HashSet<>()).add(edge);
                }
            }
            batch = nextBatch;
        }
        return this.backtrack(parents, startNode, endNode);
    }

    /**
     * Returns the number of switches in the edge labels of a path.
     */
    private int getNumberOfSwitches(List<Edge> path) {
        int switches = 0;
        String previousLabel = null;
        for (Edge e : path) {
            if (!e.getLabel().equals(previousLabel)) {
                switches++;
                previousLabel = e.getLabel();
            }
        }
        return switches;
    }

    /**
     * Returns the path with less changes in edge labels.
     *
     * @param paths the set of paths to evaluate.
     */
    private List<Edge> getLessChanging(Set<List<Edge>> paths) {
        return paths.stream()
                    .min((a, b) -> Integer.compare(this.getNumberOfSwitches(a),
                                                   this.getNumberOfSwitches(b)))
                    .orElse(null);
    }

    public List<Edge> searchBest(Node startNode, Node endNode) {
        Set<List<Edge>> shortestPaths = this.searchShortest(startNode, endNode);
        return this.getLessChanging(shortestPaths);
    }
}
