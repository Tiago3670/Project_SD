import java.io.IOException;
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

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface {
    HashMap<String, Double> hashprocessors = new HashMap<String, Double>();
    ArrayList<ProcessorClass> ProcessorList = new ArrayList<ProcessorClass>();
    protected MulticastSocket socket = null;
    InetAddress group;
    DecimalFormat df = new DecimalFormat("#%");
    ProcessorClass best;
    protected byte[] buf = new byte[256];

    protected BalancerManager() throws IOException, NotBoundException {
          MulticastReceiver();
    }
    public UUID SendRequest(RequestClass r) throws IOException, NotBoundException, InterruptedException {
         best= BestProcessor();
         String Link= best.getLink();
        if (hashprocessors.containsKey(Link))
        {
            ProcessorInterface ProcessorInte = (ProcessorInterface) Naming.lookup(Link);
            ProcessorInte.Send(r);
            r.setIdentificadorProcessor(best.getIdentificador());
        }

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

    public  void MulticastReceiver () throws  IOException
    {
        Thread threadBalancer = (new Thread() {
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
                        double CPUusage=Double.parseDouble(portstr[1]);
                        String Link = "rmi://localhost:" + portstr[0] + "/Processor";
                        System.out.println("Processor:"+ Link +" "+df.format(CPUusage));
                        if ("end".equals(received)) {
                            break;
                        }
                        if (!hashprocessors.containsKey(Link)) {
                            hashprocessors.put(Link, CPUusage);
                            ProcessorList.add(new ProcessorClass(Integer.parseInt(portstr[0])));
                            for(int i=0;i<ProcessorList.size();i++)
                            {
                                if(ProcessorList.get(i).getPort()==Integer.parseInt(portstr[0]))
                                {
                                    ProcessorList.get(i).setCpuusage(CPUusage);
                                }
                            }
                        }else{
                            hashprocessors.replace(Link,CPUusage);
                           /* for(int i=0;i<ProcessorList.size();i++)
                            {
                                if(ProcessorList.get(i).getPort()==Integer.parseInt(portstr[0]))
                                {
                                    ProcessorList.get(i).Ativo();
                                }
                            }*/
                        }
                    }
                    socket.leaveGroup(group);
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        threadBalancer.start();
    }
    /*int estado;

    public int ProcessorsActive(ProcessorClass p)
    {

        Thread threadBalancer2 = (new Thread() {
            public void run()
            {
              //0-> ativo 1->desativo
                try {
                    int x=p.getOldValidated();
                    sleep(30000);
                    if(p.getOldValidated()==x)
                    {
                        hashprocessors.remove(p.getLink());
                        for(int i=0;i<ProcessorList.size();i++)
                        {
                            if(ProcessorList.get(i)==p)
                            {
                                estado=1;
                                ProcessorList.remove(i);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadBalancer2.start();
        return estado;
    }*/

}