
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
import java.util.Base64;
import java.util.UUID;

public class FileManager extends UnicastRemoteObject implements FileInterface  {

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
        return f.getIdentificadorFile();
    }



    public void ToFile(FileClass f) throws IOException {
        byte[] clientfile = Base64.getDecoder().decode(f.FileBase64().getBytes(StandardCharsets.UTF_8));
        Path serverpath = Paths.get("C:/Users/tiago/OneDrive/Área de Trabalho/EI/3 Ano/SD/teste", f.getName());
        Files.write(serverpath, clientfile);
    }


}
