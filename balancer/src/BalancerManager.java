import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.time.Instant;
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
    private InetAddress group2;
    protected MulticastSocket socket2 = null;
    ProcessorInterface ProcessorBackup;
    protected byte[] buf2 = new byte[256];
    volatile boolean Cordenador = true;
    volatile String linkCordenador=null;
    int failsCordenador = 0;

    CordenadorInterface CordenadorInte;
    protected BalancerManager() throws IOException, NotBoundException {
        CheckCordenador();
    }
    public synchronized void CheckCordenador() throws IOException, NotBoundException
    {
        Thread threadCordenador = (new Thread() {
            public void run() {
                try {
                    socket2 = new MulticastSocket(4447);
                    group2 = InetAddress.getByName("239.0.0.0");
                    socket2.joinGroup(group2);
                    String received = null;
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buf2, buf2.length);
                        socket2.receive(packet);
                        System.out.println(received);
                        if (received != null) {
                           String link = "rmi://localhost:" + received + "/Cordenador";
                            Cordenador = true;
                            if(linkCordenador!=null)
                            {
                                if(!linkCordenador.equals(link))
                                {
                                    linkCordenador=link;
                                    GetProcessors();
                                }
                            }
                            else {
                                linkCordenador=link;
                                GetProcessors();
                            }
                        }
                        received = new String(packet.getData(), 0, packet.getLength());
                        if ("end".equals(received)) {
                            break;
                        }
                    }
                    socket2.leaveGroup(group2);
                    socket2.close();
                } catch (IOException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }
         });
        threadCordenador.start();
    }



    @Override
    public void AddProcessor(ProcessorClass p) throws RemoteException {
        if (ProcessorMap.containsKey(p.getLink())) {
            System.out.println("já existe o " + p.getLink());
        } else {
            ProcessorMap.put(p.getLink(), p);
            System.out.println("Adicionei o " + p.getLink());
        }
    }

    public void RemoveProcessor(String link) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException {
        if (ProcessorMap.size() > 0) {
            if (ProcessorMap.containsKey(link)) {
                ProcessorMap.remove(link);
                System.out.println("Removi o " + link);
                if (best != null)
                    if (best.getLink().equals(link)) {
                        best = null;
                    }
            }
        }
    }

    public  void GetProcessors() throws RemoteException, MalformedURLException, NotBoundException {
        if(Cordenador==true) {
            System.out.println(linkCordenador);
            CordenadorInterface CordenadorInte = (CordenadorInterface) Naming.lookup(linkCordenador);
            ProcessorMap.clear();
            ProcessorMap = CordenadorInte.sendAllProcessors();
            if (ProcessorMap.size() > 0) {
                for (Map.Entry<String, ProcessorClass> p : ProcessorMap.entrySet()) {
                    System.out.println("Adicionei o " + p.getKey());
                }
            }
        }
    }

    public  String GetLinkProcessor(String identificador) throws RemoteException {
        for (Map.Entry<String, ProcessorClass> p : ProcessorMap.entrySet()) {
            if (p.getValue().getIdentificador().toString().equals(identificador)) {
                return p.getKey();
            }
        }
        return null;
    }

    @Override
    public void CordenadorFail() throws RemoteException {
        failsCordenador++;
        if (failsCordenador == ProcessorMap.size()) {
            Cordenador = false;
            System.out.println("Cordenador Falhou!");
        }
    }

    public synchronized UUID SendRequest(RequestClass r) throws IOException, NotBoundException, InterruptedException {
        if (Cordenador == true) {
            CordenadorInterface CordenadorInte = (CordenadorInterface) Naming.lookup(linkCordenador);
            int x = 0;
            best = CordenadorInte.BestProcessor(); //retorna melhor processador
            System.out.println("Request enviado para o processador  " + best.getLink() + ", com backup no processador " + best.getProcessorBackup());

            if (best != null) {
                ProcessorInterface ProcessorInte = (ProcessorInterface) Naming.lookup(best.getLink());
                r.setIdentificadorProcessor(best.getIdentificador());
                if (best.getProcessorBackup() != null) {
                    r.setIdentificadorProcessorBackup(best.getProcessorBackup());
                    ProcessorBackup = (ProcessorInterface) Naming.lookup(best.getProcessorBackup());
                    ProcessorBackup.ADDBackupList(r);
                    ProcessorBackup = null;
                }
                ProcessorInte.Send(r);
                RequestMap.put(r.getIdentificadorRequest().toString(), r);
                best = null;
                return r.getIdentificadorProcessor();
            } else {
                return null;
            }
        }
        else
        {
            System.out.println("Codenador não detetado");
            return null;
        }
    }

}
