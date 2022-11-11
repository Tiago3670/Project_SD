import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileInterface extends Remote {
    String SendFile(FileClass f) throws RemoteException;
    void ToFile(FileClass f) throws IOException;

    FileClass GetFile(String UIDD) throws IOException;

}
