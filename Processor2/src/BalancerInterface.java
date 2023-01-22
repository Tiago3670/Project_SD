import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BalancerInterface extends Remote {
     public void SendRequest(RequestClass r) throws RemoteException, MalformedURLException, NotBoundException;
     public void CordenadorFail() throws RemoteException;
}
