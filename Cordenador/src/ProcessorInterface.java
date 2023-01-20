import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface ProcessorInterface extends Remote {
    public int GetEstado() throws RemoteException;
    public void Exec(String url) throws IOException;
    public void EXECBACKUP(UUID identificadorProcessor) throws IOException, InterruptedException ;

}
