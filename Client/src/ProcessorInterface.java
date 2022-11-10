import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcessorInterface extends Remote {

    public void Send (RequestClass r) throws RemoteException, InterruptedException;

    public int GetEstado() throws RemoteException;
}
