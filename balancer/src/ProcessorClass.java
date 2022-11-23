import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class ProcessorClass  implements Serializable
{
    private UUID Identificador;
    private Double cpuusage;

    private int Estado; //1 ativo 0 desativo
    private String Link;
    private int Port;

    private int oldValidated=0;

    public  ProcessorClass(int port)
    {
        this.Identificador=UUID.fromString(UUID.nameUUIDFromBytes(String.valueOf(port).getBytes()).toString());;
        this.Port=port;
        Link = "rmi://localhost:" + port + "/Processor";
    }


    public UUID getIdentificador()
    {
        return this.Identificador;
    }
    public int getPort() {
        return this.Port;
    }

    public String getLink() {
        return this.Link;
    }

    public Double getCpuusage() {
        return this.cpuusage;
    }
    public void setCpuusage(Double cpuusage)
    {
        this.cpuusage=cpuusage;
    }

    public void Ativo()
    {
        this.oldValidated++;
    }

    public int getOldValidated() {
        return this.oldValidated;
    }
}
