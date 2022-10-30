import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.Serializable;
import java.util.UUID;


public class RMIServer implements  Serializable{
    public static Registry r=null;
    public  static FileManager File;

    public static  void main(String args[])
    {
        try{
            r = LocateRegistry.createRegistry(2022);
        }catch(RemoteException a){
            a.printStackTrace();
        }
        try{
            File=new FileManager();
            r.rebind("File", File );
            System.out.println("File server ready");
        }catch(Exception e) {
            System.out.println("File server main " + e.getMessage());
        }
    }

}
