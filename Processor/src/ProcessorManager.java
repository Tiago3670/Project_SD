import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;

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
        StringBuilder output = new StringBuilder();
        String command = "cmd /c " + url + "\"" + f.getUrlDir() +"\""+ request.getIdentificadorFile() + "\"";
        Process process = Runtime.getRuntime().exec(command);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + System.lineSeparator());
                }
                request.setEstadoConcluido();
                System.out.println(output);
                FileInte.SubmitOutput(request.getIdentificadorRequest().toString(),f);
            } catch (RemoteException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

}
