import java.io.Serializable;

public class Nombre implements Serializable {
    private String Nombre;
    private String Id;


    public Nombre(String nombre, String id) {
        Nombre = nombre;
        Id = id;
    }
}