import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public interface CordenadorInterface extends Remote {
    public ArrayList<ProcessorClass> GetProcessores();
    public ProcessorClass BestProcessor() throws RemoteException;
    public ProcessorClass BackupProcessor(ProcessorClass P) throws RemoteException;
    public ConcurrentHashMap sendProcessors() throws RemoteException;


}
