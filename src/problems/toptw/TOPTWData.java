package problems.toptw;

import math.geom2d.Point2D;

import java.io.*;

public class TOPTWData {
    public double[][] distanceMatrix;
    public Node[] nodes;
    public int m;

    public TOPTWData(double[][] distanceMatrix, Node[] nodes, int m) {
        this.distanceMatrix = distanceMatrix;
        this.nodes = nodes;
        this.m = m;

    }

    public TOPTWData( Node[] nodes, int m) {
         this.nodes = nodes;
        this.m = m;
        constructDistanceMatrix();

    }

    public TOPTWData(String problemFile) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File(problemFile)));
            String line;
            String[] params;
            int N = 0;

            line = reader.readLine();
            params = line.split(" ");
            int nodeCount = Integer.parseInt(params[2])+1;
            m = Integer.parseInt(params[1]);
            nodes = new Node[nodeCount];

            line = reader.readLine();//Dummy line
            int node =0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if(line.isEmpty())
                    continue;
                params = line.split(" ");
                int openTime = node==0 ? 0:Integer.parseInt(params[8]);
                int closeTime = node==0 ? Integer.parseInt(params[8]):Integer.parseInt(params[9]);
                Node newNode = new Node(new Point2D(Double.parseDouble(params[1]),Double.parseDouble(params[2])),
                                         Double.parseDouble(params[3]), Double.parseDouble(params[4]),
                                        openTime,closeTime);

                nodes[node++] = newNode;
            }
            constructDistanceMatrix();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void constructDistanceMatrix()
    {
        distanceMatrix = new double[nodes.length][nodes.length];
        for (int i = 0; i< nodes.length-1; i++)
        {
            for (int j = i+1; j< nodes.length; j++)
            {
                double distance = nodes[i].pos.distance(nodes[j].pos);
                distanceMatrix[i][j] = distance;
                distanceMatrix[j][i] = distance;
            }
        }
    }


    public void setM(int m) {
        this.m = m;
    }

    public TOPTWData() {
    }
}