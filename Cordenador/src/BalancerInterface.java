import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface BalancerInterface extends Remote {

    public void AddProcessor(ProcessorClass p) throws RemoteException;
    public  void RemoveProcessor(String link) throws RemoteException;
    public void ResumeTasks(ProcessorClass p) throws IOException, NotBoundException, InterruptedException ;


    }