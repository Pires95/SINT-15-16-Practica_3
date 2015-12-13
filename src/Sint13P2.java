import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;


public class Sint13P2 extends HttpServlet {
    static ArrayList<String> listaIML = new ArrayList<String>();
    static ArrayList<Document> listaDocuments = new ArrayList<Document>();
    static ArrayList<String> fases = new ArrayList<String>();
    static ArrayList<String> errores = new ArrayList<String>();
    static ArrayList<String> ficheroErroneo = new ArrayList<String>();

    public void init() {
        listaIML.add("http://clave.det.uvigo.es:8080/~sintprof/15-16/p2/sabina.xml");

        for (int i = 0; i < listaIML.size(); i++) {    //leemos los xml
            String nuevoXML = listaIML.get(i);
            leerXML(nuevoXML);
        }
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (request.getParameter("fase") != null) {
            String fase = request.getParameter("fase");

            if (fase.equals("0")) pantallaSeleccion(out, request, response);

            if (fase.equals("1")) {
                if (request.getParameter("consulta").equals("lista")) primeraPantallaLista(out, request, response);

                if (request.getParameter("consulta").equals("estilo")) primeraPantallaEstilos(out, request, response);
            }
            if (fase.length() > 1) {
                if (fase.charAt(0) == '1') {//procesamos por artista
                    if (fase.equals("11")) segundaPantallaLista(out, request, response);

                    if (fase.length() > 2) terceraPantallaLista(out, request, response);
                }
                if (fase.charAt(0) == '2') { //procesamos por genero
                    if (fase.equals("21")) segundaPantallaEstilos(out, request, response);

                    if (fase.length() == 3) terceraPantallaEstilos(out, request, response);

                    if (fase.length() == 4) cuartaPantallaEstilos(out, request, response);
                }
            }
        } else pantallaSeleccion(out, request, response);
    }

    public void pantallaSeleccion(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        imprimirCabecera(out, request, response);
        out.println("<hr></hr>");
        out.println("Se han producido los siguientes errores: <br>");
        out.println("<div class=errores>");
        for (int i = 0; i <errores.size() ; i++) {
            out.println(errores.get(i)+"<br>");
            out.println(ficheroErroneo.get(i)+"<br>");
        }
        out.println("</div>");
        out.println("<hr></hr>");
        out.println("<form method='POST' name='form' action='?fase1'>");
        out.println("<input type='hidden' name='fase' value='1'> ");
        out.println("<input type='radio' name='consulta' value='lista' checked> Lista <br>");
        out.println("<input type='radio' name='consulta' value='estilo'> Canciones por genero<br>");
        out.println("<br>");
        out.println("<input type='submit' value='Enviar'>");
        imprimirFinal(out, request, response);
    }

    public void primeraPantallaLista(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        imprimirCabecera(out, request, response);
        out.println("Consulta 1");
        out.println("<form method='POST' action='?fase2'>");
        out.println("<input type='hidden' name='fase' value='11' >");
        out.println("<input type='hidden' name='consulta' value='lista'>");
        ArrayList<String> listaInterpretes = buscarInterprete(listaDocuments);
        out.println("<input type='radio' name='interprete' value='" + listaInterpretes.get(0) + "' checked>" + listaInterpretes.get(0) + "<br>");

        for (int i = 1; i < listaInterpretes.size(); i++) out.println("<input type='radio' name='interprete' value='" + listaInterpretes.get(i) + "'>" + listaInterpretes.get(i) + "<br>");

        out.println("<input type='radio' name='interprete' value='todos'> Todos<br><br>");
        out.println("<input type='submit' value='Enviar'>");
        out.println("<input type='submit' value='Atras' onclick='form.fase.value=0'>");
        out.println("<input type='submit' value='Inicio' onclick='form.fase.value=0'>");
        imprimirFinal(out, request, response);
    }

