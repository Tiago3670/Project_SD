import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Base64;
import java.util.Scanner;

public class Main {
    static Scanner scan = new Scanner(System.in);


    public static void SendFile() throws RemoteException {

       try {
           FileInterface FileInte = (FileInterface) Naming.lookup("rmi://localhost:2022/File");
           File pathfile;
           String identificador,url,name;
           //File pathfile=new File("C:\\Users\\tiago\\OneDrive\\Área de Trabalho\\ficheiro2.txt");
           System.out.println("Url:");
           url=scan.next();
           pathfile =new File(url);
           String FileInBase64=ToBase64(pathfile);
           System.out.println("Name:");
           name=scan.next();
           FileClass f=new FileClass(null,name,FileInBase64);
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
    public static void Menu() throws RemoteException {

        String op;
        System.out.println("1-Enviar ficheiro para o Servidor.");
        System.out.println("2-Receber um ficheiro dado o seu identificador.");
        op=scan.next();
        if(op.equals("1"))
        {
        SendFile();
        }
    }
    public static String ToBase64(File file) throws RemoteException{
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    public static void main(String[] args) throws RemoteException
    {
       // Menu();
        try {
            FileInterface FileInte = (FileInterface) Naming.lookup("rmi://localhost:2022/File");
            String identificador;
            File pathfile=new File("C:\\Users\\tiago\\OneDrive\\Área de Trabalho\\ficheiro2.txt");
            String FileInBase64=ToBase64(pathfile);
            FileClass f=new FileClass(null,"ficheiro2.txt",FileInBase64);
            identificador = FileInte.SendFile(f);
            System.out.println("uploading to server...");
            System.out.println("File UIDD:");
            System.out.println(identificador);
            
            FileClass ff= FileInte.GetFile("e6b79a8b-07a6-3546-9de5-e61ce8d38a08");
            String x= ff.getIdentificadorFile();
            System.out.println(x.toString());


        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}