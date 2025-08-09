package metrics.partitioning;

import representation.DoubleVector;
import representation.base.Representation;

public class DoubleVectorPartitioner implements SearchSpacePartitioner {

    double[] uBounds;
    double[] lBounds;
    int slotCount;

    public DoubleVectorPartitioner(double[] uBounds, double[] lBounds, int slotCount) {
        this.uBounds = uBounds;
        this.lBounds = lBounds;
        this.slotCount = slotCount;
    }

    public String idOf( double[] values) {
        StringBuilder idBuilder=new StringBuilder();

        for (int i =0;i<values.length;i++)
        {
            double range= uBounds[i]-lBounds[i];
            double vi = values[i]-lBounds[i];
            int slot = slotOf(vi, range, slotCount);
            idBuilder.append(slot);
            if (i<values.length-1)
                idBuilder.append("-");
        }

        return idBuilder.toString();
    }

    private int slotOf(double vi, double range, int slotCount) {
        double interval = range/slotCount;
        for (int i = 0; i < slotCount; i++) {
            if (interval*(i+1)> vi)
                return i;
        }
        return slotCount-1;
    }

    @Override
    public String idOf(Representation rep) {
        assert rep instanceof DoubleVector : "Double Vector is required!";
        DoubleVector rv= (DoubleVector) rep;

        String id = idOf(rv.getValues());

        return id;
    }

    @Override
    public long locationCount() {
        return (long) Math.pow(slotCount,uBounds.length);
    }
}
