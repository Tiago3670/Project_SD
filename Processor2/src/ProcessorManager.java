import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.invoke.VolatileCallSite;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface, Serializable {
    RequestClass request;
    private DatagramSocket socket;
    private InetAddress group;
    private byte[]buf;
    FileClass f;
    ProcessorClass p;
    ConcurrentHashMap<String, RequestClass> RequestBackupMap = new ConcurrentHashMap<>();

    volatile double cpu_mean_usage;
    ProcessorInterface ProcessorBackup;
    FileInterface FileInte=(FileInterface) Naming.lookup("rmi://localhost:2022/Storage");
    int enviar=0;
    protected ProcessorManager(ProcessorClass po) throws IOException, NotBoundException {
        p=po;
        MulticastPublisher();
    }
    public ProcessorClass GetProcessor(ProcessorClass p) throws RemoteException {
        return p;
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
    public void EXECBACKUP(UUID identificadorProcessor) throws IOException, InterruptedException {
        if(RequestBackupMap.size()>0)
        {
            for(Map.Entry<String, RequestClass> r : RequestBackupMap.entrySet())
            {
                if(r.getValue().getIdentificadorProcessor().equals(identificadorProcessor))
                {
                    Send(r.getValue());
                }
            }
        }
    }
    public void ADDBackupList(RequestClass r) throws RemoteException
    {
        RequestBackupMap.put(r.getIdentificadorRequest().toString(),r);
    }
    public void RemoveRequest(RequestClass r)
    {
        if(RequestBackupMap.size()>0)
        {
            RequestBackupMap.remove(r.getIdentificadorRequest().toString());
        }
    }

    public void Send(RequestClass r) throws IOException, InterruptedException {
        request=r;
        if(request==null)
            return;

        f=FileInte.GetFile(request.getIdentificadorFile());

        if(f==null)
            return;

        System.out.println(request.getEstado());
        Exec(request.getUrl());
    }

    public synchronized int GetEstado(String IdentificadorRequest) throws RemoteException
    {
        if(IdentificadorRequest.equals(request.getIdentificadorRequest().toString()))
        {
            return request.getEstado();
        }
        else{
            return 1000;
        }
    }


    public synchronized void Exec(String url) throws IOException, InterruptedException {
        Thread threadExec = (new Thread() {
            public void run() {
                while (true) {
                    if (request.getEstado() == 1)
                    {
                        try {
                            f = FileInte.GetFile(request.getIdentificadorFile());
                            byte[] scriptfile = Base64.getDecoder().decode(f.FileBase64().getBytes(StandardCharsets.UTF_8));
                            String scriptdecode = new String(scriptfile, StandardCharsets.UTF_8);
                            byte[] script = Base64.getDecoder().decode(url.getBytes(StandardCharsets.UTF_8));

                            File batfile = new File("temp.bat");
                            String x;
                            File temp2 = new File("temp2.txt");

                            FileOutputStream outtxt = new FileOutputStream(temp2);
                            outtxt.write(scriptdecode.getBytes());
                            outtxt.flush();
                            outtxt.close();

                            FileOutputStream out = new FileOutputStream(batfile);
                            out.write(script);
                            out.flush();
                            out.close();

                            StringBuilder output = new StringBuilder();
                            //String command = "cmd /c " + url + " " + f.getUrlDir() + "\""+ request.getIdentificadorFile() + "\"";
                            // String command = "cmd /c " + batfile.getAbsolutePath() + " \"" + temp2.getAbsolutePath() + "\"";
                            String command = "cmd /c " + "temp.bat "  + temp2 + " ";

                            // System.out.println(scriptdecode.toString());
                            System.out.println(command);
                            Process process = Runtime.getRuntime().exec(command);
                            BufferedReader reader   = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                output.append(line).append(System.getProperty("line.separator"));
                            }
                            process.waitFor();
                            reader.close();
                            System.out.println("->"+output.length());

                            if (output.length() > 0) {
                                request.setEstadoConcluido();
                                if(request.getIdentificadorProcessorBackup()!=p.getLink()) {
                                    if (request.getIdentificadorProcessorBackup() != null) {
                                        System.out.println("processor backup:"+request.getIdentificadorProcessorBackup());
                                        ProcessorBackup = (ProcessorInterface) Naming.lookup(request.getIdentificadorProcessorBackup());
                                        ProcessorBackup.RemoveRequest(request);
                                    }
                                    FileInte.SubmitOutput(f, request.getIdentificadorRequest().toString(), output.toString());
                                }
                            }
                            batfile.delete();
                            temp2.delete();
                        } catch (NotBoundException | InterruptedException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

        });
        threadExec.start();
    }


    public  synchronized void MulticastPublisher() throws IOException {
        Thread threadProcessor = (new Thread() {
            public void run() {
                while(true){
                    String message=p.getPort()+",";
                    try {
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
                    } catch (InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        threadProcessor.start();
    }
}
