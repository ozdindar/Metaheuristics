package metrics.stn;



import representation.base.Representation;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class STN implements Graph<STNNode> {

    HashMap<String, STNNode> nodeMap;
    long iteration;
    double bestCost;
    Representation bestRep;

    public STN() {
        nodeMap = new HashMap<>();
        iteration = 0;
    }




    public Collection<STNNode> getNodes()
    {
        return nodeMap.values();
    }

    public Collection<STNNode> getNodes(Predicate<STNNode> filter)
    {
        return nodeMap.values().stream().filter(filter).collect(Collectors.toList());
    }

    @Override
    public List<Connection<STNNode>> getConnections(STNNode from) {
        return from.getConnections();
    }

    public void addNode(STNNode node)
    {
        nodeMap.put(node.id, node);
    }

    public STNNode getNode(String id, Representation rep, double cost) {

        nodeMap.putIfAbsent(id, new STNNode(id,rep,cost));
        return nodeMap.get(id);
    }

    public void setIteration(long iteration)
    {
        this.iteration = iteration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (STNNode node : nodeMap.values())
        {
            sb.append(node.id + "  :  "+ node+ "\n");
        }

        return sb.toString();
    }

    public void newIteration() {
        iteration++;
    }

    public long iteration() {
        return iteration;
    }

    void updateBest(Representation bestRep, double bestCost) {
        if (this.bestRep == null|| this.bestCost<bestCost)
        {
            this.bestCost = bestCost;
            this.bestRep = bestRep.clone();
        }
    }

    public double getBestCost() {
        return bestCost;
    }

    public Representation getBestRep() {
        return bestRep;
    }

    public double totalInWeight(STNNode n) {
        double inWeight = nodeMap.values().stream()
                .mapToDouble(x->x.neighborMap.containsKey(n)? x.neighborMap.get(n).weight:0.0)
                .sum();

        return inWeight;
    }
}
