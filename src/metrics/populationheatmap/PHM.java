package metrics.populationheatmap;


import metrics.partitioning.SearchSpacePartitioner;
import representation.base.Individual;
import representation.base.Population;
import representation.base.Representation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PHM  {

    HashMap<String, PHMNode> nodeMap;
    long iteration;
    double bestCost;
    Representation bestRep;
    long bestCostUpdate; // The iteration that the best solution is updated

    private static final int DEFAULT_VISIT_THRESHOD = 5;
    private final int visitThreshold;

    public PHM() {
        this(DEFAULT_VISIT_THRESHOD);
    }

    public PHM(int visitThreshold) {
        nodeMap = new HashMap<>();
        iteration = 0;
        this.visitThreshold = visitThreshold;
    }

    public Collection<PHMNode> getNodes()
    {
        return nodeMap.values();
    }

    public Collection<PHMNode> getNodes(Predicate<PHMNode> filter)
    {
        return nodeMap.values().stream().filter(filter).collect(Collectors.toList());
    }

    public void addNode(PHMNode node)
    {
        nodeMap.put(node.getID(), node);
    }

    public PHMNode getNode(String id) {

        nodeMap.putIfAbsent(id, new PHMNode(id));
        return nodeMap.get(id);
    }

    public void setIteration(long iteration)
    {
        this.iteration = iteration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (PHMNode node : nodeMap.values())
        {
            sb.append(node.getID() + "  :  "+ node+ "\n");
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
            bestCostUpdate= iteration;
        }
    }

    public double getBestCost() {
        return bestCost;
    }

    public Representation getBestRep() {
        return bestRep;
    }

    public void update(Population p, SearchSpacePartitioner ssp) {
        Map<String, List<Individual>> pMap =
                p.getIndividuals().stream().collect(Collectors.groupingBy(i->ssp.idOf( i.getRepresentation())));
        for (String id: pMap.keySet())
        {
            PHMNode node= nodeMap.computeIfAbsent(id, PHMNode::new);
            List<Individual> individuals = pMap.get(id);

            node.update(individuals,iteration);
            if (individuals.size()>visitThreshold)
                node.visit(iteration);
        }
    }
}
