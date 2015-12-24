import java.io.Serializable;

public class Nombre implements Serializable {
    private String Nombre;
    private String Id;

    public Nombre(String nombre, String id) {
        Nombre = nombre;
        Id = id;
    }

    public Nombre() {

    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;

    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


}