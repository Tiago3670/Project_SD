import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface{
    protected BalancerManager() throws RemoteException, MalformedURLException, NotBoundException {}
    public UUID SendRequest(RequestClass r) throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {

        ProcessorInterface ProcessorInte = (ProcessorInterface) Naming.lookup("rmi://localhost:2024/Processor");
        ProcessorInte.Send(r);
        r.setIdentificadorProcessor(UUID.fromString(UUID.nameUUIDFromBytes(String.valueOf(2024).getBytes()).toString()));
        return r.getIdentificadorProcessor();
    }
}
