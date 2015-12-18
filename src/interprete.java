import java.io.Serializable;
import java.util.ArrayList;

public class interprete implements Serializable {
    private Nombre nombre;
    private String Nacionalidad;
    private ArrayList<Album> albumInterprete;

    public interprete(){

    }

    public interprete(Nombre nombre, String nacionalidad, ArrayList<Album> albumInterprete) {
        this.nombre = nombre;
        Nacionalidad = nacionalidad;
        this.albumInterprete = albumInterprete;
    }

}