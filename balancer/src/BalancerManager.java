import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface , Serializable {
    ArrayList<ProcessorClass> ProcessorList = new ArrayList<ProcessorClass>();
    ArrayList<RequestClass> RequestList = new ArrayList<RequestClass>();
    DecimalFormat df = new DecimalFormat("#%");
    ProcessorClass best;CordenadorInterface CordenadorInte = (CordenadorInterface)  Naming.lookup("rmi://localhost:2026/Cordenador");
    protected BalancerManager() throws IOException, NotBoundException {
    }
    @Override
    public void AddProcessor(ProcessorClass p) throws RemoteException {
        ProcessorList.add(p);
        System.out.println("Adicionei o "+ p.getLink());
    }
    public void RemoveProcessor(String link) throws RemoteException, InterruptedException {
        if(ProcessorList.size()>0)
        {
            for(int i=0;i<ProcessorList.size();i++)
            {
                if(ProcessorList.get(i).getLink().equals(link))
                {
                   // sleep(30000);
                    if(best!=null)
                    if(best.getLink().equals(link))
                    {
                        best=null;
                    }
                    System.out.println("Removi o "+ProcessorList.get(i).getLink());
                    ProcessorList.remove(i);
                }
            }
        }
    }
    public UUID SendRequest(RequestClass r) throws IOException, NotBoundException, InterruptedException
    {
         best=CordenadorInte.BestProcessor();
         if(best!=null)
         {
             ProcessorInterface ProcessorInte = (ProcessorInterface) Naming.lookup(best.getLink());
             ProcessorInte.Send(r);
             r.setIdentificadorProcessor(best.getIdentificador());
             RequestList.add(r);
             best=null;
             return r.getIdentificadorProcessor();
         }
         else{
             return null;
         }
    }
}