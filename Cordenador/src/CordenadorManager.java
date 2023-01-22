import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CordenadorManager extends UnicastRemoteObject implements CordenadorInterface , Serializable {
    BalancerInterface BalancerInte = null;
    ConcurrentHashMap<String, ProcessorClass> ProcessorMap = new ConcurrentHashMap<>();
    protected MulticastSocket socket = null;
    protected DatagramSocket socket2 = null;
    InetAddress group2;
    InetAddress group;
    ProcessorClass best;
    protected byte[] buf = new byte[256];
    protected byte[] buf2 = new byte[256];
    volatile boolean balancer=false;
    volatile boolean processors=false;

    protected CordenadorManager() throws IOException, NotBoundException {
        ProcessorReciver();
        CheckProcessors();
        SendAliveBeat();

    }

    public synchronized void SendAliveBeat()
    {
        Thread threadAliveCordenador= (new Thread() {
            public void run() {

                while (true) {
                    if (processors == true) {
                        String message = "ALIVE";
                        try {
                            socket2 = new DatagramSocket();
                            group2 = InetAddress.getByName("239.0.0.0");
                            buf2 = message.getBytes();
                            DatagramPacket packet = new DatagramPacket(buf2, buf2.length, group2, 4447);
                            socket2.send(packet);
                            socket2.close();
                            sleep(1000);
                        } catch (InterruptedException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        threadAliveCordenador.start();
    }
    public synchronized  void ProcessorReciver () throws IOException, NotBoundException {
        Thread threadCordenador = (new Thread() {
            public void run()
            {
                try {
                    socket = new MulticastSocket(4446);
                    group = InetAddress.getByName("230.0.0.0");
                    socket.joinGroup(group);
                    String received;
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);
                        received = new String(packet.getData(), 0, packet.getLength());
                        String []portstr = received.split(",");
                        if ("end".equals(received)) {
                            break;
                        }
                        String Link = null;
                        Link = "rmi://localhost:" + portstr[0] + "/Processor";

                        if(portstr[2].equals("setup")) //criar o processador
                        {
                            double CPUusage=Double.parseDouble(portstr[1]);
                            ProcessorMap.put(Link, new ProcessorClass(Integer.parseInt(portstr[0])));
                            System.out.println("add->"+Link);
                            processors=true;
                            SendProcessors(Link,CPUusage);
                        } else if (portstr[2].equals("update")) //upadate processor
                        {
                            if(ProcessorMap.containsKey(Link))
                            {
                                ProcessorClass p = ProcessorMap.get(Link);
                                p.setCpuusage(Double.parseDouble(portstr[1]));
                                Instant d =Instant.now();
                                p.setEstado(d);
                            }
                        }
                    }
                    socket.leaveGroup(group);
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadCordenador.start();
    }
    public synchronized void CheckProcessors( )
    {
        Thread theardcheckativos = (new Thread() {
            public void run()
            {
                while (true) {
                    if(ProcessorMap.size()>0)
                    {
                        Instant  current, interval,date_Processor;
                        current = Instant.now();

                        for(Map.Entry<String, ProcessorClass> p : ProcessorMap.entrySet())
                        {
                            date_Processor= p.getValue().getEstado();
                            interval = Instant.ofEpochSecond(ChronoUnit.SECONDS.between(date_Processor,current));
                           if(interval.getEpochSecond()>25) //se o intervalo de tempo passar os 30 segundos significa que ja
                            {                               // não recebemos sinal deste processador há 30 segundos
                                //notificar o balancer
                                try {
                                    if(balancer=true)
                                    {
                                        BalancerInte= (BalancerInterface) Naming.lookup("rmi://localhost:2023/Balancer");
                                        BalancerInte.RemoveProcessor(p.getKey()); // dizer ao balanceador para remover o processador
                                    }
                                    System.out.println("Remove-> "+p.getKey());
                                    RemoveProcessor(p.getKey()); //remover e recuperar as tarefas
                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }
                                catch (NotBoundException e) {
                                    throw new RuntimeException(e);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        theardcheckativos.start();
    }

    public  void RemoveProcessor(String link) throws NotBoundException, IOException, InterruptedException {

        if(ProcessorMap.containsKey(link))
        {
            ProcessorClass p = ProcessorMap.get(link);
            if (p.getProcessorBackup()!=null)
            {
                ResumeTasks(p.getProcessorBackup(),p.getIdentificador());
            }
            ProcessorMap.remove(link);
        }
    }

    public void ResumeTasks(String link, UUID identificador) throws IOException, InterruptedException, NotBoundException {
        System.out.println("Vou recuperar as tarefas do "+ identificador + "no "+link);
        ProcessorInterface Process = (ProcessorInterface) Naming.lookup(link);
        Process.EXECBACKUP(identificador);
    }
    @Override
    public void SendProcessors(String Link,Double CpuUsage) throws RemoteException, MalformedURLException, NotBoundException {
        if(balancer==true) {
            BalancerInte = (BalancerInterface) Naming.lookup("rmi://localhost:2023/Balancer");
            ProcessorClass p = null;
            if (ProcessorMap.containsKey(Link)) {
                p = ProcessorMap.get(Link);
                BalancerInte.AddProcessor(p);
                p = null;
            }
        }
    }
    public ProcessorClass BestProcessor() throws RemoteException
    {
        String firstKey = ProcessorMap.keySet().iterator().next();
        best = ProcessorMap.get(firstKey);
        for(Map.Entry<String, ProcessorClass> p : ProcessorMap.entrySet())
        {
            if(p.getValue().getCpuusage()>best.getCpuusage())
            {
               best=p.getValue();
            }
        }

        best.setProcessorBackup(BackupProcessor(best));
        System.out.println("------------------------------");
        System.out.println("best processor:"+best.getLink());
        System.out.println("backup processor:"+best.getProcessorBackup());
        System.out.println("------------------------------");
        return best;
    }
    public String BackupProcessor(ProcessorClass P) throws RemoteException
    {
        for(Map.Entry<String, ProcessorClass> p : ProcessorMap.entrySet())
        {
            if(!p.getValue().getLink().equals(P.getLink()))
            {
                return p.getValue().getLink();
            }
        }
      return null;
    }

    public synchronized ConcurrentHashMap sendAllProcessors() throws RemoteException, MalformedURLException, NotBoundException {
        balancer=true;
        return  ProcessorMap;
    }


}
