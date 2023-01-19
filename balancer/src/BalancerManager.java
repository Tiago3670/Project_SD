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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface , Serializable {

    ConcurrentHashMap<String, ProcessorClass> ProcessorMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, RequestClass> RequestMap = new ConcurrentHashMap<>();
    ProcessorClass best;
    ProcessorInterface ProcessorBackup;
    CordenadorInterface CordenadorInte = (CordenadorInterface)  Naming.lookup("rmi://localhost:2026/Cordenador");
    protected BalancerManager() throws IOException, NotBoundException {
        GetProcessors();
    }
    @Override
    public void AddProcessor(ProcessorClass p) throws RemoteException {
       if(ProcessorMap.containsKey(p.getLink())) {
            System.out.println("já existe o "+ p.getLink());
        }
       else {
           ProcessorMap.put(p.getLink(),p);
           System.out.println("Adicionei o "+ p.getLink());
       }
    }
    public void RemoveProcessor(String link) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException {
        if(ProcessorMap.size()>0)
        {
            if(ProcessorMap.containsKey(link))
            {
                ProcessorMap.remove(link);
                System.out.println("Removi o "+link);
                if(best!=null)
                    if(best.getLink().equals(link))
                    {
                        best=null;
                    }
            }
        }
    }
    public synchronized void GetProcessors() throws RemoteException {
        ProcessorMap.clear();
        ProcessorMap=CordenadorInte.sendAllProcessors();
        if(ProcessorMap.size()>0) {
            for (Map.Entry<String, ProcessorClass> p : ProcessorMap.entrySet()) {
                System.out.println("Adicionei o " + p.getKey());
            }
        }
    }
    public synchronized String GetLinkProcessor(String identificador) throws RemoteException
    {
        for(Map.Entry<String, ProcessorClass> p : ProcessorMap.entrySet())
        {
            if(p.getValue().getIdentificador().equals(identificador))
            {
                return p.getKey();
            }
        }
            return null;
    }

    public synchronized UUID SendRequest(RequestClass r) throws IOException, NotBoundException, InterruptedException
    {
        int x=0;
        best=CordenadorInte.BestProcessor(); //retorna melhor processador
        System.out.println("Request enviado para o processador  "+best.getLink()+ ", com backup no processador "+ best.getProcessorBackup());

        if(best!=null) {
             ProcessorInterface ProcessorInte = (ProcessorInterface) Naming.lookup(best.getLink());
             r.setIdentificadorProcessor(best.getIdentificador());
             if (best.getProcessorBackup()!=null) {
               r.setIdentificadorProcessorBackup(best.getProcessorBackup());
               ProcessorBackup = (ProcessorInterface) Naming.lookup(best.getProcessorBackup());
               ProcessorBackup.ADDBackupList(r);
               ProcessorBackup=null;
             }
             ProcessorInte.Send(r);
             RequestMap.put(r.getIdentificadorRequest().toString(),r);
             best=null;
             return r.getIdentificadorProcessor();
         }
         else {
             return null;
         }
    }
}