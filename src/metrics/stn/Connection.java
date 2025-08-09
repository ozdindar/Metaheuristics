package metrics.stn;

public interface Connection<Node> {

    Node getFrom();
    Node getTo();

    double getCost();


}