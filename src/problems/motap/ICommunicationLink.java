package problems.motap;

/**
 * Created by dindar.oz on 15.07.2015.
 */
public interface ICommunicationLink extends IHardwareUnit{
    int getCommunicationCost();
    double getTransmissionRate();
}
