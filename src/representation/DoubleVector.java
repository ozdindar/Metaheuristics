package representation;

import metaheuristic.pso.base.Velocity;
import representation.base.Representation;


/**
 * Created by dindar.oz on 03.06.2015.
 */
public class DoubleVector implements Representation,Velocity {

    double[] values;

    public DoubleVector(int size) {
        this.values = new double[size];
    }


    public DoubleVector(double[] q) {
        this.values = q;
    }

    public DoubleVector(int size, double initialPosition) {
        this.values = new double[size];
        for (int i= 0 ; i<size;i++)
            values[i] = initialPosition;

    }

    public int size()
    {
        return values.length;
    }

    @Override
    public Representation clone() {
        double q[] = new double[values.length];
        for (int i =0 ; i<q.length;i++)
        {
            q[i] = values[i];
        }
        return new DoubleVector(q);
    }

    @Override
    public boolean equals(Object r) {
        if (!(r instanceof DoubleVector))
            return false;
        DoubleVector ir = (DoubleVector)r;

        if (ir.getValues().length!=values.length)
            return false;
        for (int i=0;i<values.length;i++)
        {
            if (values[i] != ir.getValues()[i])
                return false;
        }
        return true;
    }

    @Override
    public double distanceTo(Representation r) {

        DoubleVector dv = (DoubleVector)r;
        double sum =0;


        for (int i=0; i<values.length;i++)
        {
            sum += (values[i]-dv.getValues()[i])*(values[i]-dv.getValues()[i]);
        }


        return Math.sqrt(sum);
    }

    public double[] getValues() {
        return values;
    }

    public void add(DoubleVector v) {
        add(v.getValues());
    }
    public void add(double[] values) {
        for (int i=0;i<values.length;i++)
            this.values[i] += values[i];
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    @Override
    public Velocity multiply(double c) {
        for (int i=0;i<values.length;i++)
            values[i]= c*values[i];

        return this;
    }

    @Override
    public Velocity distance(Representation r1, Representation r2) {
        DoubleVector dr1 = (DoubleVector)r1;
        DoubleVector dr2 = (DoubleVector)r2;


        DoubleVector d = (DoubleVector)r1.clone();

        for (int i=0;i<d.getValues().length;i++)
            d.values[i] = dr1.values[i]-dr2.values[i];

        return d;
    }

    @Override
    public void move(Representation r) {
        DoubleVector dr = (DoubleVector)r;

        for (int i=0; i<dr.values.length;i++)
            dr.values[i] += values[i];


        return ;
    }

    @Override
    public Velocity add(Velocity v) {
        DoubleVector d = (DoubleVector)clone();

        for (int i=0; i<d.values.length;i++)
            d.values[i] += ((DoubleVector)v).getValues()[i];

        return d;
    }

    @Override
    public boolean isNullVelocity() {
        for (int i =0;i<values.length;i++)
        {
            if (values[i] != 0)
                return false;
        }
        return true;
    }

    public void set(int index,double value)
    {
        values[index] = value;
    }
}
