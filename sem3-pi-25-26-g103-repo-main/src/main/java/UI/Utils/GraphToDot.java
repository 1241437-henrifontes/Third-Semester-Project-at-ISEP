package UI.Utils;

import Model.Graph.Edge;
import Model.Graph.Graph;
import Model.Graph.Node;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;

/**
 * Utility class to export a graph representation of the Minimal Backbone Network
 * to a Graphviz DOT file.
 */
public class GraphToDot {
    /**
     * Output path for the generated DOT file.
     */
    private static final String mbnDot = "outputFiles/MBN.dot";

    /**
     * Generates a Graphviz DOT file for the provided graph.
     *
     * @param graph a graph whose vertices are stations (Node) and whose edge weight is a Double
     */
    public static void generateDotFile(Graph<Node, Double> graph) {
        StringBuilder sb = new StringBuilder();
        sb.append("graph \"Minimal Backbone Network\" {\n");
        sb.append("  layout=neato;\n");
        sb.append("  inputscale=1;\n");
        sb.append("  splines=false;\n");
        sb.append("  normalize=false;\n");
        sb.append("  outputorder=edgesfirst;\n");
        sb.append("  bgcolor=\"#ffffff\";\n");
        sb.append("  node [shape=point, width=0.06, fixedsize=true, style=filled, color=\"#949698\",\n");
        sb.append("        fillcolor=white, fontsize=7, fontname=\"Helvetica-Bold\", fontcolor=\"#1d2122\"];\n");
        sb.append("  edge [color=\"#e62c2b\", penwidth=2.5, style=solid];\n\n");

        double minXg = Double.MAX_VALUE;
        double maxYg = -Double.MAX_VALUE;

        for (int i = 0; i < graph.numVertices(); i++) {
            Node v = graph.vertex(i);
            minXg = Math.min(minXg, v.getXyCoordinates().first());
            maxYg = Math.max(maxYg, v.getXyCoordinates().second());
        }

        double U = 72.0;
        for (int i = 0; i < graph.numVertices(); i++) {
            Node v = graph.vertex(i);
            double x = v.getXyCoordinates().first();
            double y = v.getXyCoordinates().second();
            String name = v.getName();

            double xIn = (x - minXg) / U;
            double yIn = (y - maxYg) / U;

            sb.append(String.format(Locale.US, "  \"%s\" [pos=\"%.6f,%.6f!\", label=\"\", xlabel=\"%s\", tooltip=\"%s\"];\n", name, xIn, yIn, name, name));
        }
        sb.append("\n");

        Set<String> seen = new HashSet<>();
        for (Edge<Node, Double> e : graph.edges()) {
            Node u = e.getVOrig();
            Node v = e.getVDest();
            if (u.equals(v)) continue;

            String a = u.getName();
            String b = v.getName();
            String key = (a.compareTo(b) <= 0) ? (a + "||" + b) : (b + "||" + a);
            if (!seen.add(key)) continue;

            sb.append(String.format("  \"%s\" -- \"%s\";\n", a, b));
        }
        sb.append("}\n");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(mbnDot, StandardCharsets.UTF_8))) {
            bw.write(sb.toString());
            bw.newLine();
            System.out.println("DOT file successfully created: " + mbnDot +"\n");
        } catch (IOException ex) {
            System.err.println("Error writing DOT file: " + ex.getMessage());
        }
    }
}
