import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.LazyIntIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Decompress {
    private static void printHelpMessage() {
        System.out.println("Convert a Webgraph file to an edge list file in text format.");
        System.out.println("./executable graph-file [Path to Webgraph file] output-file [Path to output file]");
    }

    public static void main(String[] args) throws Exception {
        if (Arrays.asList(args).contains("help")) {
            printHelpMessage();
            System.exit(0);
        }
        Map<String, String> argsMap = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            argsMap.put(args[i], args[i + 1]);
        }
        final String graphFilePath = argsMap.get("graph-file");
        final String outputFilePath = argsMap.get("output-file");
        if (!new File(graphFilePath + ".offsets").exists()) {
            System.out.println("Offsets not found for " + graphFilePath);
            BuildOffsets.run(graphFilePath);
            System.out.println("Offsets built for " + graphFilePath);
        }
        System.out.println("To load Webgraph from " + graphFilePath + " and convert to " + outputFilePath);
        final ImmutableGraph graph = ImmutableGraph.load(graphFilePath);
        int numNodes = graph.numNodes();
        System.out.println("Successfully load graph " + graphFilePath + ". Number of nodes: " + numNodes + ".");

        File file = new File(outputFilePath);
        assert file.exists() || file.createNewFile();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        int prog = 10;
        for (int i = 0; i < numNodes; i++) {
            LazyIntIterator succ = graph.successors(i);
            for (int d = graph.outdegree(i); d > 0; d--) {
                writer.write(String.valueOf(i));
                writer.write(' ');
                writer.write(String.valueOf(succ.nextInt()));
                writer.write('\n');
            }
            if (i * 100.0 / numNodes >= prog) {
                System.out.println("Convertion progress: " + i * 100.0 / numNodes + "%.");
                prog += 10;
            }
        }
        System.out.println("Convertion done.");
        writer.close();
    }
}