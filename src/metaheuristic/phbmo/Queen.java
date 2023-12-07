package metaheuristic.phbmo;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.base.Individual;
import representation.base.Population;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 10.08.2015.
 */
public class Queen {


    private static final double MAX_GAMMA = 0.9;
    private static final double MIN_GAMMA = 0.6;
    Individual individual;

    double minSpeed;
    double speed;
    List<Individual> spermethica = new ArrayList<>();

    public List<Individual> getSpermethica() {
        return spermethica;
    }

    double alpha =0.9;
    double gamma;

    public Queen(Individual individual) {
        this.individual = individual;
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void init(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {

        double randCost = problem.cost(solutionGenerator.generate(problem,1).get(0));

        gamma = RandUtil.randDouble(MIN_GAMMA,MAX_GAMMA);
        spermethica.clear();
        speed = (individual.getCost()-randCost)/Math.log(gamma);
        minSpeed = (individual.getCost()-randCost)/Math.log(0.05);


    }

    public void addToSpermethica(Individual drone) {
        spermethica.add(drone);
    }

    public void update() {
        speed *= alpha;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public void doMatingFlightOver(Population drones, int smmin, int spmax) {

        while ((spermethica.size()<smmin)|| (speed>minSpeed && spermethica.size()<spmax)) {
            Individual drone = selectDrone(drones);
            double matingProb = calculateMatingProbability(drone);
            if (RandUtil.rollDice(matingProb)) {
                addToSpermethica(drone);
            }
            update();
        }

    }

    private Individual selectDrone(Population drones) {
        return drones.get(RandUtil.randInt(drones.size()));
    }

    private double calculateMatingProbability(Individual drone) {
        return Math.exp(-1*(Math.abs(drone.getCost()-individual.getCost())/speed));
    }
}
