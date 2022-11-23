import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class ProcessorClass  implements Serializable
{
    private UUID Identificador;
    private int cpuusage;
    private String Link;
    private int Port;

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

    public int getCpuusage() {
        return this.cpuusage;
    }
    public void setCpuusage(int cpuusage)
    {
        this.cpuusage=cpuusage;
    }
}
