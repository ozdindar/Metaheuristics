package problems.rap;

/**
 * Created by dindar.oz on 6/23/2017.
 */
public class Component {
    double reliability;
    double cost;
    double weight;

    public Component(double reliability, double cost, double weight) {
        this.reliability = reliability;
        this.cost = cost;
        this.weight = weight;
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
