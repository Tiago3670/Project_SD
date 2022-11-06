import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ProcessorInterface extends Remote {
    public ProcessorClass GetProcessor(ProcessorClass p) throws RemoteException;

    public void Send (RequestClass r) throws RemoteException;

}
