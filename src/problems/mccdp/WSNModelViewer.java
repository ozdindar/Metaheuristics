package problems.mccdp;

import gui.MHViewer;
import metaheuristic.IterationEvent;
import representation.IntegerPermutation;
import representation.IntegerVector;
import representation.base.Representation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * Created by dindar.oz on 1.07.2016.
 */
public class WSNModelViewer extends JPanel implements MHViewer {


    private static final int NODERADIUS = 1;
    private static final int OFFSET = 50;
    WSNModel model;
    IntegerVector placement;

    public void setZoomFactor(int zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    int zoomFactor =1;

    public WSNModelViewer(WSNModel model, IntegerVector placement) {
        this.model = model;
        this.placement = placement;
        Rectangle2D boundingRect = model.getBoundingRect();
        setSize((int)(boundingRect.getWidth()+OFFSET)*zoomFactor,(int)(boundingRect.getHeight()+OFFSET)*zoomFactor );
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D)g;
        Rectangle2D boundingRect = model.getBoundingRect();
        g2D.drawRect(zoomFactor*OFFSET/2,zoomFactor*OFFSET/2,(int)boundingRect.getWidth()*zoomFactor,zoomFactor*(int)boundingRect.getHeight());

        for (int i =0; i<model.targetPointCount();i++ )
        {
            drawNode(g2D,i);
        }


        Color c = g2D.getColor();
        g2D.setColor(Color.red);
        for (Integer i:placement.getList())
        {
            drawSensor(g2D,i);
        }
        g2D.setColor(c);
    }

    private void drawSensor(Graphics2D g2D, Integer sensorIndex) {
        Point2D pos = model.getSensorPosition(sensorIndex);
        int sx = zoomFactor*(int)(pos.getX()-model.getSensorRange()+(OFFSET/2));
        int sy = zoomFactor*(int)(pos.getY()-model.getSensorRange()+(OFFSET/2));
        int nx = zoomFactor*(int)(pos.getX()-NODERADIUS+(OFFSET/2));
        int ny = zoomFactor*(int)(pos.getY()-NODERADIUS+(OFFSET/2));
        g2D.drawOval(sx,sy, (int) (2*model.getSensorRange())*zoomFactor, (int) (2*model.getSensorRange())*zoomFactor);
        g2D.fillOval(nx,ny, (int) (2*NODERADIUS)*zoomFactor, (int) (2*NODERADIUS)*zoomFactor);
    }

    private void drawNode(Graphics2D g2D, int targetIndex ) {
        Color clr = g2D.getColor();

        Point2D pos = model.getTargetPosition(targetIndex);
        int x= ((int)pos.getX()-NODERADIUS+(OFFSET/2))*zoomFactor;
        int y = ((int)pos.getY()-NODERADIUS+(OFFSET/2))*zoomFactor;

        if (model.isCovered(targetIndex,placement.getList()))
            g2D.setColor(Color.red);

        g2D.drawOval(x,y,2*NODERADIUS*zoomFactor,2*NODERADIUS*zoomFactor);

        g2D.setColor(clr);
    }

    public static WSNModelViewer showModel(String title, WSNModel model, IntegerVector placement)
    {
        return showModel(title,model,placement,1);

    }

    public static WSNModelViewer showModel(String title, WSNModel model, IntegerVector placement, int zoomFactor)
    {
        JFrame fr = new JFrame(title);
        WSNModelViewer viewer = new WSNModelViewer(model,placement);
        viewer.setZoomFactor(zoomFactor);
        fr.add(viewer);
        fr.setSize(viewer.getSize());
        fr.setVisible(true);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return viewer;

    }
    public static void main(String[] args) {

        WSNGridModel model = new WSNGridModel(10,10,100,100,25);

        IntegerVector iv = new IntegerVector(Arrays.asList(12,315,117,227,839,459,582));
        showModel("Sample View",model,iv);

    }

    @Override
    public void updateViewer(IterationEvent event) {
        Representation best = event.getBestSolution();
        if (best instanceof IntegerPermutation)
            placement =model.firstFit(((IntegerPermutation) best).getList());
        else if (best instanceof  IntegerVector)
            placement = (IntegerVector) best;

        updateUI();
    }
}
