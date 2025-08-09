package experiments.datacollectors.stn;

import experiments.datacollectors.AverageDC;
import metrics.HyperVolumeUtils;
import metrics.stn.STN;
import metrics.stn.STNNode;
import metrics.stn.STNUtils;

import java.util.Collection;
import java.util.function.Predicate;

public class HyperVolumeIndexDC extends AverageDC<STN> {

    final Predicate<STNNode> filter;
    final int  dimension;
    final long loc_count;
    public HyperVolumeIndexDC(int dimension, long locCount) {
        this(x->true,dimension,locCount);
    }

    public HyperVolumeIndexDC(Predicate<STNNode> filter, int dimension, long locCount) {
        this.filter = filter;
        this.dimension = dimension;
        loc_count = locCount;
    }

    @Override
    protected double _collect(STN stn) {
        Collection<STNNode> nodes = stn.getNodes(filter);
        if (nodes.isEmpty())
            return 0.0;

        double[][] hrect = STNUtils.boundingHyperRectangle(nodes,dimension);
        double hv = HyperVolumeUtils.calculateVolume(hrect);


        return hv*nodes.size()/loc_count;
    }

    @Override
    protected String title() {
        return "HVI";
    }
}
