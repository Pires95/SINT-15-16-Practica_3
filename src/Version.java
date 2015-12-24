import java.io.Serializable;

/**
 * Created by p1res on 18/12/2015.
 */
public class Version implements Serializable{
    private String Nombre;
    private String IML;

    public Version(String nombre, String iml) {
        Nombre = nombre;
        IML = iml;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getIML() {
        return IML;
    }

    public void setIML(String IML) {
        this.IML = IML;
    }
}
