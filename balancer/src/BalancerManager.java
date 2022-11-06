import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface{
    ArrayList<ProcessorClass> ProcessorList = new ArrayList<ProcessorClass>();
    ArrayList<RequestClass> RequestList = new ArrayList<RequestClass>();

    ReplicaInterface ReplicaInte = (ReplicaInterface) Naming.lookup("rmi://localhost:2021/Replica");
    //ProcessorInterface Processor = (ProcessorInterface) Naming.lookup("rmi://localhost:"+ ProcessorList.get(i).getPort()+"/Processor");
    ProcessorInterface Processor = (ProcessorInterface) Naming.lookup("rmi://localhost:2024/Processor");


    protected BalancerManager() throws RemoteException, MalformedURLException, NotBoundException {}

    @Override
    public void SendRequest(RequestClass r) throws RemoteException, MalformedURLException, NotBoundException {
        UUID processorDisp=FirstProcessor();
        System.out.println(processorDisp);
        if(processorDisp!=null)
        {

            for(int i=0;i<ProcessorList.size();i++)
            {
                if(ProcessorList.get(i).getIdentificador().equals(processorDisp))
                {
                    r.setIdentificadorProcessor(processorDisp);
                    RequestList.add(r);
                    Processor.Send(r);
                }
            }
        }
    }

    @Override
    public void EndRequest(UUID request) throws RemoteException
    {

        for(int i=0;i<RequestList.size();i++) {
            if(RequestList.get(i).getIdentificadorRequest().equals(request))
            {
                RequestList.get(i).setEstadoConcluido();
                for(int j=0;j<ProcessorList.size();j++)
                {
                    if(ProcessorList.get(j).getIdentificador().equals(RequestList.get(i).getIdentificadorProcessor()))
                    {
                       ProcessorList.get(j).setEstadoToDisponivel();
                       System.out.println("Processador "+ProcessorList.get(i).getIdentificador()+" Processou o request "+RequestList.get(j).getIdentificadorRequest());
                    }
                }
            }
        }
    }

    @Override
    public UUID FirstProcessor() throws RemoteException, MalformedURLException, NotBoundException {
        ViewProcessors();
        if(ProcessorList.size()>=0) {
            for (int i = 0; i < ProcessorList.size(); i++) {
                if (ProcessorList.get(i).getEstado() == 1) {
                    System.out.println("O processador é  " + ProcessorList.get(0).getIdentificador());
                    ProcessorList.get(i).setEstadoToOcupado();
                    return ProcessorList.get(i).getIdentificador();
                }
            }
        }
        return null;
    }

    @Override
    public void ViewProcessors() throws RemoteException {
        if(ReplicaInte.ProcessorList()!=null)
        ProcessorList=ReplicaInte.ProcessorList();
        //System.out.println("Existem "+ProcessorList.size() + " Processadores");

    }
}
