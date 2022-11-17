import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static Registry r = null;
    public  static FileManager File;
    public static void main(String[] args) {
        try{
            r = LocateRegistry.createRegistry(2022);
        } catch(RemoteException a){
            a.printStackTrace();
        }
        try{
            File=new FileManager();
            r.rebind("Storage", File );
            System.out.println("Storage ready");
        } catch(Exception e) {
            System.out.println("Error Storage Main : " + e.getMessage());
        }
    }
}