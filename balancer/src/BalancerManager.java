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
    volatile ArrayList<ProcessorClass> ProcessorList = new ArrayList<ProcessorClass>();
    volatile ArrayList<RequestClass> RequestList = new ArrayList<RequestClass>();
    ProcessorClass best;
    ProcessorInterface ProcessorBackup;
    CordenadorInterface CordenadorInte = (CordenadorInterface)  Naming.lookup("rmi://localhost:2026/Cordenador");
    protected BalancerManager() throws IOException, NotBoundException {
    }
    @Override
    public void AddProcessor(ProcessorClass p) throws RemoteException {
        ProcessorList.add(p);
        System.out.println("Adicionei o "+ p.getLink());
    }
    public void RemoveProcessor(String link) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException {
        if(ProcessorList.size()>0)
        {
            for(int i=0;i<ProcessorList.size();i++)
            {
                if(ProcessorList.get(i).getLink().equals(link))
                {
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

    public synchronized void ResumeTasks(ProcessorClass p) throws IOException, NotBoundException, InterruptedException {
           for(int j=0;j<RequestList.size();j++)
            {
                if(RequestList.get(j).getIdentificadorProcessor().equals(p.getIdentificador()))
                {
                    if(RequestList.get(j).getIdentificadorProcessorBackup()!=null) {
                        ProcessorInterface Process = (ProcessorInterface) Naming.lookup(RequestList.get(j).getIdentificadorProcessorBackup());
                        Process.EXECBACKUP(p.getIdentificador());
                    }
                }
            }
    }

    public synchronized String GetLinkProcessor(String identificador) throws RemoteException
    {
        for (int i=0;i<ProcessorList.size();i++)
        {
            if(ProcessorList.get(i).getIdentificador().toString().equals(identificador))
            {
                return ProcessorList.get(i).getLink();
            }
        }
        return null;
    }

    public synchronized UUID SendRequest(RequestClass r) throws IOException, NotBoundException, InterruptedException
    {
        int x=0;
        best=CordenadorInte.BestProcessor(); //retorna melhor processador
        ProcessorClass backup;
        System.out.println("best: "+best.getLink());
        backup=CordenadorInte.BackupProcessor(best); //vai retornar um processador diferente do que vai receber o request
        if(backup==null) {x=1;}
        else {System.out.println("back: "+backup.getLink());}

        if(best!=null)
         {
             ProcessorInterface ProcessorInte = (ProcessorInterface) Naming.lookup(best.getLink());
             r.setIdentificadorProcessor(best.getIdentificador());
             if (x!=1)
             {
               r.setIdentificadorProcessorBackup(backup.getLink());
               ProcessorBackup = (ProcessorInterface) Naming.lookup(backup.getLink());
               ProcessorBackup.ADDBackupList(r);
               ProcessorBackup=null;
             }
             ProcessorInte.Send(r);
             RequestList.add(r);
             best=null;
             return r.getIdentificadorProcessor();
         }
         else{return null;}
    }
}