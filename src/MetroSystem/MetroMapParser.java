package MetroSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.NoSuchElementException;

import MultigraphADT.*;

/**
 * This class reads a text description of a metro subway system
 * and generates a graph representation of the metro.
 *
 *<p>
 *
 * The grammar for the file is described below in BNF. A typical line
 * in the file looks like this :
 *
 * <code> 20 NorthStation   Green 19 22  Orange 15 22  </code>
 *
 * where :
 *         20 is the StationID
 *         NorthStation is the StationName
 *         Green 19 22
 *                  Green is the LineName
 *                  19 is the StationID of the outbound station
 *                  22 is the StationID of the inbound station
 *         Orange 15 22 is a LineID in which :
 *                  Orange is the LineName
 *                  15 is the StationID of the outbound station
 *                  22 is the StationID of the inbound station
 *
 *         Therefore, NorthStation has two outgoing lines.
 *
 *  note : 0 denotes the end of a line : i.e. in this case,
 *  OakGrove would be at the end of the line, as there is no other outbound
 *  station.
 *
 *<p>
 * metro-map ::= station-spec* <BR>
 * station-spec ::= station-id station-name station-line+ <BR>
 * station-id ::= (positive integer) <BR>
 * station-name ::= string <BR>
 * station-line ::= line-name station-id station-id <BR>
 *
 */
public class MetroMapParser {
    public static class InvalidMetroMap extends Exception {
        public static final long serialVersionUID = 1L;
        public InvalidMetroMap(String message) {
            super(message);
        }
    }

    /**
     * Parses the contents of the filename into the graph.
     */
    public static void parse(String filename, Multigraph graph) throws IOException, InvalidMetroMap {
        /*
         * Lines may connect stations which are defined in a farther down the
         * file. Nodes are inmmutable, so we have to keep a cache of edges that
         * have not yet been constructed.
         */

        // Station cache: station id -> station name
        Map<String,String> stations = new HashMap<>();
        // Edge cache: start station id -> end station id -> edge label
        Map<String,Map<String,String>> lines = new HashMap<>();

	BufferedReader fileInput = new BufferedReader(new FileReader(filename));
        String line;
	while ((line = fileInput.readLine()) != null) {
            // Tokenizer that splits on any of "\t\n\r\f"
	    StringTokenizer st = new StringTokenizer(line);

	    // Ignore empty lines
	    if (!st.hasMoreTokens()) continue;

	    String stationID = st.nextToken();

            try {
                String stationName = st.nextToken();
                stations.put(stationID, stationName);
            }
            catch (NoSuchElementException e) {
		throw new MetroMapParser.InvalidMetroMap("No station name.");
            }

	    if (!st.hasMoreTokens()) {
		throw new MetroMapParser.InvalidMetroMap("Station is on no lines.");
	    }

            lines.computeIfAbsent(stationID, id -> new HashMap<>());

	    while (st.hasMoreTokens()) {
		String lineName = st.nextToken();
                // Each (station and line) has an inbound and an outbound station
                for (int i = 0; i < 2; i++) {
                    try {
                        String connectedStationID = st.nextToken();
                        // Prevent creating stations that don't really exist
                        // They are just there to represent end of lines while
                        // maintaining the syntax
                        if (!connectedStationID.equals("0")) {
                            lines.get(stationID).put(connectedStationID, lineName);
                        }
                    }
                    catch (NoSuchElementException e) {
                        throw new MetroMapParser.InvalidMetroMap("Poorly formatted line info.");
                    }
                }
	    }
	}

        for (String startID : lines.keySet()) {
            for (String endID : lines.get(startID).keySet()) {
                Node start = graph.createNode(startID, stations.get(startID));
                Node end = graph.createNode(endID, stations.get(endID));
                Edge edge = graph.createEdge(start, end, lines.get(startID).get(endID));
                graph.addBidirectionalEdge(edge);
            }
        }
    }
}
