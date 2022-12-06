import java.rmi.Remote;
import java.util.ArrayList;

public interface CordenadorInterface extends Remote {
    public ArrayList<ProcessorClass> GetProcessores();

}
