import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface, Serializable {

    RequestClass request;

    private DatagramSocket socket;
    private InetAddress group;
    private byte[]buf;
    FileClass f;
    ProcessorClass p;

    private double cpu_mean_usage;

    FileInterface FileInte=(FileInterface) Naming.lookup("rmi://localhost:2022/Storage");
    int enviar=0;
    protected ProcessorManager(ProcessorClass po) throws IOException, NotBoundException {
        p=po;
        MulticastPublisher();
    }


    public ProcessorClass GetProcessor(ProcessorClass p) throws RemoteException {
        return p;
    }
    public void SetProcessor(ProcessorClass p) throws RemoteException {
        p=p;
    }
    public void cpuUsage() throws InterruptedException {

        com.sun.management.OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        int it = 0, it_max = 9;
        while(it <= it_max)
        {
            double x = osBean.getSystemCpuLoad();
            cpu_mean_usage += x;

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(it == it_max)
            {
                cpu_mean_usage /= it_max+1;
                    return;
            }
            else
            {
                it++;
            }

        }

    }

    public void Send(RequestClass r) throws IOException, InterruptedException {
           request=r;
           if(request==null)
               return;

            f=FileInte.GetFile(request.getIdentificadorFile());

           if(f==null)
               return;
            Exec(r.getUrl());
    }




   /* public void Exec(String url) throws IOException, InterruptedException {


        f = FileInte.GetFile(request.getIdentificadorFile());
        byte[] scriptfile = Base64.getDecoder().decode(f.FileBase64().getBytes(StandardCharsets.UTF_8));
        String scriptdecode = new String(scriptfile, StandardCharsets.UTF_8);
        byte[] script = Base64.getDecoder().decode(url.getBytes(StandardCharsets.UTF_8));

        File batfile= new File("temp.bat");
        String x;

        FileOutputStream out=new FileOutputStream(batfile);
        out.write(script);
        out.flush();
        out.close();

        StringBuilder output = new StringBuilder();
        //String command = "cmd /c " + url + " " + f.getUrlDir() + "\""+ request.getIdentificadorFile() + "\"";
        String command = "cmd /c " + batfile + " \""+ scriptdecode +"\"";

        System.out.println(command);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + System.lineSeparator());
                }
                System.out.println(output);
                if(output!=null)
                {
                    request.setEstadoConcluido();
                    FileInte.SubmitOutput(f,request.getIdentificadorRequest().toString(),output.toString());
                }

            } catch (RemoteException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
      batfile.delete();
    }
*/


    public void Exec(String url) throws IOException, InterruptedException {
        Thread threadExec = (new Thread() {
            public void run() {
                while (true) {
                    System.out.println(request.getEstado());
                   // if (request.getEstado() == 1)
                    //{
                        try {
                            f = FileInte.GetFile(request.getIdentificadorFile());
                            byte[] scriptfile = Base64.getDecoder().decode(f.FileBase64().getBytes(StandardCharsets.UTF_8));
                            String scriptdecode = new String(scriptfile, StandardCharsets.UTF_8);
                            byte[] script = Base64.getDecoder().decode(url.getBytes(StandardCharsets.UTF_8));

                            File batfile = new File("temp.bat");
                            String x;

                            FileOutputStream out = new FileOutputStream(batfile);
                            out.write(script);
                            out.flush();
                            out.close();

                            StringBuilder output = new StringBuilder();
                            //String command = "cmd /c " + url + " " + f.getUrlDir() + "\""+ request.getIdentificadorFile() + "\"";
                            String command = "cmd /c " + batfile + " \"" + scriptdecode + "\"";

                            System.out.println(command);
                            Process process = Runtime.getRuntime().exec(command);
                            process.waitFor();
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(process.getInputStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    output.append(line + System.lineSeparator());
                                }
                                System.out.println(output);
                                if (output != null) {
                                    request.setEstadoConcluido();
                                    FileInte.SubmitOutput(f, request.getIdentificadorRequest().toString(), output.toString());
                                }
                            } catch (RemoteException | MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            batfile.delete();
                        } catch (SocketException e) {
                            throw new RuntimeException(e);
                        } catch (UnknownHostException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                  /*  }
                    else
                    {
                        System.out.println("oi");
                    }*/
                }
            }

        });
        threadExec.start();
    }


    public  void MulticastPublisher() throws IOException {
          Thread threadProcessor = (new Thread() {
              public void run() {
                  while(true){
                      //message= port,cpusage,
                  String message=p.getPort()+",";
                  try {
                      //System.out.println("Im in the theard");
                      socket = new DatagramSocket();
                      cpuUsage();
                      group = InetAddress.getByName("230.0.0.0");
                      message=message+cpu_mean_usage;
                      if(enviar==0)
                      {
                          message=message+",setup";
                          enviar++;
                      }
                      else
                      {
                          message=message+",update";
                      }
                      buf = message.getBytes();
                      DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
                      socket.send(packet);
                      socket.close();
                      sleep(3000);
                  } catch (SocketException e) {
                      throw new RuntimeException(e);
                  } catch (UnknownHostException e) {
                      throw new RuntimeException(e);
                  } catch (IOException e) {
                      throw new RuntimeException(e);
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
              }}
          });
          threadProcessor.start();
      }
}
