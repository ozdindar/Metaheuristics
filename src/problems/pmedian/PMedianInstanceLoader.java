package problems.pmedian;

import java.io.*;
import java.util.*;

public class PMedianInstanceLoader {

    public static PMedianProblem loadFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();
        String[] header = line.trim().split("\\s+");

        int n = Integer.parseInt(header[0]); // number of nodes
        int m = Integer.parseInt(header[1]); // number of edges
        int p = Integer.parseInt(header[2]); // number of facilities

        double[][] distanceMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(distanceMatrix[i], Double.POSITIVE_INFINITY);
            distanceMatrix[i][i] = 0;
        }

        // Read edges
        for (int i = 0; i < m; i++) {
            line = reader.readLine();
            String[] parts = line.trim().split("\\s+");
            int u = Integer.parseInt(parts[0]) - 1;
            int v = Integer.parseInt(parts[1]) - 1;
            double dist = Double.parseDouble(parts[2]);
            distanceMatrix[u][v] = dist;
            distanceMatrix[v][u] = dist; // assuming undirected graph
        }

        // Floyd-Warshall to compute all-pairs shortest paths
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (distanceMatrix[i][k] + distanceMatrix[k][j] < distanceMatrix[i][j]) {
                        distanceMatrix[i][j] = distanceMatrix[i][k] + distanceMatrix[k][j];
                    }
                }
            }
        }

        reader.close();
        return new PMedianProblem(distanceMatrix, p);
    }
}
