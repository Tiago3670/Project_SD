import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface ProcessorInterface extends Remote {
    public void Send (RequestClass r) throws IOException, InterruptedException;
    public void Exec(String url) throws IOException, InterruptedException;

    public void RemoveRequest(RequestClass r) throws RemoteException;

    public void EXECBACKUP(UUID identificadorProcessor) throws IOException, InterruptedException ;

    public void ADDBackupList(RequestClass r) throws RemoteException;
    public  int GetEstado(String r) throws RemoteException;


}
