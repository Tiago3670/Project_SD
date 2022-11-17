import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

public class FileManager extends UnicastRemoteObject implements FileInterface  {
    private ArrayList<FileClass> ListFile = new ArrayList<FileClass>();
    ArrayList<String> DoneRequest=new ArrayList<>();
    private String dir = "C:/Users/tiago/OneDrive/Área de Trabalho/EI/3 Ano/SD/teste";
    //private String dir = "C:/Users/dseabra/IdeaProjects/2022/Project_SD/teste";

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
            this.ListFile.add(f);
            return f.getIdentificadorFile();
    }
    @Override
    public void SubmitOutput(String IDRequest, FileClass f) throws RemoteException
    {
        DoneRequest.add(IDRequest);
    }

   public String GetOutput(String IdentificadorRequest) throws IOException
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
        for(int i=0;i < ListFile.size();i++)
        {
            if(UIDD.equals(this.ListFile.get(i).getIdentificadorFile()))
            {
                return this.ListFile.get(i);
            }
        }
        return null;
    }


}
