import java.io.*;
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

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface, Serializable {

    RequestClass request;
    FileClass Script;

    FileClass f;
    FileInterface FileInte=(FileInterface) Naming.lookup("rmi://localhost:2022/Storage");


    protected ProcessorManager() throws RemoteException, MalformedURLException, NotBoundException {
    }


    public ProcessorClass GetProcessor(ProcessorClass p) throws RemoteException {
        return p;
    }

    @SuppressWarnings("deprecation")

    public void Send(RequestClass r) throws IOException {
           request=r;
           if(request==null)
               return;

           f=FileInte.GetFile(request.getIdentificadorFile());
           if(f==null)
               return;
        String command="";
        Script=request.getScript();
           System.out.println("é nome do file é "+f.getName());
           System.out.println("é nome do script é "+Script.getName());
           request.setEstadoConcluido();
              byte[] scriptfile = Base64.getDecoder().decode(Script.FileBase64().getBytes(StandardCharsets.UTF_8));
             command = new String(scriptfile, StandardCharsets.UTF_8);
             StringBuilder out=new StringBuilder();
                System.out.println(command);
           try
           {
               //ProcessBuilder processBuilder = new ProcessBuilder("C:\\Users\\tiago\\OneDrive\\Área de Trabalho\\EI\\3 Ano\\SD\\Project_SD\\s.bat");
               // ProcessBuilder processBuilder = new ProcessBuilder("cmd",command);
               //Process processo = processBuilder.start();
              Process processo= Runtime.getRuntime().exec(command);
               BufferedReader read=new BufferedReader(new InputStreamReader(processo.getInputStream()));
               String line;
               while ((line=read.readLine()) != null )
               {
                   out.append(line+"\n");
               }
           }
           catch (Exception e)
           {
               e.printStackTrace();
           }

        System.out.println(out);
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
