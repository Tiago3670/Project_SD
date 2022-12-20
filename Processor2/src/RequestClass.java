import java.io.Serializable;
import java.rmi.Remote;
import java.util.UUID;

public class RequestClass implements Serializable {
    private static final long serialVersionUID = 5902050570918905273L;

    private UUID IdentificadorRequest;
    private String  Script;
    private String IdentificadorFile;
    private  UUID IdentificadorProcessor;
    private  String LinkProcessorBackup;


    private int Estado; //varia entre 1->em espera 0->concluido

    public  RequestClass (UUID IdentificadorRequest,String script,String IdentificadorFile,int Estado,UUID IdentificadorProcessor)
    {
        this.IdentificadorRequest=IdentificadorRequest;
        this.Script=script;
        this.IdentificadorFile=IdentificadorFile;
        this.Estado=1;
        this.IdentificadorProcessor=IdentificadorProcessor;
    }
    void setIdentificadorProcessorBackup(String Processor) {LinkProcessorBackup=Processor;}
    String getIdentificadorProcessorBackup()
    {
        return this.LinkProcessorBackup;
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
    public  void setEstadoProcessamento()
    {
        this.Estado=3;
    }
    public  void setEstadoConcluido()
    {
        this.Estado=2;
    }
    String getUrl(){return this.Script;}
}
