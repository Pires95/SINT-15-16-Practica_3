import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by p1res on 18/12/2015.
 */
public class Album implements Serializable, Comparable<Album>  {
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

    public Album() {

    }
    public Album(int año) {
        Año = año;
    }
    public int getAño() {
        return Año;
    }

    public String getNombreA() {
        return nombreA;
    }

    public ArrayList<Cancion> getCanciones() {
        return canciones;
    }

    public String getTipo() {
        return tipo;
    }

    public int getTemas() {
        return temas;
    }

    public void setNombreA(String nombreA) {
        this.nombreA = nombreA;
    }

    public void setAño(int año) {
        Año = año;
    }

    public void setCanciones(ArrayList<Cancion> canciones) {
        this.canciones = canciones;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setTemas(int temas) {
        this.temas = temas;
    }

    public int compareTo(Album o) {
        if (Año < o.Año) {
            return -1;
        }
        if (Año > o.Año) {
            return 1;
        }
        return 0;
    }
}
