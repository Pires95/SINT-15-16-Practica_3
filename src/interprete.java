import java.io.Serializable;
import java.util.ArrayList;

public class interprete implements Serializable, Comparable {
    private Nombre nombre;
    private String Nacionalidad;
    private ArrayList<Album> albumInterprete;

    public interprete() {

    }

    public interprete(Nombre nombre, String nacionalidad, ArrayList<Album> albumInterprete) {
        this.nombre = nombre;
        this.Nacionalidad = nacionalidad;
        this.albumInterprete = albumInterprete;
    }



    public Nombre getNombre() {
        return nombre;
    }

    public void setNombre(Nombre nombre) {
        this.nombre = nombre;
    }

    public String getNacionalidad() {
        return Nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        Nacionalidad = nacionalidad;
    }

    public ArrayList<Album> getAlbumInterprete() {
        return albumInterprete;
    }

    public void setAlbumInterprete(ArrayList<Album> albumInterprete) {
        this.albumInterprete = albumInterprete;
    }


    public void setNombre(String nombre) {
        Nombre nombreTemporal = new Nombre();
        nombreTemporal.setNombre(nombre);
        this.nombre=nombreTemporal;
    }

    public int compareTo(Object o) {
        interprete otroInterprete = (interprete) o;
        return nombre.getNombre().compareTo(nombre.getNombre());
    }
}