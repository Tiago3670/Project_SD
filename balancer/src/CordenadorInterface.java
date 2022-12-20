import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface CordenadorInterface extends Remote {
    public ArrayList<ProcessorClass> GetProcessores();
    public ProcessorClass BestProcessor() throws RemoteException;
    public ProcessorClass BackupProcessor(ProcessorClass P) throws RemoteException;



}
