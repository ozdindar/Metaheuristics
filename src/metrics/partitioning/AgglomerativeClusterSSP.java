package metrics.partitioning;

import experiments.DiscreteSSP;
import representation.BinaryString;
import representation.base.Representation;

import java.util.*;

public class AgglomerativeClusterSSP implements DiscreteSSP {

    private static int hammingDistance(String a, String b) {
        int dist = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) dist++;
        }
        return dist;
    }

    static class Cluster {
        HashSet<String> elements = new HashSet<>();

        Cluster(String s) {
            elements.add(s);
        }

        Cluster(Cluster c1, Cluster c2) {
            elements.addAll(c1.elements);
            elements.addAll(c2.elements);
        }

        @Override
        public String toString() {
            return elements.toString();
        }
    }

    List<Cluster> partitions;

    private AgglomerativeClusterSSP(List<Cluster> partitions)
    {
        this.partitions=partitions;
    }

    public static List<Cluster> cluster(List<String> binaryStrings, int targetClusterCount) {
        List<Cluster> clusters = new ArrayList<>();
        for (String s : binaryStrings) {
            clusters.add(new Cluster(s));
        }

        int totalMerges = clusters.size() - targetClusterCount;
        int completedMerges = 0;

        int progress=0;
        while (clusters.size() > targetClusterCount) {
            int minDist = Integer.MAX_VALUE;
            int mergeA = -1, mergeB = -1;

            // Find the closest pair of clusters
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    int dist = minHammingDistance(clusters.get(i), clusters.get(j));
                    if (dist < minDist) {
                        minDist = dist;
                        mergeA = i;
                        mergeB = j;
                    }
                }
            }

            // Merge the closest pair
            Cluster merged = new Cluster(clusters.get(mergeA), clusters.get(mergeB));
            clusters.remove(mergeB); // remove higher index first
            clusters.remove(mergeA);
            clusters.add(merged);

            completedMerges++;
            int percentComplete = (int) ((completedMerges / (double) totalMerges) * 100);
            if (percentComplete != progress)
            {
                System.out.println("Clustering.. "+ percentComplete + "% completed");
                progress = percentComplete;
            }
        }

        return clusters;
    }


    private static int minHammingDistance(Cluster c1, Cluster c2) {
        int min = Integer.MAX_VALUE;
        for (String s1 : c1.elements) {
            for (String s2 : c2.elements) {
                min = Math.min(min, hammingDistance(s1, s2));
            }
        }
        return min;
    }


    public static AgglomerativeClusterSSP from(List<String> elements, int maxPartitions)
    {
        List<Cluster> clusters = cluster(elements,maxPartitions);
        return new AgglomerativeClusterSSP(clusters);
    }

    @Override
    public String idOf(Representation rep) {
        assert rep instanceof BinaryString : "BinaryString is required!";
        BinaryString code= (BinaryString) rep;

        String id = "" + indexOf(code.toString());
        return id;
    }

    private int indexOf(String code) {
        for (int i = 0; i < partitions.size(); i++) {
            if (partitions.get(i).elements.contains(code))
                return i;
        }
        return -1;
    }

    @Override
    public long locationCount() {
        return partitions.size();
    }
}
