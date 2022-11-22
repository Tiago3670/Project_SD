import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];

    protected BalancerManager() throws IOException, NotBoundException {
            socket = new MulticastSocket(4446);
            InetAddress group = InetAddress.getByName("230.0.0.0");
            socket.joinGroup(group);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(
                        packet.getData(), 0, packet.getLength());
                if ("end".equals(received)) {
                    break;
                }
            }
        socket.leaveGroup(group);
        socket.close();
    }

    public UUID SendRequest(RequestClass r) throws IOException, NotBoundException, InterruptedException {

        ProcessorInterface ProcessorInte = (ProcessorInterface) Naming.lookup("rmi://localhost:2024/Processor");
        ProcessorInte.Send(r);
        r.setIdentificadorProcessor(UUID.fromString(UUID.nameUUIDFromBytes(String.valueOf(2024).getBytes()).toString()));
        return r.getIdentificadorProcessor();
    }
}