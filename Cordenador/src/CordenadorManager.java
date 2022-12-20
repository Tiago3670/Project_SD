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

public class CordenadorManager extends UnicastRemoteObject implements CordenadorInterface , Serializable {
    BalancerInterface BalancerInte = null;
    ArrayList<RequestClass> RequestList = new ArrayList<RequestClass>();

    ArrayList<ProcessorClass> ProcessorList = new ArrayList<ProcessorClass>();
    protected MulticastSocket socket = null;
    InetAddress group;
    DecimalFormat df = new DecimalFormat("#%");
    ProcessorClass best;
    protected byte[] buf = new byte[256];


    protected CordenadorManager() throws IOException, NotBoundException {
        ProcessorReciver();
        CheckProcessors(null);
    }

    public  void ProcessorReciver () throws IOException, NotBoundException {
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
                            ProcessorList.add(new ProcessorClass(Integer.parseInt(portstr[0])));
                            System.out.println("add->"+Link);
                            SendProcessors(Link,CPUusage);
                        } else if (portstr[2].equals("update")) //upadate processor
                        {
                            for(int j=0;j<ProcessorList.size();j++)
                            {
                                if(ProcessorList.get(j).getLink().equals(Link))
                                {
                                    ProcessorList.get(j).setCpuusage(Double.parseDouble(portstr[1]));
                                    Instant d =Instant.now();
                                    ProcessorList.get(j).setEstado(d);
                                }
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
    public void CheckProcessors(String link)
    {
        Thread theardcheckativos = (new Thread() {
            public void run()
            {
                while (true) {
                    if(ProcessorList.size()>0)
                    {
                        Instant  current, interval,date_Processor;
                        current = Instant.now();

                        for(int j=0;j<ProcessorList.size();j++)
                        {
                            date_Processor= ProcessorList.get(j).getEstado();
                            interval = Instant.ofEpochSecond(ChronoUnit.SECONDS.between(date_Processor,current));
                           // System.out.println("seconds bettew="+interval.getEpochSecond());
                           if(interval.getEpochSecond()>25) //se o intervalo de tempo passar os 30 segundos significa que ja
                            {                               // não recebemos sinal deste processador há 30 segundos
                                //notificar o balancer
                                try {
                                    RemoveProcessor(ProcessorList.get(j));
                                    BalancerInte.RemoveProcessor(ProcessorList.get(j).getLink());
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
                                System.out.println("O "+ProcessorList.get(j).getLink()+" Rebentou");
                                ProcessorList.remove(j);
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

    public  void RemoveProcessor(ProcessorClass p) throws NotBoundException, IOException, InterruptedException {
      BalancerInte.ResumeTasks(p);
    }
    @Override
    public void SendProcessors(String Link,Double CpuUsage) throws RemoteException, MalformedURLException, NotBoundException {
        BalancerInte=(BalancerInterface) Naming.lookup("rmi://localhost:2023/Balancer");

        for(int i=0;i<ProcessorList.size();i++) {
            if (ProcessorList.get(i).getLink().equals(Link)) {
                ProcessorList.get(i).setCpuusage(CpuUsage);
                BalancerInte.AddProcessor(ProcessorList.get(i));
                return;
            }
        }
    }
    public ProcessorClass BestProcessor() throws RemoteException
    {
        best=ProcessorList.get(0);
        for(int i=0;i<ProcessorList.size();i++)
        {
            if(ProcessorList.get(i).getCpuusage()<best.getCpuusage())
            {
              return best=ProcessorList.get(i);
            }
        }
        System.out.println("best processor:"+best.getLink());
        return best;
    }
    public ProcessorClass BackupProcessor(ProcessorClass P)
    {
        ProcessorClass Backup;
        if(ProcessorList.size()==1)
        {
            return null;
        }

        for(int i=0;i<ProcessorList.size();i++)
        {
            if(ProcessorList.get(i).getLink()!=P.getLink())
            {
                return  ProcessorList.get(i);
            }
        }
        return  null;

    }

}
