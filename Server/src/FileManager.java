
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

public class FileManager extends UnicastRemoteObject implements FileInterface  {
    private ArrayList<FileClass> ListFile = new ArrayList<FileClass>();

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



    public void ToFile(FileClass f) throws IOException {
        byte[] clientfile = Base64.getDecoder().decode(f.FileBase64().getBytes(StandardCharsets.UTF_8));
        Path serverpath = Paths.get("C:/Users/tiago/OneDrive/Área de Trabalho/EI/3 Ano/SD/teste", f.getNameIdent());
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
