package metrics.partitioning;

import representation.base.Representation;

public interface SearchSpacePartitioner {
    String idOf(Representation rep);
    long locationCount();
}
