import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface BalancerInterface extends Remote {
     public void SendRequest(RequestClass r) throws RemoteException, MalformedURLException, NotBoundException;
     public void EndRequest(UUID request) throws RemoteException;
     public UUID FirstProcessor() throws RemoteException, MalformedURLException, NotBoundException;
     public  void ViewProcessors() throws  RemoteException;
     public int GetEStado(UUID IdentificadorRequest) throws RemoteException;
}
