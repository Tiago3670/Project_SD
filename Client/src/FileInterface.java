import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface FileInterface extends Remote {
       void SendFile(FileClass f) throws RemoteException;
       UUID GetIdentificador() throws  RemoteException;
       void ToFile(FileClass f) throws IOException;
}
