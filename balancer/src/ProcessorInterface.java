import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface ProcessorInterface extends Remote {
    public void Send (RequestClass r) throws IOException, InterruptedException;
    public int GetEstado() throws RemoteException;
    public void Exec(String url) throws IOException;
    public void EXECBACKUP(UUID identificadorProcessor) throws IOException, InterruptedException ;
    public void ADDBackupList(RequestClass r) throws RemoteException;



}
