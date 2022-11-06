import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.UUID;

public interface ReplicaInterface extends Remote {

    void ADDProcessor(ProcessorClass p) throws RemoteException;
    ArrayList<ProcessorClass> ProcessorList() throws RemoteException;
    void Ocupado(UUID processorID) throws RemoteException;

}
