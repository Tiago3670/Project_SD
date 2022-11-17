import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class FileClass  implements Serializable {
    private UUID Identificador;
    private String FileName;
    private String FileInBase64;

    private String urlDir="C:/Users/tiago/OneDrive/Área de Trabalho/EI/3 Ano/SD/teste";



    public FileClass (UUID Identificador,String FileName,String FileInBase64)
    {
        this.Identificador=Identificador;
        this.FileName=FileName;
        this.FileInBase64=FileInBase64;
    }
    public String getName()
    {
        return this.FileName;
    }
    public String getNameIdent()
    {
        return getIdentificadorFile() + "." + getExtension();
    }
    public String getUrlDir()
    {
        return urlDir;
    }


    public String getIdentificadorFile()
    {
        return this.Identificador.toString();
    }

    public UUID getIdentificadorUUID()
    {
        return this.Identificador;
    }
    public void setIdentificador(UUID identificador)
    {
        this.Identificador=identificador;
    }


    public String FileBase64()
    {
        return  this.FileInBase64;
    }

    public String getExtension() {
        String extension="";
        int index = FileName.lastIndexOf('.');
        if (index > 0) {
            extension = FileName.substring(index + 1);
        }
        return extension;
    }
}
