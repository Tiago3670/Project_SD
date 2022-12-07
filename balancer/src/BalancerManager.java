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

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface , Serializable {
    private static final long serialVersionUID = 4000529155687724350L;

    //HashMap<String, Double> hashprocessors = new HashMap<String, Double>();
    ArrayList<ProcessorClass> ProcessorList = new ArrayList<ProcessorClass>();
    DecimalFormat df = new DecimalFormat("#%");
    ProcessorClass best;CordenadorInterface CordenadorInte = (CordenadorInterface)  Naming.lookup("rmi://localhost:2026/Cordenador");
    protected BalancerManager() throws IOException, NotBoundException {
         // MulticastReceiver();
    }



    public void GetProcessors()
    {
      /*  ProcessorList= CordenadorInte.GetProcessores();
        for(int i=0;i<ProcessorList.size();i++)
        {
           System.out.println(ProcessorList.get(i).getLink());
        }
        System.out.println("fim");
        System.out.println();*/
        System.out.println("fim");

    }

    @Override
    public void AddProcessor(ProcessorClass p) throws RemoteException {
        ProcessorList.add(p);
        System.out.println("Adicionei o "+ p.getLink());
    }

    public UUID SendRequest(RequestClass r) throws IOException, NotBoundException, InterruptedException {
         best= BestProcessor();
         String Link= best.getLink();
         ProcessorInterface ProcessorInte = (ProcessorInterface) Naming.lookup(Link);
         ProcessorInte.Send(r);
         r.setIdentificadorProcessor(best.getIdentificador());
         return r.getIdentificadorProcessor();
    }
    public ProcessorClass BestProcessor()
    {
        best=ProcessorList.get(0);
        for(int i=1;i<ProcessorList.size();i++)
        {
            if(ProcessorList.get(i).getCpuusage()<best.getCpuusage())
            {
                best=ProcessorList.get(i);
            }
        }
        return best;
    }


}