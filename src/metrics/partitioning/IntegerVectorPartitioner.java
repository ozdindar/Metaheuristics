package metrics.partitioning;

import representation.IntegerVector;
import representation.base.Representation;

public class IntegerVectorPartitioner implements SearchSpacePartitioner {

    int[] uBounds;
    int[] lBounds;
    int slotCount;

    public IntegerVectorPartitioner(int[] uBounds, int[] lBounds, int slotCount) {
        this.uBounds = uBounds;
        this.lBounds = lBounds;
        this.slotCount = slotCount;
    }

    public static String idOf(int[] values, int[] lBounds, int[] uBounds, int slotCount)
    {
        StringBuilder idBuilder=new StringBuilder();

        for (int i =0;i<values.length;i++)
        {
            int range= uBounds[i]-lBounds[i];
            int vi = values[i]-lBounds[i];
            int slot = slotOf(vi, range, slotCount);
            idBuilder.append(slot);
            if (i<values.length-1)
                idBuilder.append("-");
        }

        return idBuilder.toString();
    }

    public static String idOf(int[] values, int lBound, int uBound, int slotCount)
    {
        StringBuilder idBuilder=new StringBuilder();

        for (int i =0;i<values.length;i++)
        {
            int range= uBound-lBound;
            int vi = values[i]-lBound;
            int slot = slotOf(vi, range, slotCount);
            idBuilder.append(slot);
            if (i<values.length-1)
                idBuilder.append("-");
        }

        return idBuilder.toString();
    }

    public String idOf( int[] values) {
        return idOf(values,lBounds,uBounds,slotCount);
    }

    private static int slotOf(int vi, int range, int slotCount) {
        double interval = (double) range/(double) slotCount;
        for (int i = 0; i < slotCount; i++) {
            if (interval*(i+1)> vi)
                return i;
        }
        return slotCount-1;
    }

    @Override
    public String idOf(Representation rep) {
        assert rep instanceof IntegerVector : "Double Vector is required!";
        IntegerVector rv= (IntegerVector) rep;

        String id = idOf(rv.getValues().stream().mapToInt(x->x).toArray());

        return id;
    }

    @Override
    public long locationCount() {
        return (long) Math.pow(slotCount,uBounds.length);
    }
}
