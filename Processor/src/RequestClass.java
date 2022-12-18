import java.io.Serializable;
import java.util.UUID;

public class RequestClass implements Serializable {
    private UUID IdentificadorRequest;
    private String  Script;
    private String IdentificadorFile;
    private  UUID IdentificadorProcessor;

    private int Estado; // 0->concluido

    public  RequestClass (UUID IdentificadorRequest,String script,String IdentificadorFile,int Estado,UUID IdentificadorProcessor)
    {
        this.IdentificadorRequest=IdentificadorRequest;
        this.Script=script;
        this.IdentificadorFile=IdentificadorFile;
        this.Estado=1;
        this.IdentificadorProcessor=IdentificadorProcessor;
    }

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
    public  void setEstadoConcluido()
    {
        this.Estado=0;
    }
    String getUrl(){return this.Script;}
}
