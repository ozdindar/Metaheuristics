package metrics.stn;

public class STNEdge implements Connection<STNNode> {
    double weight;
    STNNode from;
    STNNode to;

    public STNEdge(STNNode n1, STNNode n2, double weight) {
        from = n1;
        to = n2;
        this.weight = weight;
    }

    public void visit() {
        weight +=1;
    }

    public double getCost() {
        return weight;
    }

    public STNNode getTo() {
        return to;
    }

    public STNNode getFrom() {
        return from;
    }
}
