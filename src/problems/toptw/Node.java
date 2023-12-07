package problems.toptw;

import math.geom2d.Point2D;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class Node {

    Point2D pos;

    public double serviceTime;
    public double profit;
    public int opening;
    public int closing;

    public Node(Point2D pos, double serviceTime, double profit, int opening, int closing) {
        this.pos = pos;
        this.serviceTime = serviceTime;
        this.profit = profit;
        this.opening = opening;
        this.closing = closing;
    }
}
