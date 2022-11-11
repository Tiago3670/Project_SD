import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface {

    RequestClass request;
    FileClass Script;

    FileClass f;
    FileInterface FileInte=(FileInterface) Naming.lookup("rmi://localhost:2022/Storage");


    protected ProcessorManager() throws RemoteException, MalformedURLException, NotBoundException {
    }


    public ProcessorClass GetProcessor(ProcessorClass p) throws RemoteException {
        return p;
    }

    public void Send(RequestClass r) throws IOException, InterruptedException {
           request=r;
           if(request==null)
               return;

           f=FileInte.GetFile(request.getIdentificadorFile());
           if(f==null)
               return;

           Script=request.getScript();
           System.out.println("é nome do file é "+f.getName());
           System.out.println("é nome do script é "+Script.getName());
           request.setEstadoConcluido();
    }


    public int GetEstado() throws RemoteException {
        if(request==null)
            return 0;
        else
        return request.getEstado();
    }


}
