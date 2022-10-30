import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class FileClass  implements Serializable {
    private UUID Identificador;
    private String FileName;
    private String FileInBase64;

    public FileClass (UUID Identificador,String FileName,String FileInBase64)
    {
        this.Identificador=Identificador;
        this.FileName=FileName;
        this.FileInBase64=FileInBase64;
    }
    public String getName()
    {
        return FileName;
    }

    public UUID getIdentificadorFile()
    {
        return Identificador;
    }
    public void setIdentificador(UUID identificador)
    {
        this.Identificador=identificador;
    }
    public String FileBase64()
    {
        return  FileInBase64;
    }
}
