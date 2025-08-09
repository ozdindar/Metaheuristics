package metrics.stn;

import representation.base.Representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class STNNode {

    HashMap<STNNode,STNEdge> neighborMap; // Transitions from this location to others
    String id;
    Representation bRep=null; // Best solution found in this location ( vector of real numbers)



    double bCost = -Double.MAX_VALUE; // Best solutions fitness in this location

    long totalStay; // the total # of iterations stayed at this location
    long maxStay; // the maximum # of iterations stayed at one visit at this location
    long curStay; // the current # of iterations stayed at current visit at this location

    long totalStall; // the total # of iterations stalled (solution not changed) at this location
    long maxStall; // the maximum # of iterations stalled at one visit at this location
    long curStall; // the current # of iterations stalled at current visit at this location


    long visitCount;
    long firstVisit; // The first time (iteration) that this location visited
    long lastVisit; // The last time (iteration) that this location is visited


    public String getId() {
        return id;
    }

    public Representation getbRep() {
        return bRep;
    }

    public double getbCost() {
        return bCost;
    }

    public long getMaxStay() {
        return maxStay;
    }

    public long getCurStay() {
        return curStay;
    }

    public long getTotalStay() {
        return totalStay;
    }

    public long getVisitCount() {
        return visitCount;
    }

    public long getLastVisit() {
        return lastVisit;
    }


    public STNNode(String id) {
        this.id = id;
        neighborMap = new HashMap<>();
    }

    public STNNode(String id, Representation bRep, double bCost) {
        this.id = id;
        this.bRep= bRep;
        this.bCost= bCost;
        neighborMap = new HashMap<>();
    }

    STNEdge edgeTo(STNNode node)
    {
        return neighborMap.get(node);
    }

    boolean isNeighborTo(STNNode node)
    {
        return neighborMap.containsKey(node);
    }

    public STNEdge addEdge(STNNode n2) {
        neighborMap.putIfAbsent(n2 , new STNEdge(n2,n2,0));
        STNEdge edge = neighborMap.get(n2);
        edge.visit();
        return edge;
    }

    public List<Connection<STNNode>> getConnections() {
        return new ArrayList<>(neighborMap.values());
    }

    public void stay(Representation bestSolution, double bestCost, long iteration) {
        if (iteration>0)
            curStall=0;
        visit(bestSolution,bestCost,iteration);
        totalStay++;
        if (++curStay>maxStay)
            maxStay = curStay;
    }

    void visit(Representation bestSolution, double bestCost, long iteration)
    {
        updateBest(bestSolution,bestCost);
        visitCount++;
        if (visitCount==1 && iteration>0 )
            firstVisit= iteration;
        lastVisit = iteration;
    }

    public void updateBest(Representation bestSolution, double bestCost) {
        if (bRep == null|| bestCost<bCost)
        {
            bCost = bestCost;
            bRep = bestSolution.clone();
        }
    }

    public void traverse(STNNode node, Representation bestSolution, double bestCost, long iteration) {
        if (node.equals(this))
            return;
        neighborMap.putIfAbsent(node, new STNEdge(this,node,0.0));
        STNEdge edge = neighborMap.get(node);
        curStay=0;
        curStall=0;
        edge.visit(); // using the edge
        node.visit(bestSolution,bestCost,iteration);
    }

    @Override
    public String toString() {
        return id + " C:"+ String.format("%-8.2f", bCost)+ " R:"+ bRep + " maxStay:"+ maxStay +" totalStay:"+totalStay + " NC:"+ neighborMap.size();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        STNNode other = (STNNode) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public long getFirstVisit() {
        return firstVisit;
    }

    public void stall() {
        stay(bRep,bCost,0);
        totalStall++;
        if (++curStall>maxStall)
            maxStall = curStall;
    }

    public long getTotalStall() {
        return totalStall;
    }

    public double totalOutgoingEdgeWeight()
    {
        return neighborMap.values().stream().mapToDouble(STNEdge::getCost).sum();
    }
}
