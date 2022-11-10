import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface {

    BalancerInterface BalancerInte = (BalancerInterface) Naming.lookup("rmi://localhost:2023/Balancer");
    RequestClass request;


    protected ProcessorManager() throws RemoteException, MalformedURLException, NotBoundException {
    }


    public ProcessorClass GetProcessor(ProcessorClass p) throws RemoteException {
        return p;
    }

    public void Send(RequestClass r) throws RemoteException, InterruptedException {
           request=r;
           request.setEstadoConcluido();
    }


    public int GetEstado() throws RemoteException {
        if(request==null)
            return 0;
        else
        return request.getEstado();
    }
}
