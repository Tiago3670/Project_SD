import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class ProcessorClass  implements Serializable
{
    private UUID Identificador;
    private int Estado;//varia entre disponivel->1  e ocupado->0
    private int Port;

    public  ProcessorClass(UUID Identificador,int Estado,int port)
    {
        this.Identificador=Identificador;
        this.Estado=Estado;
        this.Port=port;
    }

    public UUID getIdentificador()
    {
        return this.Identificador;
    }
    public int getPort() {
        return Port;
    }
    public int getEstado() {
        return Estado;
    }
    public void setEstadoToOcupado() {
        this.Estado = 0;
        System.out.println("ALTEREI O ESTADO do "+this.getIdentificador()+ " para "+this.Estado);
    }
    public void setEstadoToDisponivel() {
        this.Estado = 1;
        System.out.println("ALTEREI O ESTADO do "+this.getIdentificador()+ " para "+this.Estado);

    }
}
