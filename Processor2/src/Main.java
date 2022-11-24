import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.UUID;

public class Main implements Serializable {
    public static Registry r=null;
    public  static ProcessorManager processor;

    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        UUID identificador;

        // identificador = UUID.fromString(UUID.nameUUIDFromBytes(String.valueOf(port).getBytes()).toString());;
        ProcessorClass p=new ProcessorClass(2025);
        try{
            r = LocateRegistry.createRegistry(p.getPort());
        }catch(RemoteException a){
            a.printStackTrace();
        }
        try{
            processor=new ProcessorManager(p);
            r.rebind("Processor", processor );
            System.out.println("processor ready");
        }catch(Exception e) {
            System.out.println("->" + e.getMessage());
        }
    }
}