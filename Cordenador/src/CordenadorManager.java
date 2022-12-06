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

public class CordenadorManager extends UnicastRemoteObject implements CordenadorInterface , Serializable {
    //private static final long serialVersionUID = 2509159142972867020L;

    BalancerInterface BalancerInte = null;

    ArrayList<ProcessorClass> ProcessorList = new ArrayList<ProcessorClass>();
    protected MulticastSocket socket = null;
    InetAddress group;
    DecimalFormat df = new DecimalFormat("#%");
    ProcessorClass best;
    protected byte[] buf = new byte[256];


    protected CordenadorManager() throws IOException, NotBoundException {
        ProcessorReciver();
    }

    public  void ProcessorReciver () throws IOException
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
                        if ("end".equals(received)) {
                            break;
                        }
                        System.out.println(portstr[2]);

                        if(portstr[2].equals("setup")) //criar o processador
                        {
                            double CPUusage=Double.parseDouble(portstr[1]);
                           String Link = "rmi://localhost:" + portstr[0] + "/Processor";
                           // System.out.println("Processor:"+ Link +" "+df.format(CPUusage));
                            ProcessorList.add(new ProcessorClass(Integer.parseInt(portstr[0])));
                            System.out.println("add->"+Link);
                            SendProcessors(Link);
                        } else if (portstr[2].equals("update")) //upadate processor
                        {

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
        threadBalancer.start();

    }




    @Override
    public void SendProcessors(String Link) throws RemoteException, MalformedURLException, NotBoundException {

      /*  for(int i=0;i<ProcessorList.size();i++)
        {
            if(ProcessorList.get(i).getLink().equals(Link))
            {
                BalancerInte.AddProcessor(ProcessorList.get(0));
                return;
            }
        }*/
        notfy();
    }
    public void notfy() throws MalformedURLException, NotBoundException, RemoteException {
        BalancerInte=(BalancerInterface) Naming.lookup("rmi://localhost:2023/Balancer");
        BalancerInte.AddProcessor(ProcessorList.get(0));

    }
}
