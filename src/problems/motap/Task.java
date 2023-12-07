package problems.motap;



import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dindar.oz on 15.07.2015.
 */
public class Task implements Serializable{
    List<IModule> modules;

    // Task Interaction Graph
    HashMap<Pair<Integer,Integer>,Integer> tig;

    public Task(List<IModule> modules, HashMap<Pair<Integer, Integer>, Integer> tig) {
        this.modules = modules;
        this.tig = tig;
    }

    public List<IModule> getModules() {
        return modules;
    }

    public HashMap<Pair<Integer, Integer>, Integer> getTig() {
        return tig;
    }

    double interactionDensity()
    {
        return ( (double)2* tig.size() / (double)modules.size()*(modules.size()-1));
    }


    int getCommunication(int task1, int task2 )
    {
        Integer c_ij = tig.get(Pair.of(task1,task2));
        if (c_ij==null)
            c_ij = tig.get(Pair.of(task2,task1));
        return  c_ij == null ? 0:c_ij;
    }

    @Override
    public String toString() {
        String res="";
        for (int m =0;m<modules.size();m++)
        {
            IModule md  = modules.get(m);
            res += md+"\n";
        }
        return res;
    }


    public int getMaxCommunicating(int module) {

        int maxCommunication =0;
        int c,m=-1;
        for (int i =0;i<modules.size();i++)
        {
            c = getCommunication(module,i);
            if (c>maxCommunication)
            {
                m = i;
                maxCommunication = c;
            }
        }
        return m;
    }

    public double totalMemoryRequirement() {
        int mr = 0;
        for (int i =0;i<modules.size();i++)
        {
            mr += modules.get(i).getMemoryRequirement();
        }
        return mr;
    }

    public double totalCRR() {
        int crr = 0;
        for (int i =0;i<modules.size();i++)
        {
            crr += modules.get(i).getCRR();
        }
        return crr;

    }

    public int getModuleCount() {
        return modules.size();
    }

    public IModule getModule(int m) {

        return modules.get(m);
    }
}
