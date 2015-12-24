import java.io.Serializable;

/**
 * Created by p1res on 18/12/2015.
 */
public class Cancion implements Serializable, Comparable {
    private String NombreT;
    private String Duracion;
    private Version version;
    private String comentario;

    public Cancion(String nombreT, String duracion, Version version, String comentario) {
        NombreT = nombreT;
        Duracion = duracion;
        this.version = version;
        this.comentario = comentario;
    }

    public Cancion (){ }

    public Cancion(String nombreT, String duracion, String descripcion) {
        this.NombreT=nombreT;
        this.Duracion=duracion;
        this.comentario=descripcion;
    }

    public String getNombreT() {return NombreT;}

    public void setNombreT(String nombreT) {
        NombreT = nombreT;
    }

    public String getDuracion() {
        return Duracion;
    }

    public void setDuracion(String duracion) {
        Duracion = duracion;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getComentario () { return comentario; }

    public int compareTo(Object o) {
        Cancion otroCancion = (Cancion) o;
        return NombreT.compareTo(otroCancion.getNombreT());
    }
}
