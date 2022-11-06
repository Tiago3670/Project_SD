import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface {
    static Scanner scan = new Scanner(System.in);
    BalancerInterface BalancerInte = (BalancerInterface) Naming.lookup("rmi://localhost:2023/Balancer");
    ReplicaInterface ReplicaInte = (ReplicaInterface) Naming.lookup("rmi://localhost:2021/Replica");

    protected ProcessorManager() throws RemoteException, MalformedURLException, NotBoundException {
    }

    @Override
    public ProcessorClass GetProcessor(ProcessorClass p) throws RemoteException {
        return p;
    }

    public void Send(RequestClass r) throws RemoteException, InterruptedException {
        ReplicaInte.Ocupado(r.getIdentificadorProcessor());
        System.out.println("vou processar o request  "+r.getIdentificadorRequest()+" com o script "+r.getIdentificadorScript());
        int x=0;
           //processar o script
       do {
            System.out.println("1->Acabar processamento ");
            String end=scan.next();
            if(end.equals("1"))
            {
                ReplicaInte.Disponivel(r.getIdentificadorProcessor());
                BalancerInte.EndRequest(r.getIdentificadorRequest());
                x=-1;
                return;
            }

        }while (x!=-1);



    }
}
