import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by p1res on 18/12/2015.
 */
public class Album implements Serializable {
    private String nombreA;
    private int Año;
    ArrayList<Cancion> canciones;
    private String tipo;
    private int temas;

    public Album(String nombreA, int año, String tipo, int temas) {
        this.nombreA = nombreA;
        Año = año;
        this.tipo = tipo;
        this.temas = temas;
    }
}
