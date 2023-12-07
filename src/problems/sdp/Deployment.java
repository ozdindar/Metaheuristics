package problems.sdp;

import math.geom2d.Angle2D;
import math.geom2d.Point2D;

/**
 * Created by dindar.oz on 17.05.2017.
 */
public class Deployment {
    Sensor  sensor;
    Point2D pos;
    double orientation;

    public boolean covers(Point2D target) {

        if (sensor.range> pos.distance(target))
            return false; // Out of range

        double angle = Angle2D.horizontalAngle(pos,target);

        return (angle>orientation && angle<orientation+sensor.viewAngle);
        // todo: Implement coverage condition

    }

    public static void main(String[] args) {
        Point2D p1 = new Point2D(1,0);
        Point2D p2 = new Point2D(1,1);
        Point2D p3 = new Point2D(2,1);

        System.out.println(Angle2D.horizontalAngle(p1,p2));
    }
}
