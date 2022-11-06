import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main implements Serializable {
    public static Registry r=null;
    public  static ReplicaManager replica;
    public static void main(String[] args) {
        try{
            r = LocateRegistry.createRegistry(2021);
        }catch(RemoteException a){
            a.printStackTrace();
        }
        try{
            replica=new ReplicaManager();
            r.rebind("Replica", replica );
            System.out.println("Replica ready");
        }catch(Exception e) {
            System.out.println("->" + e.getMessage());
        }
    }
}