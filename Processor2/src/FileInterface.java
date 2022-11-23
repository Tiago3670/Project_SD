import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileInterface extends Remote {
    String SendFile(FileClass f) throws RemoteException;
    void SubmitOutput(FileClass f,String IDRequest,String FOutput) throws RemoteException;
    void ToFile(FileClass f) throws IOException;
    FileClass GetFile(String UIDD) throws IOException;
    String GetOutput(String IdentificadorRequest) throws IOException;
}
