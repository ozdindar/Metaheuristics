package metrics.stn;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class STNVisualizer extends Application {

    public static STN stn; // Static reference to the graph
    private static List<Pair<String,Predicate<STNNode>>> filters;
    private static String fileName= "./output/stngraph.png";

    private static boolean displayWindow= true;

    static Scene buildScene(Predicate<STNNode> filter)
    {
        if (stn == null) {
            System.err.println("STN graph not initialized!");
            return null;
        }

        Group root = new Group();
        Map<STNNode, Double[]> positions = new HashMap<>();

        double width = 800, height = 600;
        int i = 0;
        double radius = 200;

        filter = filter!=null ? filter: (x)->true;
        Collection<STNNode> nodes = stn.getNodes(filter);

        // Position nodes in a circle
        for (STNNode node : nodes) {
            double angle = 2 * Math.PI * i / nodes.size();
            double x = width / 2 + radius * Math.cos(angle);
            double y = height / 2 + radius * Math.sin(angle);
            positions.put(node, new Double[]{x, y});
            i++;
        }

        // Draw edges
        for (STNNode from : nodes) {
            for (Connection<STNNode> conn : from.getConnections()) {
                STNNode to = conn.getTo();
                Double[] p1 = positions.get(from);
                Double[] p2 = positions.get(to);

                if (p1 == null || p2 == null)
                    continue;

                Line line = new Line(p1[0], p1[1], p2[0], p2[1]);
                line.setStrokeWidth(Math.min(conn.getCost(),5 )); // thickness
                line.setStroke(Color.GRAY);

                double midX = (p1[0] + p2[0]) / 2;
                double midY = (p1[1] + p2[1]) / 2;
                Text costLabel = new Text(midX, midY, String.format("%.1f", conn.getCost()));

                Polygon arrow = createArrowhead(p1[0], p1[1], p2[0], p2[1], 10, 5);

                root.getChildren().addAll(line, arrow, costLabel);

            }
        }


        double minCost = nodes.stream().mapToDouble(n -> n.bCost).min().orElse(0);
        double maxCost = nodes.stream().mapToDouble(n -> n.bCost).max().orElse(1);


        // Draw nodes
        for (STNNode node : nodes) {
            Double[] pos = positions.get(node);
            double size = Math.min(10, node.totalStay);

            Color nodeColor = getColorForCost(node.bCost, minCost, maxCost);

            Circle circle = new Circle(pos[0], pos[1], size, nodeColor);
            Text label = new Text(pos[0] - 30, pos[1] - size - 10,
                    node.id.substring(0,1) + "\nC:" + String.format("%.2f", node.bCost) +
                            "\nR:" + node.bRep.toString().substring(0,5) +"..."+
                            "\nmaxStay:" + node.maxStay +
                            "\ntotalStay:" + node.totalStay+
                            "\nfirstVisit:" + node.firstVisit);

            root.getChildren().addAll(circle, label);
        }



        Scene scene = new Scene(root, width, height, Color.WHITE);
        return scene;
    }
    @Override
    public void start(Stage stage) {
        if (stn == null) {
            System.err.println("STN graph not initialized!");
            return;
        }

        for (int f = 0; f < filters.size(); f++) {
            Scene scene = buildScene(filters.get(f).getValue());
            exportSceneAsImage(scene,filters.get(f).getKey());
            if (f==filters.size()-1 && displayWindow) {
                stage.setTitle("STN Graph Visualizer-1");
                stage.setScene(scene);
                stage.show();
            }
        }
    }


    public static void exportAsImage(STN graph, String fileName) {
        STNVisualizer.stn = graph;
        STNVisualizer.filters = Collections.singletonList(new Pair<>(fileName,null));
        STNVisualizer.displayWindow= false;
        STNVisualizer.fileName = fileName;
        Application.launch(STNVisualizer.class);
    }



public static void visualize(STN graph) {
        STNVisualizer.stn = graph;
        STNVisualizer.filters = Collections.singletonList(new Pair<>(fileName,null));
        Application.launch(STNVisualizer.class);
    }

    public static void visualize(STN graph, Predicate<STNNode> filter) {
        STNVisualizer.stn = graph;
        STNVisualizer.filters = Collections.singletonList(new Pair<>(fileName,filter));

        Application.launch(STNVisualizer.class);
    }

    public static void visualize(STN graph, Predicate<STNNode> filter, String fileName) {
        STNVisualizer.stn = graph;
        STNVisualizer.filters = Collections.singletonList(new Pair<>(fileName,filter));
        Application.launch(STNVisualizer.class);
    }

    public static void visualize(STN graph, List<Pair<String,Predicate<STNNode>>> filters) {
        STNVisualizer.stn = graph;
        STNVisualizer.filters = filters;
        Application.launch(STNVisualizer.class);
    }



    private static void exportSceneAsImage(Scene scene, String filename) {
        WritableImage image = scene.snapshot(null);
        File file = new File(filename);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Graph exported to " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static Color getColorForCost(double cost, double min, double max) {
        if (max == min) {
            return Color.GRAY; // avoid division by zero
        }

        double ratio = (cost - min) / (max - min);
        ratio = Math.max(0, Math.min(1, ratio)); // clamp between 0 and 1

        // Interpolate from green (low) to red (high)
        return Color.color(ratio, 1 - ratio, 0);
    }


    private static Polygon createArrowhead(double startX, double startY, double endX, double endY, double arrowLength, double arrowWidth) {
        double dx = endX - startX;
        double dy = endY - startY;
        double angle = Math.atan2(dy, dx);

        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        double x1 = endX - arrowLength * cos + arrowWidth * sin;
        double y1 = endY - arrowLength * sin - arrowWidth * cos;

        double x2 = endX - arrowLength * cos - arrowWidth * sin;
        double y2 = endY - arrowLength * sin + arrowWidth * cos;

        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(endX, endY, x1, y1, x2, y2);
        arrowHead.setFill(Color.DARKGRAY);
        return arrowHead;
    }

}
