package metaheuristic.mbo;

import representation.ListPopulation;
import representation.base.Individual;
import representation.base.Population;

public class Bird {

    private int age =0;
    private Individual individual;
    private Population sharedNeighbours;

    private Individual neighbours[];

    public Bird(Individual individual){
        this.individual = individual;
        this.sharedNeighbours = new ListPopulation();

    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public Population getSharedNeighbours() {
        return sharedNeighbours;
    }

    public void setSharedNeighbours(Population sharedNeighbours) {
        this.sharedNeighbours = sharedNeighbours;
    }

    public Individual[] getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Individual neighbours[]) {
        this.neighbours = neighbours;
    }

    public void addSharedNeighbour(Individual neighbour) {
        sharedNeighbours.add(neighbour);
    }



    public void clearNeighbours() {
        neighbours =null;
    }

    public void clearSharedNeighbours() {
        sharedNeighbours.clear();
    }

    public void age() {
        age++;
    }

    public int getAge() {
        return age;
    }
}
