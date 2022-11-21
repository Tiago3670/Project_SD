import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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



    public void Send(RequestClass r) throws IOException, InterruptedException {
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

    public void Exec(String url) throws IOException, InterruptedException {
        f = FileInte.GetFile(request.getIdentificadorFile());
        byte[] scriptfile = Base64.getDecoder().decode(f.FileBase64().getBytes(StandardCharsets.UTF_8));
        String scriptdecode = new String(scriptfile, StandardCharsets.UTF_8);
        byte[] script = Base64.getDecoder().decode(url.getBytes(StandardCharsets.UTF_8));

        File batfile= new File("temp.bat");
        String x;

        FileOutputStream out=new FileOutputStream(batfile);
        out.write(script);
        out.flush();
        out.close();


        StringBuilder output = new StringBuilder();
        //String command = "cmd /c " + url + " " + f.getUrlDir() + "\""+ request.getIdentificadorFile() + "\"";
        String command = "cmd /c " + batfile + " \""+ scriptdecode +"\"";

        System.out.println(command);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + System.lineSeparator());
                }
                System.out.println(output);
                if(output!=null)
                {
                    request.setEstadoConcluido();
                    FileInte.SubmitOutput(f,request.getIdentificadorRequest().toString(),output.toString());
                }

            } catch (RemoteException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
      batfile.delete();
    }

}
