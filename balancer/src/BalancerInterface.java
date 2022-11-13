import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface BalancerInterface extends Remote {
    public UUID SendRequest(RequestClass r) throws IOException, NotBoundException, InterruptedException;
}