package metrics.stn;

import org.jgrapht.EdgeFactory;

public class STNEdgeFactory implements EdgeFactory<STNNode,STNEdge> {
    @Override
    public STNEdge createEdge(STNNode n1, STNNode n2) {
        return new STNEdge(n1,n2,0);
    }
}
