import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

public class ReplicaManager extends UnicastRemoteObject implements ReplicaInterface {
    ArrayList<ProcessorClass> ProcessorList = new ArrayList<ProcessorClass>();

    protected ReplicaManager() throws RemoteException {
    }

    @Override
    public void ADDProcessor(ProcessorClass p) throws RemoteException {
        ProcessorList.add(p);

    }

    @Override
    public ArrayList<ProcessorClass> ProcessorList() throws RemoteException {
        if (ProcessorList.size()>0)
            return ProcessorList;
        else
            return null;

    }

    @Override
    public void Ocupado(UUID processorID) throws RemoteException {

        for(int i=0;i<ProcessorList.size();i++)
        {
            if(processorID.equals(ProcessorList().get(i).getIdentificador()))
            {
                ProcessorList.get(i).setEstadoToOcupado();
            }
        }
    }


}
