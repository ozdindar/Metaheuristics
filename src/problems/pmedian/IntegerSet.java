package problems.pmedian;

import representation.base.Representation;

import java.util.*;

public class IntegerSet implements Representation {
    private Set<Integer> facilityIndices;

    public IntegerSet(Set<Integer> facilityIndices) {
        this.facilityIndices = new HashSet<>(facilityIndices);
    }

    public Set<Integer> getFacilityIndices() {
        return facilityIndices;
    }

    @Override
    public IntegerSet clone() {
        return new IntegerSet(new HashSet<>(facilityIndices));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerSet)) return false;
        IntegerSet that = (IntegerSet) o;
        return Objects.equals(facilityIndices, that.facilityIndices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityIndices);
    }

    @Override
    public double distanceTo(Representation r) {
        if (!(r instanceof IntegerSet)) return Double.MAX_VALUE;
        IntegerSet other = (IntegerSet) r;
        Set<Integer> union = new HashSet<>(facilityIndices);
        union.addAll(other.facilityIndices);
        Set<Integer> intersection = new HashSet<>(facilityIndices);
        intersection.retainAll(other.facilityIndices);
        return union.size() - intersection.size(); // Hamming-like distance
    }
}
