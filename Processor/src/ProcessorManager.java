import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;

import static java.lang.Runtime.getRuntime;

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
           byte[] scriptfile = Base64.getDecoder().decode(Script.FileBase64().getBytes(StandardCharsets.UTF_8));
           String scriptdecode = new String(scriptfile, StandardCharsets.UTF_8);
        ProcessBuilder execut = new ProcessBuilder("cmd","/c",scriptdecode);
        Process process = execut.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + System.lineSeparator());
            }

        }
         catch (RemoteException  e) {
        e.printStackTrace();
        } catch (IOException e) {
        e.printStackTrace();
        }
        byte[] FileOutput64 = Base64.getDecoder().decode(f.FileBase64().getBytes(StandardCharsets.UTF_8));
        String StrFileOut = new String(FileOutput64, StandardCharsets.UTF_8);
        System.out.println(StrFileOut);
    }


    public int GetEstado() throws RemoteException {
        if(request==null)
            return 0;
        else
        return request.getEstado();
    }

    public void Exec() throws IOException
    {
        byte[] scriptfile = Base64.getDecoder().decode(Script.FileBase64().getBytes(StandardCharsets.UTF_8));
        //     ProcessBuilder processBuilder = new ProcessBuilder("cmd","/c",f);
        //   Process process = processBuilder.start();
    }


}
