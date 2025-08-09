package metrics.stn;

import java.util.List;

public interface Graph<Node> {

    List<Connection<Node>> getConnections(Node from);
}