    public void segundaPantallaLista(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        imprimirCabecera(out, request, response);
        String interpr = request.getParameter("interprete");
        out.println("Consulta 1, interprete->" + interpr);
        out.println("<form method='POST' action='?fase3'>");
        out.println("<input type='hidden' name='consulta' value='lista'>");
        out.println("<input type='hidden' name='interprete' value='" + request.getParameter("interprete") + "'>");
        out.println("<input type='hidden' name='fase' value='111' >");
        String interprete = request.getParameter("interprete");
        ArrayList<String> albumesAImprimir = buscarAlbum(listaDocuments, interprete);
        if(albumesAImprimir.size()!=0){
            out.println("<input type='radio' name='album' value='" + albumesAImprimir.get(0) + "' checked>" + albumesAImprimir.get(0) + "<br>");

            for (int i = 1; i < albumesAImprimir.size(); i++) out.println("<input type='radio' name='album' value='" + albumesAImprimir.get(i) + "'>" + albumesAImprimir.get(i) + "<br>");

            out.println("<input type='radio' name='album' value='todos'> Todos<br><br>");
            out.println("<input type='submit' value='Enviar'>");
        }
        char faseAnterior = request.getParameter("fase").charAt(0);
        out.println("<input type='submit' value='Atras' onclick='form.fase.value=" + faseAnterior + "'>");
        out.println("<input type='submit' value='Inicio' onclick='form.fase.value=0'>");
        imprimirFinal(out, request, response);
    }

    public void terceraPantallaLista(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        imprimirCabecera(out, request, response);
        out.println("Consulta 1, interprete-> " + request.getParameter("interprete") + ", Álbum->" + request.getParameter("album"));
        out.println("<form method='POST' action='?fase4'>");
        out.println("<input type='hidden' name='consulta' value='lista'>");
        out.println("<input type='hidden' name='interprete' value='" + request.getParameter("interprete") + "'>");
        out.println("<input type='hidden' name='album' value='" + request.getParameter("album") + "'>");
        out.println("<input type='hidden' name='fase' value='" + request.getParameter("fase") + "1'>");
        String album = request.getParameter("album");
        String interprete = request.getParameter("interprete");
        ArrayList<String> albumSeleccionado = buscarCanciones(listaDocuments, album, interprete);

        if(albumSeleccionado.size()!=0) for (int i = 0; i < albumSeleccionado.size(); i++) out.println(albumSeleccionado.get(i) + "<br>");

        String fase = request.getParameter("fase");
        String faseAnterior = fase.substring(0, 2);
        out.println("<br><input type='submit' value='Atras' onclick='form.fase.value=" + faseAnterior + "'>");
        out.println("<input type='submit' value='Inicio' onclick='form.fase.value=0'>");
        imprimirFinal(out, request, response);
    }

    public void primeraPantallaEstilos(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        imprimirCabecera(out, request, response);
        out.println("Consulta 2");
        out.println("<form method='POST' action='?fase2'>");
        out.println("<input type='hidden' name='consulta' value='estilo'>");
        out.println("<input type='hidden' name='fase' value='21' >");
        ArrayList<String> años = buscarFechas(listaDocuments);

        if(años.size()!=0){
            out.println("<input type='radio' name='anho' value='" + años.get(0) + "' checked>" + años.get(0) + "<br>");

            for (int i = 1; i < años.size(); i++) out.println("<input type='radio' name='anho' value='" + años.get(i) + "'>" + años.get(i) + "<br>");

            out.println("<input type='radio' name='anho' value='todos'> Todos <br><br>");
            out.println("<input type='submit' value='Enviar'>");
        }
        out.println("<input type='submit' value='Atras' onclick='form.fase.value=0'>");
        out.println("<input type='submit' value='Inicio' onclick='form.fase.value=0'>");
        imprimirFinal(out, request, response);
    }

    public void segundaPantallaEstilos(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        imprimirCabecera(out, request, response);

        out.println("Consulta 2, Año-> " + request.getParameter("anho"));
        out.println("<form method='POST' action='?fase3'>");
        out.println("<input type='hidden' name='consulta' value='estilo'>");
        out.println("<input type='hidden' name='anho' value='" + request.getParameter("anho") + "'>");
        out.println("<input type='hidden' name='fase' value='211' >");
        String año = request.getParameter("anho");
        ArrayList<String> años = buscarAlbumesDeAño(listaDocuments, año);

        if(años.size()!=0){
            out.println("<input type='radio' name='album' value='" + años.get(0) + "' checked> " + años.get(0) + "<br>");

            for (int i = 1; i < años.size(); i++) out.println("<input type='radio' name='album' value='" + años.get(i) + "'> " + años.get(i) + "<br>");

            out.println("<input type='radio' name='album' value='todos'> Todos <br><br>");
            out.println("<input type='submit' value='Enviar'>");
        }
        String faseAnterior = "1";
        out.println("<input type='submit' value='Atras' onclick='form.fase.value=" + faseAnterior + "'>");
        out.println("<input type='submit' value='Inicio' onclick='form.fase.value=0'>");
        imprimirFinal(out, request, response);
    }

