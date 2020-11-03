package MultigraphADT;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.Before;

public class TestMapMultigraph {
    private IndexedMultigraph graph;

    @Before
    public void setUp() {
        graph = new IndexedMultigraph();
    }

    private Node[] createNodes(int n) {
        return IntStream.range(0, n)
                        .mapToObj(i -> String.format("n%d", i))
                        .map(i -> graph.createNode(i, ""))
                        .toArray(Node[]::new);
    }

    private Edge connect(Node start, Node end, String label) {
        Edge e = graph.createEdge(start, end, label);
        graph.addBidirectionalEdge(e);
        return e;
    }

    @Test
    public void test_unconnected_nodes() {
        Node[] ns = createNodes(2);
        graph.addNode(ns[0]);
        graph.addNode(ns[1]);
        assertEquals(null, graph.searchBest(ns[0], ns[1]));
    }

    @Test
    public void test_same_nodes() {
        Node[] ns = createNodes(1);
        graph.addNode(ns[0]);
        assertEquals(Arrays.asList(), graph.searchBest(ns[0], ns[0]));
    }

    @Test
    public void test_only_path() {
        Node[] ns = createNodes(2);
        Edge e = connect(ns[0], ns[1], "");
        assertEquals(Arrays.asList(e), graph.searchBest(ns[0], ns[1]));
    }

    @Test
    public void test_doesnt_go_further() {
        Node[] ns = createNodes(3);
        Edge e1 = connect(ns[0], ns[1], "");
        Edge e2 = connect(ns[1], ns[2], "");
        assertEquals(Arrays.asList(e1), graph.searchBest(ns[0], ns[1]));
    }

    @Test
    public void test_better_paths() {
        Node[] ns = createNodes(3);
        Edge e1 = connect(ns[0], ns[1], "");
        Edge e2 = connect(ns[1], ns[2], "");
        Edge e3 = connect(ns[0], ns[2], "");
        assertEquals(Arrays.asList(e3), graph.searchBest(ns[0], ns[2]));
    }

    @Test
    public void test_avoids_cycles() {
        Node[] ns = createNodes(4);
        // Cycle
        Edge e1 = connect(ns[0], ns[1], "");
        Edge e2 = connect(ns[1], ns[0], "");

        // Long path
        Edge e3 = connect(ns[0], ns[2], "");
        Edge e4 = connect(ns[2], ns[3], "");

        assertEquals(Arrays.asList(e3, e4), graph.searchBest(ns[0], ns[3]));
    }

    @Test
    public void test_equal_paths() {
        Node[] ns = createNodes(4);

        Edge e1 = connect(ns[0], ns[1], "");
        Edge e2 = connect(ns[1], ns[3], "");
        Edge e3 = connect(ns[0], ns[2], "");
        Edge e4 = connect(ns[2], ns[3], "");

        Set<List<Edge>> result = new HashSet<>();
        result.add(Arrays.asList(e1, e2));
        result.add(Arrays.asList(e3, e4));

        assertEquals(result, graph.searchShortest(ns[0], ns[3]));
    }

    @Test
    public void test_less_changing() {
        Node[] ns = createNodes(4);
        Edge e1 = connect(ns[0], ns[1], "blue");
        Edge e2 = connect(ns[1], ns[3], "blue");
        Edge e3 = connect(ns[0], ns[2], "blue");
        Edge e4 = connect(ns[2], ns[3], "red");
        assertEquals(Arrays.asList(e1, e2), graph.searchBest(ns[0], ns[3]));
    }

    @Test
    public void test_everything_equal() {
        Node[] ns = createNodes(4);
        Edge e1 = connect(ns[0], ns[1], "blue");
        Edge e2 = connect(ns[1], ns[3], "blue");
        Edge e3 = connect(ns[0], ns[2], "blue");
        Edge e4 = connect(ns[2], ns[3], "blue");
        assertThat(graph.searchBest(ns[0], ns[3]), anyOf(
                    equalTo(Arrays.asList(e1, e2)),
                    equalTo(Arrays.asList(e3, e4))));
    }

    @Test
    public void test_line_switches_are_counted() {
        Node[] ns = createNodes(8);
        Edge e1 = connect(ns[0], ns[1], "blue");
        Edge e2 = connect(ns[1], ns[2], "red");
        Edge e3 = connect(ns[2], ns[3], "blue");
        Edge e4 = connect(ns[3], ns[4], "red");

        Edge e5 = connect(ns[0], ns[5], "blue");
        Edge e6 = connect(ns[5], ns[6], "blue");
        Edge e7 = connect(ns[6], ns[7], "red");
        Edge e8 = connect(ns[7], ns[4], "red");
        assertEquals(Arrays.asList(e5, e6, e7, e8), graph.searchBest(ns[0], ns[4]));
    }
}
