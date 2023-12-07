package metaheuristic.rbeamsearch;

import java.util.Comparator;

public class DeltaCostBasedComparator implements Comparator<RBeamNode> {
    static final double Inequality_Threshold = 0.000001;
    @Override
    public int compare(RBeamNode node1, RBeamNode node2) {

        if (node1 ==null || node2 == null) {
            boolean aha = true;
        }
        return Double.compare(node1.deltaCost, node2.deltaCost);
    }
}
