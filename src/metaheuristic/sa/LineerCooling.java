package metaheuristic.sa;

/**
 * Created by dindar.oz on 16.06.2015.
 */
public class LineerCooling implements CoolingSchedule {
    double delta;

    public LineerCooling(double delta) {
        this.delta = delta;
    }

    @Override
    public double updateTemperature(double t) {
        return t-delta;
    }
}
