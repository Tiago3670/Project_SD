import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface {
    BalancerInterface BalancerInte = (BalancerInterface) Naming.lookup("rmi://localhost:2023/Balancer");
    ReplicaInterface ReplicaInte = (ReplicaInterface) Naming.lookup("rmi://localhost:2021/Replica");

    protected ProcessorManager() throws RemoteException, MalformedURLException, NotBoundException {
    }

    @Override
    public ProcessorClass GetProcessor(ProcessorClass p) throws RemoteException {
        return p;
    }

    public void Send(RequestClass r) throws RemoteException, InterruptedException {
        r.setEstadoProcessamento();
        ReplicaInte.Ocupado( r.getIdentificadorProcessor());
        System.out.println("vou processar o request  "+r.getIdentificadorRequest()+" com o script "+r.getIdentificadorScript());
        r.setEstadoConcluido();
        BalancerInte.EndRequest(r.getIdentificadorRequest());
    }
}
