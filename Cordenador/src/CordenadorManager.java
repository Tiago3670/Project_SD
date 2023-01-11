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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CordenadorManager extends UnicastRemoteObject implements CordenadorInterface , Serializable {
    BalancerInterface BalancerInte = null;
    ConcurrentHashMap<String, ProcessorClass> ProcessorMap = new ConcurrentHashMap<>();

    protected MulticastSocket socket = null;
    InetAddress group;
    ProcessorClass best;
    protected byte[] buf = new byte[256];


    protected CordenadorManager() throws IOException, NotBoundException {
        ProcessorReciver();
        CheckProcessors(null);
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
                        //System.out.println(portstr[2]);
                        String Link = null;
                        Link = "rmi://localhost:" + portstr[0] + "/Processor";
                        if(portstr[2].equals("setup")) //criar o processador
                        {
                            double CPUusage=Double.parseDouble(portstr[1]);
                           // System.out.println("Processor:"+ Link +" "+df.format(CPUusage));
                           // ProcessorList.add(new ProcessorClass(Integer.parseInt(portstr[0])));
                            ProcessorMap.put(Link, new ProcessorClass(Integer.parseInt(portstr[0])));
                            System.out.println("add->"+Link);
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
    public synchronized void CheckProcessors(String link)
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
                           // System.out.println("seconds bettew="+interval.getEpochSecond());
                           if(interval.getEpochSecond()>25) //se o intervalo de tempo passar os 30 segundos significa que ja
                            {                               // não recebemos sinal deste processador há 30 segundos
                                //notificar o balancer
                                try {
                                    RemoveProcessor(p.getKey()); //resumir as tarefas
                                    BalancerInte.RemoveProcessor(p.getKey()); // dizer ao balanceador para remover o utilizador
                                    System.out.println("Remove-> "+p.getKey());
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
        if(ProcessorMap.containsKey(link)) {
            ProcessorClass p = ProcessorMap.get(link);
            BalancerInte.ResumeTasks(p);
            ProcessorMap.remove(link);
        }
    }
    @Override
    public void SendProcessors(String Link,Double CpuUsage) throws RemoteException, MalformedURLException, NotBoundException {
        BalancerInte=(BalancerInterface) Naming.lookup("rmi://localhost:2023/Balancer");
        ProcessorClass p=null;
        if(ProcessorMap.containsKey(Link))
        {
            p=ProcessorMap.get(Link);
            p.setCpuusage(CpuUsage);
            BalancerInte.AddProcessor(p);
            p=null;
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
        System.out.println("best processor:"+best.getLink());
        return best;
    }
    public ProcessorClass BackupProcessor(ProcessorClass P)
    {
        ProcessorClass backup = null;
        for(Map.Entry<String, ProcessorClass> p : ProcessorMap.entrySet())
        {
            if(!p.getValue().getLink().equals(P.getLink()))
            {
                backup=p.getValue();
            }
        }
        return  backup;
    }

}