    public void terceraPantallaEstilos(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        imprimirCabecera(out, request, response);

        out.println("Consulta 2, Año-> " + request.getParameter("anho") + ", Álbum->" + request.getParameter("album"));
        out.println("<form method='POST' action='?fase4'>");
        out.println("<input type='hidden' name='consulta' value='estilo'>");
        out.println("<input type='hidden' name='anho' value='" + request.getParameter("anho") + "'>");
        out.println("<input type='hidden' name ='album' value='" + request.getParameter("album") + "'>");
        out.println("<input type='hidden' name='fase' value='" + request.getParameter("fase") + "1' >");
        String año = request.getParameter("anho");
        String album = request.getParameter("album");
        ArrayList<String> estilos = buscarEstilos(listaDocuments, año, album);

        if(estilos.size()!=0){
            out.println("<input type='radio' name='estilos' value='" + estilos.get(0) + "' checked> " + estilos.get(0) + "<br>");

            for (int i = 1; i < estilos.size(); i++) out.println("<input type='radio' name='estilos' value='" + estilos.get(i) + "'> " + estilos.get(i) + "<br>");

            out.println("<input type='radio' name='estilos' value='todos'> Todos <br><br>");
            out.println("<input type='submit' value='Enviar'>");
        }
        String fase = request.getParameter("fase");
        String faseAnterior = fase.substring(0, 2);
        out.println("<input type='submit' value='Atras' onclick='form.fase.value=" + faseAnterior + "'>");
        out.println("<input type='submit' value='Inicio' onclick='form.fase.value=0'>");
        imprimirFinal(out, request, response);
    }

    public void cuartaPantallaEstilos(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        imprimirCabecera(out, request, response);

        out.println("Consulta 2, Año-> " + request.getParameter("anho") + ", Álbum->" + request.getParameter("album") + ", Estilo->" + request.getParameter("estilos"));
        out.println("<form method='POST' action='?fase5'>");
        out.println("<input type='hidden' name='consulta' value='estilo'>");
        out.println("<input type='hidden' name='anho' value='" + request.getParameter("anho") + "'>");
        out.println("<input type='hidden' name ='album' value='" + request.getParameter("album") + "'>");
        out.println("<input type='hidden' name ='estilo' value='" + request.getParameter("estilos") + "'>");
        out.println("<input type='hidden' name='fase' value='" + request.getParameter("fase") + "1' >");
        String año = request.getParameter("anho");
        String album = request.getParameter("album");
        String estilo = request.getParameter("estilos");
        int num = buscarCancionesFinales(listaDocuments, año, album, estilo);
        out.println("El número de canciones de este estilo es:" + num + "<br><br>");
        String fase = request.getParameter("fase");
        String faseAnterior = fase.substring(0, 3);
        out.println("<br><input type='submit' value='Atras' onclick='form.fase.value=" + faseAnterior + "'>");
        out.println("<input type='submit' value='Inicio' onclick='form.fase.value=0'>");
        imprimirFinal(out, request, response);
    }

    public void imprimirCabecera(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        out.println("<html>");
        out.println("<head>");
        out.println("<link rel='stylesheet' href='iml.css'>");
        out.println("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet' type='text/css'>");
        out.println("<title>Servicio de consulta de información musical</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div>");
        out.println("<h1>Bienvenido al servicio de consulta de información musical (Marcos Pires)</h1>");
        out.println("<h2>Seleccione una de las siguientes opciones</h2>");
        out.println("</div>");
        request.setCharacterEncoding("UTF-8");
    }

    public void imprimirFinal(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        out.println("</form>");
        out.println("<footer><hr></hr>");
        out.println("Creada por Marcos Pires Filgueira</footer>");
        out.println("</body>");
        out.println("</html>");
    }


