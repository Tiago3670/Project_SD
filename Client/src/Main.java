import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;

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
            int x=10;
            FileInterface FileInte = (FileInterface) Naming.lookup("rmi://localhost:2022/File");
            BalancerInterface BalancerInte = (BalancerInterface) Naming.lookup("rmi://localhost:2023/Balancer");
            String identificador;
            File pathfile=new File("C:\\Users\\tiago\\OneDrive\\Área de Trabalho\\e.txt");
            String FileInBase64=ToBase64(pathfile);
            FileClass f=new FileClass(null,"e.txt",FileInBase64);
            identificador = FileInte.SendFile(f);
            System.out.println("uploading to server...");


             f= FileInte.GetFile(identificador);

             RequestClass r=new RequestClass(UUID.randomUUID(),f,f.getIdentificadorUUID(),1,null);
           // System.out.println(r.getIdentificadorProcessor());

            BalancerInte.SendRequest(r);
            UUID Uuid ;
            Uuid=UUID.fromString("f14bbbdf-8366-4daf-9e5e-1a65fd338bd4");

            x= BalancerInte.GetEStado(Uuid);
            String Estado=null;
            if(x==1)
            {
                Estado="Processo "+Uuid+ " está em em espera";
            }
            else if (x==2)
            {
                Estado="Processo "+Uuid+ " está em procesasmento";
            }
            else if (x==0)
            {
                Estado="Processo "+Uuid+ " está em concluido";
            }
            else if (x==10)
            {
                Estado="Processo "+Uuid+ " não foi encontrado";
            }
            System.out.println(Estado);

        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}