package metrics.partitioning;

import representation.base.Representation;

public class NullSSP implements SearchSpacePartitioner{
    @Override
    public String idOf(Representation rep) {
        return rep+"";
    }

    @Override
    public long locationCount() {
        return Long.MAX_VALUE;
    }
}
