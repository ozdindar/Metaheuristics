package problems.pmedian;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PMedianVisualizer extends JPanel {

    private final double[][] coordinates;
    private final Set<Integer> facilityIndices;

    public PMedianVisualizer(double[][] coordinates, Set<Integer> facilityIndices) {
        this.coordinates = coordinates;
        this.facilityIndices = facilityIndices;
        setPreferredSize(new Dimension(800, 800));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int padding = 60;
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding;

        // Determine bounds
        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (double[] point : coordinates) {
            minX = Math.min(minX, point[0]);
            maxX = Math.max(maxX, point[0]);
            minY = Math.min(minY, point[1]);
            maxY = Math.max(maxY, point[1]);
        }

        // Draw grid lines and axis labels
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 10; i++) {
            int x = padding + i * width / 10;
            int y = padding + i * height / 10;
            g2.drawLine(x, padding, x, padding + height); // vertical
            g2.drawLine(padding, y, padding + width, y); // horizontal

            // Axis labels
            g2.setColor(Color.BLACK);
            double xLabel = minX + i * (maxX - minX) / 10;
            double yLabel = minY + (10 - i) * (maxY - minY) / 10;
            g2.drawString(String.format("%.1f", xLabel), x - 10, padding + height + 20);
            g2.drawString(String.format("%.1f", yLabel), padding - 40, y + 5);
            g2.setColor(Color.LIGHT_GRAY);
        }

        // Draw demand points and lines to nearest facility
        for (int i = 0; i < coordinates.length; i++) {
            double[] point = coordinates[i];
            int x = (int) ((point[0] - minX) / (maxX - minX) * width + padding);
            int y = (int) ((1 - (point[1] - minY) / (maxY - minY)) * height + padding);

            // Find nearest facility
            double minDist = Double.MAX_VALUE;
            int fx = 0, fy = 0;
            for (int f : facilityIndices) {
                double[] facility = coordinates[f];
                int fxTemp = (int) ((facility[0] - minX) / (maxX - minX) * width + padding);
                int fyTemp = (int) ((1 - (facility[1] - minY) / (maxY - minY)) * height + padding);
                double dist = Math.hypot(fxTemp - x, fyTemp - y);
                if (dist < minDist) {
                    minDist = dist;
                    fx = fxTemp;
                    fy = fyTemp;
                }
            }

            // Draw connection line
            g2.setColor(Color.GRAY);
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{4}, 0));
            g2.drawLine(x, y, fx, fy);

            // Draw demand point
            g2.setColor(Color.BLUE);
            g2.fillOval(x - 4, y - 4, 8, 8);
        }

        // Draw facilities
        for (int f : facilityIndices) {
            double[] facility = coordinates[f];
            int fx = (int) ((facility[0] - minX) / (maxX - minX) * width + padding);
            int fy = (int) ((1 - (facility[1] - minY) / (maxY - minY)) * height + padding);
            g2.setColor(Color.RED);
            g2.fillRect(fx - 6, fy - 6, 12, 12);
        }

        // Draw axis titles
        g2.setColor(Color.BLACK);
        g2.drawString("X Coordinate", getWidth() / 2 - 30, getHeight() - 10);
        g2.rotate(-Math.PI / 2);
        g2.drawString("Y Coordinate", -getHeight() / 2 - 30, 20);
        g2.rotate(Math.PI / 2);
    }

    public static void visualize(double[][] coordinates, Set<Integer> facilityIndices) {
        JFrame frame = new JFrame("P-Median Solution Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new PMedianVisualizer(coordinates, facilityIndices));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

public static void main(String[] args) {
        // Define coordinates of demand points (10 points in 2D)
        double[][] coordinates = {
                {1, 3}, {2, 8}, {5, 4}, {6, 9}, {8, 2},
                {9, 6}, {4, 7}, {7, 3}, {3, 2}, {6, 5}
        };

        // Define a simple solution: indices of selected facilities

        Set<Integer> facilityIndices = new HashSet<>(Arrays.asList(2, 5, 7));
        // facilities at points 2, 5, and 7

        // Visualize the solution
        SwingUtilities.invokeLater(() -> PMedianVisualizer.visualize(coordinates, facilityIndices));
    }


}
