import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static Registry r=null;
    public  static CordenadorManager Cordenador;
    public static void main(String[] args) {
        try{
            r = LocateRegistry.createRegistry(2026);
        }catch(RemoteException a){
            a.printStackTrace();
        }
        try{
            Cordenador=new CordenadorManager();
            r.rebind("Cordenador", Cordenador );
            System.out.println("Cordenador ready");
        }catch(Exception e) {
            System.out.println("->" + e.getMessage());
        }
    }
}