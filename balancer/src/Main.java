import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
public class Main implements Serializable {
    public static Registry r=null;
    public  static BalancerManager Balancer;

    public static void main(String[] args) {
        try{
            r = LocateRegistry.createRegistry(2023);
        }catch(RemoteException a){
            a.printStackTrace();
        }
        try{
            Balancer=new BalancerManager();
            r.rebind("Balancer", Balancer );
            System.out.println("Balancer ready");
        }catch(Exception e) {
            System.out.println("->"+e.getMessage());
        }
    }
}
