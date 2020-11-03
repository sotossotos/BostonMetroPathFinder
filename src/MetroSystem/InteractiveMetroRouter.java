package MetroSystem;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import MultigraphADT.*;

/**
 * Provided a `Multigraph` representing a map of the metro, asks the user for
 * origin and destination stations and outputs directions to get from one to the
 * other.
 */
public class InteractiveMetroRouter {
    private Multigraph metroMap;

    public InteractiveMetroRouter(Multigraph metroMap){
        this.metroMap = metroMap;
    }

    /**
     * Ask the user for start and end stations and output instructions.
     */
    public void askDirections() {
        Node from = this.askStation("From: ");
        Node to = this.askStation("To: ");
        List<Edge> path = this.metroMap.searchBest(from, to);
        System.out.println();
        this.outputDirections(path);
    }

    /**
     * Output instructions to follow the given path.
     *
     * @param path list of edges to follow, null represents no possible path.
     */
    public void outputDirections(List<Edge> path){
        if (path == null) {
            System.out.println("There is no path between those stations.");
        } else if (path.isEmpty()) {
            System.out.println("Start end end stations are the same, the path is trivial.");
        } else {
            System.out.format("You start at station %s.\n\n",
                              path.get(0).getStartNode().getName());

            // This will traverse through the path and print out sensible actions to the user.
            String lane = null;
            for (Edge edge : path) {
                if (lane == null || !lane.equals(edge.getLabel())) {
                    lane = edge.getLabel();
                    System.out.format("- Take lane %s.\n", lane);
                }
                System.out.format("- Go to %s.\n", edge.getEndNode().getName());
            }
            System.out.println("\nYou are now at your destination.");
        }
    }

    /**
     * Ask the user for input.
     *
     * @return the input the user entered.
     */
    private String promptInput(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        Scanner reader = new Scanner(System.in);
        return reader.nextLine();
    }

    /**
     * Ask the user to select a station by name.
     * If there is no station with the entered name, the user is asked again.
     * If there is more than one station with the entered name, the user is
     * asked to disambiguate.
     * Otherwise, the station with the entered name is returned.
     *
     * @param prompt string to output before reading user input.
     * @return station with the entered name.
     */
    public Node askStation(String prompt) {
        while (true) {
            Set<Node> candidates = this.getStationsWithName(this.promptInput(prompt));
            if (candidates.isEmpty()) {
                System.out.println("\nNo such station, please try again.\n");
            } else if (candidates.size() > 1) {
                System.out.println();
                return this.askToDisambiguate(candidates);
            } else {
                return candidates.iterator().next();
            }
        }
    }

    /**
     * Get all the stations with the given name.
     * The lookup is case insensitive.
     */
    private Set<Node> getStationsWithName(String name) {
        // Convert it into a stream of nodes,
        // weed out those which don't have the given name,
        // convert it back into a set.
        return this.metroMap.getNodes().stream()
                                       .filter(n -> n.getName().toLowerCase().equals(name.toLowerCase()))
                                       .collect(Collectors.toSet());
    }

    /**
     * Disambiguate between stations with the same name by asking the user to
     * select the lane their station is on.
     * To be able to get the lane of a station, we rely on the underlying
     * graph's edges beeing bidirectional or undirected.
     * The user's selection is case insensitive.
     *
     * @param stations set of stations with the same name.
     * @return station on the selected lane.
     */
    private Node askToDisambiguate(Set<Node> stations) {
        // Convert it into a stream of nodes,
        // convert each node into the set of edges that exit that node,
        // make a union of all those sets,
        // convert into a map having the edge's lowercased label as key and the
        // start node as value.
        Map<String,Node> lanes = stations.stream()
                                         .map(n -> this.metroMap.getOutgoingEdges(n))
                                         .flatMap(Set::stream)
                                         .collect(Collectors.toMap(e -> e.getLabel().toLowerCase(),
                                                                   e -> e.getStartNode(),
                                                                   (e1, e2) -> e1));
        while (true) {
            System.out.println("There exist multiple stations with that name.");
            System.out.println("Please, select the lane where the station is:");
            for (String label : lanes.keySet()) {
                System.out.format("- %s\n", label);
            }
            String selection = this.promptInput("Lane: ").toLowerCase();
            if (lanes.containsKey(selection)) {
                return lanes.get(selection);
            }
            System.out.println("\nNo such lane, try again.\n");
        }
    }
}
