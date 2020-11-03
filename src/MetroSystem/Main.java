package MetroSystem;

import java.util.Scanner;
import java.io.IOException;

import MultigraphADT.*;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            Main.printUsage();
            System.exit(0);
        }
        String filename = args[0];

        Multigraph graph = new IndexedMultigraph();
        try {
            MetroMapParser.parse(filename, graph);
        }
        catch (IOException|MetroMapParser.InvalidMetroMap e) {
            e.printStackTrace();
            System.exit(1);
        }
        InteractiveMetroRouter metroSystem = new InteractiveMetroRouter(graph);
        metroSystem.askDirections();
    }

    public static void printUsage() {
        System.out.format("Usage: [FILENAME]\n");
    }
}
