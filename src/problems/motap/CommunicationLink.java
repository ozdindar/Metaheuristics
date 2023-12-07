package problems.motap;

/**
 * Created by oz on 16.07.2015.
 */
public class CommunicationLink implements ICommunicationLink {
    private int communicationCost;
    private double transmissionRate;
    private double failureRate;

    public static final ICommunicationLink NullLink = new CommunicationLink(10,0.1,0 );
    public static final ICommunicationLink FreeLink = new CommunicationLink(0,1,0 );

    public CommunicationLink(int communicationCost, double transmissionRate, double failureRate) {
        this.communicationCost = communicationCost;
        this.transmissionRate = transmissionRate;
        this.failureRate = failureRate;
    }

    @Override
    public int getCommunicationCost() {
        return communicationCost;
    }

    @Override
    public double getTransmissionRate() {
        return transmissionRate;
    }

    @Override
    public double getFailureRate() {
        return failureRate;
    }

    @Override
    public String toString() {
        return "CL("+communicationCost+"//"+transmissionRate+")";
    }
}
