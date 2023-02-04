import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FileManager extends UnicastRemoteObject implements FileInterface  {
    //private ArrayList<FileClass> ListFile = new ArrayList<FileClass>();
    ConcurrentHashMap<String, FileClass> FileMap = new ConcurrentHashMap<>();

    ArrayList<String> DoneRequest=new ArrayList<>();
    //private String dir = "C:/Users/dseabra/IdeaProjects/2022/Project_SD/teste";
  //  private String dir = "C:/Users/Tiago Almeida/Desktop/SDT/storage";
    private String dir = "C:/Users/Tiago/Desktop/SDT/storage";

    protected FileManager() throws RemoteException {
    }
    @Override
    public String SendFile(FileClass f) throws RemoteException {
            UUID identificador;
            identificador = UUID.fromString(UUID.nameUUIDFromBytes(String.valueOf(f.getName()).getBytes()).toString());;
            f.setIdentificador(identificador);
            try {
                ToFile(f);
            } catch (Exception e) {
                e.printStackTrace();
            }
           FileMap.put(f.getIdentificadorFile(),f);
            return f.getIdentificadorFile();
    }
    @Override
    public void SubmitOutput(FileClass f,String IDRequest,String FOutput) throws IOException {

        if(FileMap.containsKey(f.getIdentificadorFile()))
        {
            File file= new File(dir+"/"+f.getIdentificadorFile()+".txt");
            FileOutputStream out=new FileOutputStream(file);
            out.write(FOutput.getBytes());
            out.flush();
            out.close();
            DoneRequest.add(IDRequest);
        }

    }
   public String GetResult(String IdentificadorRequest) throws IOException
   {
        for(int i=0;i<DoneRequest.size();i++)
        {
            if(IdentificadorRequest.equals(DoneRequest.get(i)))
            {
                    return  IdentificadorRequest + " Script Done";
            }
        }
        return "Request não encontrado!";
   }
    public void ToFile(FileClass f) throws IOException {
        byte[] clientfile = Base64.getDecoder().decode(f.FileBase64().getBytes(StandardCharsets.UTF_8));
        Path serverpath = Paths.get(dir, f.getNameIdent());
        Files.write(serverpath, clientfile);
    }
    @Override
    public FileClass GetFile(String UIDD) throws IOException {

        if(FileMap.containsKey(UIDD))
        {
            return FileMap.get(UIDD);
        }
         else {
            return null;
        }
    }
}
