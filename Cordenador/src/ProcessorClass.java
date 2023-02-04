import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class ProcessorClass implements Serializable
{
    private static final long serialVersionUID = 2509159142972867020L;

     private UUID Identificador;
     private Double cpuusage;
     private String Link;
     private int Port;
     private Instant estado;

     private String processorBackup;

    public ProcessorClass(int port)
    {
        this.Identificador=UUID.fromString(UUID.nameUUIDFromBytes(String.valueOf(port).getBytes()).toString());;
        this.Port=port;
        Link = "rmi://localhost:" + port + "/Processor";
        estado=Instant.now();
    }
    public synchronized void setProcessorBackup(String link){processorBackup=link;}

    public synchronized String getProcessorBackup() {
        return processorBackup;
    }

    public synchronized void  setEstado(Instant actualiza) {
        this.estado =actualiza;
    }
    public synchronized Instant  getEstado(){return  this.estado;}

    public void SetDesativo()
    {
        this.Link="NONE";
    }
    public synchronized UUID getIdentificador()
    {
        return this.Identificador;
    }
    public int getPort() {
        return this.Port;
    }

    public synchronized String getLink() {
        return this.Link;
    }

    public synchronized Double getCpuusage() {
        return this.cpuusage;
    }
    public synchronized void setCpuusage(Double cpuusage)
    {
        this.cpuusage=cpuusage;
    }
}
