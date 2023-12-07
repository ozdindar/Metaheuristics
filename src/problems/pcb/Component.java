package problems.pcb;

public class Component {

    private double dimensions[], turretTime;
    private int index, type, groupIndex, N;
    private double[] neighbourDistances;

    public Component(int index, double[] dimensions, int type, int groupIndex, double turretTime){
        this.index = index;
        this.type = type;
        this.groupIndex = groupIndex;
        this.turretTime = turretTime;
        this.dimensions = new double[dimensions.length];
        System.arraycopy(dimensions, 0, this.dimensions, 0, dimensions.length);
    }

    public int getIndex(){
        return index;
    }

    public double[] getDimensions(){
        return dimensions;
    }

    public double getDimensionElement(int index){
        if(index >= dimensions.length)
            throw new ArrayIndexOutOfBoundsException();
        else
            return dimensions[index];
    }

    public String getComponentName(){
        return index + "";
    }

    public int getGroupIndex(){
        return groupIndex;
    }

    public double getTurretTime(){
        return turretTime;
    }
    
    public double getDistance(Component c){
        double dims[] = new double[2];
        double distance;
        
        System.arraycopy(c.getDimensions(), 0, dims, 0, 2);
        distance = 0;
        
        for(int k=0; k<2; k++)
            distance = Math.max(Math.abs(dimensions[k] - dims[k]), distance);
        
        return distance;
    }

    public void ComputeandSavetheDistance(Component c) {
        double dims[] = new double[2];
        double distance = 0;

        System.arraycopy(c.getDimensions(), 0, dims, 0, 2);
        for(int k=0; k<2; k++)
            distance = Math.max(Math.abs(dimensions[k] - dims[k]), distance);

        neighbourDistances[c.getIndex() - 1] = distance;
    }

    public double[] getNeighbourDistances(){
        return neighbourDistances;
    }

    public void initializeNeighbourArray(int N){
        this.N = N;
        neighbourDistances = new double[N];
    }

    @Override
    public String toString(){
        String ret = "";

        ret = ret + "Index: " + index + " ";

        for(int i=0; i<dimensions.length; i++)
            ret += ("Dimension " + i + ": " + dimensions[i] + " ");

        ret += "Type: " + type + " Group: " + groupIndex + " Turret Time: " + turretTime;

        for(int i=0; i<N; i++)
            ret += "\n" + neighbourDistances[i];
    

        return ret;
    }

}
