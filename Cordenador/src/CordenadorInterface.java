import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public interface CordenadorInterface extends Remote {

    public void SendProcessors(String Link,Double CpuUsage) throws RemoteException, MalformedURLException, NotBoundException;
    public ProcessorClass BestProcessor() throws RemoteException;
    public ProcessorClass BackupProcessor(ProcessorClass P) throws RemoteException;
    public  void RemoveProcessor(String link) throws NotBoundException, IOException, InterruptedException ;
    public ConcurrentHashMap sendProcessors() throws RemoteException;


    }
