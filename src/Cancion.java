import java.io.Serializable;

/**
 * Created by p1res on 18/12/2015.
 */
public class Cancion implements Serializable {
    private String NombreT;
    private String Duracion;
    private Version version;

    public Cancion(String nombreT, String duracion, Version version) {
        NombreT = nombreT;
        Duracion = duracion;
        this.version = version;
    }
}
