package problems.movingpeaks;

/**
 * Created by oz on 22.07.2015.
 */
public class MP_Scenario {

    public static MP_Scenario DefaultScenario = new MP_Scenario();

    private MP_Scenario(){};

    public MP_Scenario(int change_frequency, long movrandseed, double vlength, double height_severity, double width_severity, double lambda, int number_of_peaks, double mincoordinate, double maxcoordinate, double minheight, double maxheight, double standardheight, double minwidth, double maxwidth, double standardwidth, Peak_Function pf, Basis_Function bf) {
        this.change_frequency = change_frequency;
        this.movrandseed = movrandseed;
        this.vlength = vlength;
        this.height_severity = height_severity;
        this.width_severity = width_severity;
        this.lambda = lambda;
        this.number_of_peaks = number_of_peaks;
        this.mincoordinate = mincoordinate;
        this.maxcoordinate = maxcoordinate;
        this.minheight = minheight;
        this.maxheight = maxheight;
        this.standardheight = standardheight;
        this.minwidth = minwidth;
        this.maxwidth = maxwidth;
        this.standardwidth = standardwidth;
        this.pf = pf;
        this.bf = bf;
    }

    public int getChange_frequency() {
        return change_frequency;
    }

    public void setChange_frequency(int change_frequency) {
        this.change_frequency = change_frequency;
    }

    public long getMovrandseed() {
        return movrandseed;
    }

    public void setMovrandseed(long movrandseed) {
        this.movrandseed = movrandseed;
    }

    public static int getGeno_size() {
        return geno_size;
    }

    public static void setGeno_size(int geno_size) {
        MP_Scenario.geno_size = geno_size;
    }

    public double getVlength() {
        return vlength;
    }

    public void setVlength(double vlength) {
        this.vlength = vlength;
    }

    public double getHeight_severity() {
        return height_severity;
    }

    public void setHeight_severity(double height_severity) {
        this.height_severity = height_severity;
    }

    public double getWidth_severity() {
        return width_severity;
    }

    public void setWidth_severity(double width_severity) {
        this.width_severity = width_severity;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public int getNumber_of_peaks() {
        return number_of_peaks;
    }

    public void setNumber_of_peaks(int number_of_peaks) {
        this.number_of_peaks = number_of_peaks;
    }

    public double getMincoordinate() {
        return mincoordinate;
    }

    public void setMincoordinate(double mincoordinate) {
        this.mincoordinate = mincoordinate;
    }

    public double getMaxcoordinate() {
        return maxcoordinate;
    }

    public void setMaxcoordinate(double maxcoordinate) {
        this.maxcoordinate = maxcoordinate;
    }

    public double getMinheight() {
        return minheight;
    }

    public void setMinheight(double minheight) {
        this.minheight = minheight;
    }

    public double getMaxheight() {
        return maxheight;
    }

    public void setMaxheight(double maxheight) {
        this.maxheight = maxheight;
    }

    public double getStandardheight() {
        return standardheight;
    }

    public void setStandardheight(double standardheight) {
        this.standardheight = standardheight;
    }

    public double getMinwidth() {
        return minwidth;
    }

    public void setMinwidth(double minwidth) {
        this.minwidth = minwidth;
    }

    public double getMaxwidth() {
        return maxwidth;
    }

    public void setMaxwidth(double maxwidth) {
        this.maxwidth = maxwidth;
    }

    public double getStandardwidth() {
        return standardwidth;
    }

    public void setStandardwidth(double standardwidth) {
        this.standardwidth = standardwidth;
    }

    public Peak_Function getPf() {
        return pf;
    }

    public void setPf(Peak_Function pf) {
        this.pf = pf;
    }

    public Basis_Function getBf() {
        return bf;
    }

    public void setBf(Basis_Function bf) {
        this.bf = bf;
    }

    /***** PARAMETER SETTINGS *****/

    /*
     *  number of evaluations between changes. change_frequency
     *  =0 means that function never changes (or only if function change_peaks is called)
     *  Scenario 1: 5000
     */
    private int change_frequency = 5000;


    /*
     *  seed for built-in random number generator
     *  En el escenraio 2: 1-5
     */
    private long movrandseed = 1;

    /*
     *  number of dimensions, or the number of double valued genes
     *  Scenerio 2:  5
     */
    /**
     * @uml.property  name="geno_size"
     */
    public static int geno_size = 5;

    /*
     *  distance by which the peaks are moved, severity
     *  Scenario 2: 0.0 - 3.0 --> para el escenario 1 lo que hac’a era poner el valor medio
     */
    private double vlength = 1.5;

    /*
     *  severity of height changes, larger numbers mean larger severity
     *  Scenario 2: 7.0
     */
    private double height_severity = 7.0;

    /*
     *  severity of width changes, larger numbers mean larger severity
     */
    private double width_severity = 1.0;

    /*
     *  lambda determines whether there is a direction of the movement, or whether
     *   they are totally random. For lambda = 1.0 each move has the same direction,
     *   while for lambda = 0.0, each move has a random direction
     */
    protected double lambda = 0.0;

    /*
     *  number of peaks in the landscape
     *  Scenario 2: 10-200
     */
    private int number_of_peaks = 100;



    /*
     *  minimum and maximum coordinate in each dimension
     *  Scenario 2: 0.0 and 100.0
     */
    private double mincoordinate = 0.0;
    private double maxcoordinate = 100.0;

    /*
     *  minimum and maximum height of the peaks
     *  Scenario 2:  30.0 and 70.0
     */

    private double minheight = 30.0;
    private double maxheight = 70.0;

    /*
     *  height chosen randomly when standardheight = 0.0
     *  Scenario 2: 50.0
     */
    private double standardheight = 50.0;

    /*
     *  Scenario 2: 1.0
     */
    private double minwidth = 1.0;

    /*
     *  Scenario 2: 12.0
     */
    private double maxwidth = 12.0;

    /*
     *  width chosen randomly when standardwidth = 0.0
     *  Scenario 2:  0.0
     */
    private double standardwidth = 0.0;

    /**
     * @uml.property name="pf"
     * @uml.associationEnd
     */
    public Peak_Function pf = new Peak_Function1();  //  Scenario 1

    /**
     * @uml.property  name="bf"
     * @uml.associationEnd
     */
    public Basis_Function bf = new Constant_Basis_Function();
}
