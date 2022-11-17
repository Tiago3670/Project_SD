import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface, Serializable {

    RequestClass request;


    FileClass f;


    FileInterface FileInte=(FileInterface) Naming.lookup("rmi://localhost:2022/Storage");

    protected ProcessorManager() throws RemoteException, MalformedURLException, NotBoundException {
    }


    public ProcessorClass GetProcessor(ProcessorClass p) throws RemoteException {
        return p;
    }



    public void Send(RequestClass r) throws IOException {
           request=r;
           if(request==null)
               return;

            f=FileInte.GetFile(request.getIdentificadorFile());

           if(f==null)
               return;
            Exec(r.getUrl());
    }


    public int GetEstado() throws RemoteException {
        if(request==null)
            return 0;
        else
        return request.getEstado();
    }

    public void Exec(String url) throws IOException
    {
        try
        {
            //ProcessBuilder processBuilder = new ProcessBuilder(url);
            //Process processo = processBuilder.start();
            String command = "cmd /c " + url + "\"" + "100lines.txt" + "\"";

            //request.getIdentificadorFile()

            Process Runtime = java.lang.Runtime.getRuntime().exec(command);
            Runtime.waitFor();
            
            FileInte.SubmitOutput(request.getIdentificadorRequest().toString(), f);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        request.setEstadoConcluido();

          //este ficheiro f para já vai igual ao que vêm , a nossa ideia seria definir um ficheiro output da classe ProcessBuilder e depois no final

    }


}
