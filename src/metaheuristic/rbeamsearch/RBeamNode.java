package metaheuristic.rbeamsearch;

public class RBeamNode<T> {
    T nodeData;
    double cost;
    double deltaCost;
    int parentIndex;

    public RBeamNode(T nodeData, double cost,  double deltaCost , int assignment) {
        this.nodeData = nodeData;
        this.cost = cost;
        this.deltaCost = deltaCost;
        this.parentIndex = assignment;
    }


    public T getNodeData() {
        return nodeData;
    }

    public void setNodeData(T nodeData) {
        this.nodeData = nodeData;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String toString()
    {
        return nodeData + " | " + cost +" Delta: "+ deltaCost+ " Assignment : " + parentIndex;
    }

    public double getDeltaCost() {
        return deltaCost;
    }
}
