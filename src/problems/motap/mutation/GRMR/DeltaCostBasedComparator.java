package problems.motap.mutation.GRMR;

import java.util.Comparator;

public class DeltaCostBasedComparator implements Comparator<Reassignment> {

    static final double Inequality_Threshold = 0.000001;
    @Override
    public int compare(Reassignment reassignment1, Reassignment reassignment2) {

        if (reassignment1 ==null || reassignment2 == null) {
            boolean aha = true;
        }
        return Double.compare(reassignment1.deltaCost, reassignment2.deltaCost);
    }
}