    public static ArrayList<String> buscarInterprete(ArrayList<Document> listaXML) {
        ArrayList<String> interpretes = new ArrayList<String>();

        for (int i = 0; i < listaXML.size(); i++) {
            Document xml = listaXML.get(i);
            NodeList interprete=null;
            XPath xpath = XPathFactory.newInstance().newXPath();

            NodeList elementosEncontrados = xml.getElementsByTagName("Interprete");
            try{
                interprete = (NodeList) xpath.evaluate("/Interprete/Nombre/NombreG | /Interprete/Nombre/NombreC", xml, XPathConstants.NODESET);
                for (int j = 0; j <  interprete.getLength(); j++) interpretes.add(interprete.item(j).getTextContent());

            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(interpretes);
        return interpretes;
    }

    public static ArrayList<String> buscarAlbum(ArrayList<Document> listaXML, String interprete) {
        ArrayList<String> listaAlbums = new ArrayList<String>();

        for (int i = 0; i < listaXML.size(); i++) {
            Document xml = listaXML.get(i);
            XPath xpath = XPathFactory.newInstance().newXPath();

            try {
                NodeList nombresA=null;
                NodeList fechas=null;
                ArrayList<String> ordenarAño = new ArrayList<String>();
                if (interprete.equals("todos")) {
                    nombresA = (NodeList) xpath.evaluate("//Album/NombreA", xml, XPathConstants.NODESET);
                    fechas = (NodeList) xpath.evaluate("//Album/Año", xml, XPathConstants.NODESET);
                }
                else{
                    nombresA = (NodeList) xpath.evaluate("//Nombre[NombreG='"+interprete+"' or NombreC='"+interprete+"']/../Album/NombreA", xml, XPathConstants.NODESET);
                    fechas = (NodeList) xpath.evaluate("//Nombre[NombreG='"+interprete+"' or NombreC='"+interprete+"']/../Album/Año", xml, XPathConstants.NODESET);
                }

                for (int j = 0; j < fechas.getLength(); j++) {
                    String nombreAlbums= nombresA.item(j).getTextContent();
                    String añoPublicacion = fechas.item(j).getTextContent();
                    ordenarAño.add(añoPublicacion+"-@!"+nombreAlbums);
                }

                Collections.sort(ordenarAño);

                for (int j=0;j<ordenarAño.size(); j++){
                    String[] separado = (ordenarAño.get(j)).split("-@!");
                    listaAlbums.add(separado[1]);
                }
            }catch (XPathExpressionException e){
                e.printStackTrace();
            }
        }
        return listaAlbums;
    }

    public static ArrayList<String> buscarCanciones(ArrayList<Document> listaXML, String album, String interprete) {
        ArrayList<String> listaCancion = new ArrayList<String>();

        for (int i = 0; i < listaXML.size(); i++) {
            Document xml = listaXML.get(i);
            XPath xpath = XPathFactory.newInstance().newXPath();

            Element element = xml.getDocumentElement();
            NodeList listaCanciones = null;
            try {
                if (interprete.equalsIgnoreCase("todos")) {
                    if (album.equalsIgnoreCase("todos")) listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album/Cancion", xml, XPathConstants.NODESET);
                    else listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='" + album + "']/Cancion", xml, XPathConstants.NODESET);
                } else {
                    if (album.equalsIgnoreCase("todos")) listaCanciones = (NodeList) xpath.evaluate("//Nombre[NombreG='"+interprete+ "' or NombreC='"+interprete+"']/../Album/Cancion", xml, XPathConstants.NODESET);
                    else listaCanciones = (NodeList) xpath.evaluate("//Nombre[NombreG='"+interprete+ "' or NombreC='"+interprete+"']/../Album[NombreA='" + album + "']/Cancion", xml, XPathConstants.NODESET);

                }
                String todoJunto = null;
                String nombreC = "";
                String duracion = "";
                String descripcion = "";
                if (listaCanciones != null) {
                    for (int j = 0; j < listaCanciones.getLength(); j++) {
                        NodeList listaNodosDeCancion = listaCanciones.item(j).getChildNodes();

                        for (int k = 0; k < listaNodosDeCancion.getLength(); k++) {
                            String nodoCancion = listaNodosDeCancion.item(k).getNodeName();
                            if (nodoCancion.equals("#text")) {
                                String filtrarAux = listaNodosDeCancion.item(k).getTextContent();
                                filtrarAux = filtrarAux.replaceAll("\n", "").trim();
                                if (!filtrarAux.equals("")) descripcion += filtrarAux;
                            } else if (nodoCancion.equals("NombreT")) nombreC = listaNodosDeCancion.item(k).getTextContent();
                            else if (nodoCancion.equals("Duracion")) duracion = listaNodosDeCancion.item(k).getTextContent();

                        }
                        todoJunto = nombreC + "   --->" + duracion + " (" + descripcion+" )";
                        listaCancion.add(todoJunto);
                        descripcion = "";
                    }
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(listaCancion);
        return listaCancion;
    }

    public static ArrayList<String> buscarFechas(ArrayList<Document> listaXML) {
        ArrayList<String> listaFechas = new ArrayList<String>();

        for (int i = 0; i < listaXML.size(); i++) {
            Document xml = listaXML.get(i);
            XPath xpath = XPathFactory.newInstance().newXPath();
            try {
                NodeList listaALbumnesPorFecha = (NodeList) xpath.evaluate("/Interprete/Album/Año", xml, XPathConstants.NODESET);
                for (int j = 0; j < listaALbumnesPorFecha.getLength(); j++) {
                    String nombre = ((Element) listaALbumnesPorFecha.item(j)).getTextContent();
                    if(!listaFechas.contains(nombre)) listaFechas.add(nombre);
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(listaFechas);
        return listaFechas;
    }

    public static ArrayList<String> buscarAlbumesDeAño(ArrayList<Document> listaXML, String años) {
        ArrayList<String> listaAlbum = new ArrayList<String>();

        for (int i = 0; i < listaXML.size(); i++) {
            Document xml = listaXML.get(i);
            XPath xpath = XPathFactory.newInstance().newXPath();
            try {
                NodeList listaALbumnesPorFecha = null;
                if (años.equals("todos")) listaALbumnesPorFecha = (NodeList) xpath.evaluate("/Interprete/Album/NombreA", xml, XPathConstants.NODESET);
                else listaALbumnesPorFecha = (NodeList) xpath.evaluate("/Interprete/Album[Año='" + años + "']/NombreA", xml, XPathConstants.NODESET);

                for (int j = 0; j < listaALbumnesPorFecha.getLength(); j++) {
                    String album = listaALbumnesPorFecha.item(j).getTextContent();
                    listaAlbum.add(album);
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(listaAlbum);
        return listaAlbum;
    }

    public static ArrayList<String> buscarEstilos(ArrayList<Document> listaXML, String año, String album) {
        ArrayList<String> listaEstilos = new ArrayList<String>();

        for (int i = 0; i < listaXML.size(); i++) {
            Document xml = listaXML.get(i);
            XPath xpath = XPathFactory.newInstance().newXPath();
            try {
                NodeList listaCancionesPorEstilo = null;
                if (año.equals("todos")) {
                    if (album.equals("todos")) listaCancionesPorEstilo = (NodeList) xpath.evaluate("/Interprete/Album/Cancion/@estilo", xml, XPathConstants.NODESET);
                    else listaCancionesPorEstilo = (NodeList) xpath.evaluate("/Interprete/Album[NombreA ='" + album + "']/Cancion/@estilo", xml, XPathConstants.NODESET);
                } else if (album.equals("todos")) listaCancionesPorEstilo = (NodeList) xpath.evaluate("/Interprete/Album[Año ='" + año + "']/Cancion/@estilo", xml, XPathConstants.NODESET);

                else listaCancionesPorEstilo = (NodeList) xpath.evaluate("/Interprete/Album[Año='" + año + "' and NombreA='" + album + "']/Cancion/@estilo", xml, XPathConstants.NODESET);

                for (int j = 0; j < listaCancionesPorEstilo.getLength(); j++) {
                    String estilo = listaCancionesPorEstilo.item(j).getTextContent();
                    if (!listaEstilos.contains(estilo)) listaEstilos.add(estilo);
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(listaEstilos);
        return listaEstilos;
    }

    public static int buscarCancionesFinales(ArrayList<Document> listaXML, String año, String album, String estilo) {
        NodeList listaCanciones = null;
        int numCanciones = 0;
        for (int i = 0; i < listaXML.size(); i++) {
            Document xml = listaXML.get(i);
            XPath xpath = XPathFactory.newInstance().newXPath();

            try {
                if (año.equals("todos")) {
                    if (album.equals("todos")) {
                        if (estilo.equals("todos")) listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album/Cancion", xml, XPathConstants.NODESET);
                        else listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album/Cancion[@estilo='" + estilo + "']", xml, XPathConstants.NODESET);
                    } else {
                        if (estilo.equals("todos")) listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='"+album+"']/Cancion", xml, XPathConstants.NODESET);
                        else listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='"+album+"']/Cancion[@estilo='" + estilo + "']", xml, XPathConstants.NODESET);
                    }
                } else if (album.equals("todos")) {
                    if (estilo.equals("todos")) listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[Año='"+año+"']/Cancion", xml, XPathConstants.NODESET);
                    else listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[Año='"+año+ "']/Cancion[@estilo='" + estilo + "']", xml, XPathConstants.NODESET);
                } else if (estilo.equals("todos")) listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[Año='" + año + "' and NombreA='" + album + "']/Cancion", xml, XPathConstants.NODESET);

                else listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[Año='" + año + "' and NombreA='" + album + "']/Cancion[@estilo='" + estilo + "']", xml, XPathConstants.NODESET);

                numCanciones += listaCanciones.getLength();
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        return numCanciones;
    }

    public static void leerXML(String xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        DocumentBuilder db = null;
        NodeList nodeList = null;
        try {
            db = dbf.newDocumentBuilder();
            db.setErrorHandler(new XML_DTD_ErrorHandler());
            Document doc = null;
            if(xml.startsWith("http:")){
                doc = db.parse(new URL(xml).openStream(), "http://localhost:8013/sint13/");
            }else{
                doc = db.parse(new URL("http://clave.det.uvigo.es:8080/~sintprof/15-16/p2/"+xml).openStream(), "http://localhost:8013/sint13/");
            }
            listaDocuments.add(doc);

            NodeList nodosIMLenSabina = doc.getElementsByTagName("IML");

            for (int i = 0; i < nodosIMLenSabina.getLength(); i++) {
                String siguienteXML = nodosIMLenSabina.item(i).getTextContent();
                if(!siguienteXML.startsWith("http:")) siguienteXML = "http://178.62.190.10/"+siguienteXML;

                if (!siguienteXML.equals("") && (!listaIML.contains(siguienteXML))) listaIML.add(siguienteXML);
            }
        } catch (ParserConfigurationException e) {
            if(XML_DTD_ErrorHandler.hasError() || XML_DTD_ErrorHandler.hasWarning() || XML_DTD_ErrorHandler.hasFatalError()){
                errores.add(XML_DTD_ErrorHandler.getMessage());
                ficheroErroneo.add("Fichero: "+xml);
                XML_DTD_ErrorHandler.clear();
            }else{
                errores.add("ERROR: "+e.toString());
                ficheroErroneo.add("Fichero: "+xml);
            }
        } catch (SAXException e) {
            if(XML_DTD_ErrorHandler.hasError() || XML_DTD_ErrorHandler.hasWarning() || XML_DTD_ErrorHandler.hasFatalError()){
                errores.add(XML_DTD_ErrorHandler.getMessage());
                ficheroErroneo.add("Fichero: "+xml);
                XML_DTD_ErrorHandler.clear();
            } else{
                errores.add("ERROR: "+e.toString());
                ficheroErroneo.add("Fichero: "+xml);
            }
        } catch (IOException e) {
            if(XML_DTD_ErrorHandler.hasError() || XML_DTD_ErrorHandler.hasWarning() || XML_DTD_ErrorHandler.hasFatalError()){
                errores.add(XML_DTD_ErrorHandler.getMessage());
                ficheroErroneo.add("Fichero: "+xml);
                XML_DTD_ErrorHandler.clear();
            } else{
                errores.add("ERROR: "+e.toString());
                ficheroErroneo.add("Fichero: "+xml);
            }

        }
    }
}

class XML_DTD_ErrorHandler extends DefaultHandler {
    public static boolean error;
    public static boolean warning;
    public static boolean fatalerror;
    public static String message;

    public XML_DTD_ErrorHandler() {
        error = false;
        warning = false;
        fatalerror = false;
        message = null;
    }
    public void warning(SAXParseException spe) throws SAXException {
        warning = true;
        message = "Warning: " + spe.toString();
        throw new SAXException();
    }
    public void error(SAXParseException spe) throws SAXException {
        error = true;
        message = "Error: " + spe.toString();
        throw new SAXException();
    }
    public void fatalerror(SAXParseException spe) throws SAXException {
        fatalerror = true;
        message = "Fatal Error: " + spe.toString();
        throw new SAXException();
    }
    public static boolean hasWarning() {
        return warning;
    }
    public static boolean hasError() {
        return error;
    }
    public static boolean hasFatalError() {
        return fatalerror;
    }
    public static String getMessage() {
        return message;
    }
    public static void clear() {
        warning = false;
        error = false;
        fatalerror = false;
        message = null;
    }
}