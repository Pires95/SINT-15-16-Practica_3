import java.util.ArrayList;

public class Resultado {
    private ArrayList<String> resultados;
    private int num;

    public Resultado() {
        resultados=null;
        num=0;
    }
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public ArrayList<String> getResultados() {
        return resultados;
    }

    public void setResultados(ArrayList<String> resultados) {
        this.resultados = resultados;
    }


}
