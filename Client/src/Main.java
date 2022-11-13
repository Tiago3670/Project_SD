import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;

public class Main {
     static Scanner scan = new Scanner(System.in);
     static FileInterface FileInte;
     static BalancerInterface BalancerInte;
     static ProcessorInterface ProcessorInte;
     static {
        try {
            FileInte = (FileInterface) Naming.lookup("rmi://localhost:2022/Storage");
            BalancerInte = (BalancerInterface) Naming.lookup("rmi://localhost:2023/Balancer");
            ProcessorInte =(ProcessorInterface) Naming.lookup("rmi://localhost:2024/Processor");
        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
     }

    public Main() throws MalformedURLException, NotBoundException, RemoteException {
    }

    public static void SendFile() throws IOException {

        try {
           File pathfile;
           String identificador,name;
           //File pathfile=new File("C:\Users\tiago\OneDrive\Área de Trabalho\EI\3 Ano\SD\Project_SD\100lines.txt");
           System.out.println("Url:");
           String url= scan.nextLine();
           url+=scan.nextLine();
           pathfile =new File(url);
           if(pathfile.isFile()==false)
               return;
           String FileInBase64=ToBase64(pathfile);
           System.out.println("Name:");
           name=scan.next();
           FileClass f=new FileClass(null,name,FileInBase64);
           identificador = FileInte.SendFile(f);
           System.out.println("uploading to Storage...");
           System.out.println("File UIDD:");
           System.out.println(identificador);

       } catch (RemoteException e) {
           System.out.println(e.getMessage());
       } catch (Exception e) {
           e.printStackTrace();
       }
        System.out.println("Press Enter to continue…");
        System.in.read();
   }
   public static  void GetFile() throws IOException
   {
       String identificador = null;
       System.out.println("Identificador:");
       identificador=scan.next();
       FileClass f = FileInte.GetFile(identificador);
       if (f==null)
           return;
       System.out.println("O nome do ficheiro é ("+f.getName()+")");
       System.out.println("Press Enter to continue…");
       System.in.read();
   }
   public static void getOutput() throws IOException
   {
       System.out.println("Qual o request?");
       String IdentificadorRequest=scan.next();
       String result= FileInte.GetOutput(IdentificadorRequest);
       System.out.println(result);
       System.out.println("Press Enter to continue…");
       System.in.read();
   }
   public static void getEstado() throws RemoteException {
       int estado=0;
       estado=ProcessorInte.GetEstado();
       String frase=null;
       if(estado==0)
       {
           frase="Não Enviado";
       }
       else if (estado==2)
       {
           frase="Processo está em concluido";
       }
       System.out.println(frase);
   }
   public static void CreateRequest () throws IOException, NotBoundException, InterruptedException {
       File pathfile;
       System.out.println("Identificador do Ficheiro a enviar no request:");
       String identificadorFile=scan.next();
       System.out.println("url do Script que se vai executar no request:");
       String url= scan.nextLine();
       url+=scan.nextLine();
       pathfile =new File(url);
       if(pathfile.isFile()==false)
           return;

       RequestClass r=new RequestClass(UUID.randomUUID(),url,identificadorFile,1,null);
       UUID ident= BalancerInte.SendRequest(r);
       r.setIdentificadorProcessor(ident);
       System.out.println("Request "+r.getIdentificadorRequest()+" está no processador "+r.getIdentificadorProcessor());
       System.out.println("Press Enter to continue…");
       System.in.read();
   }
    public static void Menu() throws IOException, NotBoundException, InterruptedException {
        String op;
        int x=0;
        do {
        System.out.println("1-Enviar ficheiro para a Storage.");
        System.out.println("2-Receber um ficheiro dado o seu identificador.");
        System.out.println("3-Enviar um request.");
        System.out.println("4-Saber o estado do request.");
        System.out.println("5-Receber o output do ficheiro.");

            System.out.println("0-Para sair.");
        op=scan.next();
        if(op.equals("1"))
        {
          SendFile();
        }
        else if(op.equals("2"))
        {
            GetFile();
        } else if (op.equals("3")) {
            CreateRequest();
        } else if (op.equals("0")) {
            x=1;
        } else if (op.equals("4")) {
            getEstado();
        }else if (op.equals("5")) {
            getOutput();
        }
        }while (x!=1);
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
        try {
            Menu();
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}