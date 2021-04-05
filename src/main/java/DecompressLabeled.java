import it.unimi.dsi.fastutil.longs.LongBigListIterator;
import it.unimi.dsi.law.webgraph.CompressedIntLabel;
import it.unimi.dsi.webgraph.labelling.ArcLabelledImmutableGraph;
import it.unimi.dsi.webgraph.labelling.ArcLabelledNodeIterator;
import it.unimi.dsi.webgraph.labelling.BitStreamArcLabelledImmutableGraph;
import it.unimi.dsi.fastutil.longs.LongBigList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class DecompressLabeled {
    private static void printHelpMessage() {
        System.out.println("Convert a Webgraph file to an labelled edge list file in text format.");
        System.out.println("./executable graph-file [Path to Webgraph file] output-file [Path to output file]");
    }

    public static void main(String[] args) throws Exception {
    	String graphFilePath = args[0];
        String outputFilePath = args[1];

    	if (!new File(graphFilePath + ArcLabelledImmutableGraph.UNDERLYINGGRAPH_SUFFIX +".offsets").exists()) {
            System.out.println("Offsets not found for " + graphFilePath + ArcLabelledImmutableGraph.UNDERLYINGGRAPH_SUFFIX);
            BuildOffsets.run(graphFilePath + ArcLabelledImmutableGraph.UNDERLYINGGRAPH_SUFFIX);
            System.out.println("Offsets built for " + graphFilePath + ArcLabelledImmutableGraph.UNDERLYINGGRAPH_SUFFIX);
        }

        ArcLabelledImmutableGraph graph = ArcLabelledImmutableGraph.load(graphFilePath);
        int numNodes = graph.numNodes();
        System.out.println("Successfully load graph " + graphFilePath + ". Number of nodes: " + numNodes + ".");

        File nodeLabelFile = new File(outputFilePath + ".nodelabels");
        assert nodeLabelFile.exists() || nodeLabelFile.createNewFile();

        PrintWriter nodeWriter = new PrintWriter(new BufferedWriter(new FileWriter(nodeLabelFile)));

        ArcLabelledNodeIterator.LabelledArcIterator it = graph.successors(0);
        LongBigList nodeLabelList = ((CompressedIntLabel)it.label()).nodeLabels;
        LongBigListIterator labelIt = nodeLabelList.iterator();
        int nodeIndex = 0;

        System.out.println("Start writing node label file");
        while (labelIt.hasNext()) {
            nodeWriter.write(String.valueOf(nodeIndex));
            nodeWriter.write(' ');
            nodeWriter.write(String.valueOf(labelIt.nextLong()));
            nodeWriter.write("\n");
            nodeIndex += 1;
        }
        System.out.println("Finish writing node label file");

        File edgeLabelFile = new File(outputFilePath + ".edge");
        assert edgeLabelFile.exists() || edgeLabelFile.createNewFile();

    	PrintWriter edgeWriter = new PrintWriter(new BufferedWriter(new FileWriter(edgeLabelFile)));

        System.out.println("Start writing edge file");
    	int prog = 10;
        for (int i = 0; i < numNodes; i++) {
             ArcLabelledNodeIterator.LabelledArcIterator succ = graph.successors(i);
             for (int d = graph.outdegree(i); d > 0; d--) {
             	int label = (int)succ.label().get();
             	  edgeWriter.write(String.valueOf(i));
             	  edgeWriter.write(' ');
                edgeWriter.write(String.valueOf(label));
                edgeWriter.write(' ');
                edgeWriter.write(String.valueOf(succ.nextInt()));
                edgeWriter.write('\n');
             }
             if (i * 100.0 / numNodes >= prog) {
                 System.out.println("Convertion progress: " + i * 100.0 / numNodes + "%.");
                 prog += 10;
             }
         }
    }
}
