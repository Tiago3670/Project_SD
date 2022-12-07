import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface CordenadorInterface extends Remote {

    public void SendProcessors(String Link) throws RemoteException, MalformedURLException, NotBoundException;
    public ProcessorClass BestProcessor() throws RemoteException;
}
