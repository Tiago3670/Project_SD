import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcessorInterface extends Remote {
    public void Send (RequestClass r) throws IOException, InterruptedException;
    public int GetEstado() throws RemoteException;
}
