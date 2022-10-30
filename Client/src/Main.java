import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Base64;

public class Main {
    public static String ToBase64(File file){
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    public static void main(String[] args)
    {
        String identificador;
        File pathfile=new File("C:\\Users\\tiago\\OneDrive\\Área de Trabalho\\teste.txt");
        FileInterface FileInte = null;
        String FileInBase64=ToBase64(pathfile);
        try {
            FileInte = (FileInterface) Naming.lookup("rmi://localhost:2022/File");
            FileClass f=new FileClass(null,"teste.txt",FileInBase64);
            identificador = FileInte.SendFile(f);
            System.out.println("uploading to server...");
            System.out.println("File UIDD:");
            System.out.println(identificador);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}