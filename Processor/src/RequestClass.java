import java.io.Serializable;
import java.rmi.Remote;
import java.util.UUID;

public class RequestClass implements Serializable {
    private UUID IdentificadorRequest;
    private FileClass f;
    private String IdentificadorFile;
    private  UUID IdentificadorProcessor;

    private int Estado; //varia entre 1->em espera 0->concluido

    public  RequestClass (UUID IdentificadorRequest,FileClass f,String IdentificadorFile,int Estado,UUID IdentificadorProcessor)
    {
        this.IdentificadorRequest=IdentificadorRequest;
        this.f=f;
        this.IdentificadorFile=IdentificadorFile;
        this.Estado=1;
        this.IdentificadorProcessor=IdentificadorProcessor;
    }

    FileClass getScript() {return this.f;}
    void setIdentificadorProcessor(UUID Processor)
    {
        this.IdentificadorProcessor=Processor;
    }
    UUID getIdentificadorProcessor()
    {
        return this.IdentificadorProcessor;
    }
    UUID getIdentificadorRequest()
    {
        return this.IdentificadorRequest;
    }
    String getIdentificadorFile()
    {
        return this.IdentificadorFile;
    }
    public  int getEstado()
    {
        return this.Estado;
    }
    public  void setEstadoProcessamento()
    {
        this.Estado=3;
    }
    public  void setEstadoConcluido()
    {
        this.Estado=2;
    }
}
