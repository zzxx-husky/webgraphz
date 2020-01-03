import it.unimi.dsi.webgraph.BVGraph;

public class BuildOffsets {
    public static void main(String[] args) throws Exception {
        run(args[0]);
    }

    public static void run(String basename) throws Exception {
        BVGraph.main(new String[]{"-o", "-O", "-L", basename});
    }
}
