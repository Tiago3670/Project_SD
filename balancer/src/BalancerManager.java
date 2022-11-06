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


    protected BalancerManager() throws RemoteException, MalformedURLException, NotBoundException {}

    @Override
    public void SendRequest(RequestClass r) throws RemoteException, MalformedURLException, NotBoundException {
        RequestList.add(r);
        ProcessorInterface Processor = (ProcessorInterface) Naming.lookup("rmi://localhost:2024/Processor");
        UUID processorDisp=FirstProcessor();

        if(processorDisp!=null)
        {
            for(int j=0;j<RequestList.size();j++)
            {
                if(RequestList.get(j).getEstado()==1 && RequestList.get(j) != r)
                {
                    System.out.println("O request "+r.getIdentificadorRequest()+" vai ser colocado em espera");
                    r=RequestList.get(j);
                    j=RequestList.size();
                }
            }
            System.out.println("O request "+r.getIdentificadorRequest()+" foi atribuido ao processador ->"+processorDisp);

            for(int i=0;i<ProcessorList.size();i++)
            {
                if(ProcessorList.get(i).getIdentificador().equals(processorDisp))
                {
                    r.setIdentificadorProcessor(processorDisp);
                    r.setEstadoProcessamento();
                    Processor.Send(r);
                }
            }

        }
        else
        {
            System.out.println("O request "+r.getIdentificadorRequest()+" vai ser colocado em espera");
        }

    }

    @Override
    public void EndRequest(UUID request) throws RemoteException
    {
        for(int i=0;i<=RequestList.size();i++) {
            if(RequestList.get(i).getIdentificadorRequest().equals(request))
            {
                for(int j=0;j<=ProcessorList.size();j++)
                {
                    if(ProcessorList.get(j).getIdentificador().equals(RequestList.get(i).getIdentificadorProcessor()))
                    {
                       if(RequestList.get(i).getEstado()==2)
                       {
                           RequestList.get(i).setEstadoConcluido();
                           System.out.println("Processador "+ProcessorList.get(j).getIdentificador()+" Processou o request "+RequestList.get(i).getIdentificadorRequest());
                           j=RequestList.size();
                           i=ProcessorList.size();
                       }
                    }
                }
            }
        }
        ViewProcessors();
    }

    @Override
    public UUID FirstProcessor() throws RemoteException, MalformedURLException, NotBoundException {
        ViewProcessors();
        if(ProcessorList.size()>=0) {
            for (int i = 0; i < ProcessorList.size(); i++) {
                if (ProcessorList.get(i).getEstado() == 1) {
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
    public int GetEStado(UUID IdentificadorRequest) throws RemoteException
    {
        if(RequestList.size()>0) {
            for (int i = 0; i < RequestList.size(); i++) {
                if (RequestList.get(i).getIdentificadorRequest().equals(IdentificadorRequest)) {
                    return RequestList.get(i).getEstado();
                }
            }
        }
        return 10; //10-> significa que não encontrou o request
    }
}
