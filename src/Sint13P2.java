import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;


public class Sint13P2 extends HttpServlet {
    static ArrayList<String> listaIML = new ArrayList<String>();
    static ArrayList<Document> listaDocuments = new ArrayList<Document>();
    static ArrayList<String> errores = new ArrayList<String>();
    static HttpSession sesion;

    public void init() {
        listaIML.add("sabina.xml");

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
        request.setCharacterEncoding("UTF-8");
        String jsp = null;
        try{
            if (request.getParameter("fase") != null) {
                String fase = request.getParameter("fase");

                if (fase.equals("0")) {
                    sesion = request.getSession(true);
                    sesion.setMaxInactiveInterval(20);
                    Errores error = new Errores();
                    error.setError(errores);
                    jsp="/pantallaInicio.jsp";
                    request.setAttribute("errorBean", error);
                    delegateControl(request, response, sesion, jsp);
                }
                else{

                    Enumeration numeroDeParametros = request.getParameterNames();
                    while(numeroDeParametros.hasMoreElements()){
                        String valores = (String)numeroDeParametros.nextElement();
                        sesion.setAttribute(valores, request.getParameter(valores));
                    }
                    Resultado resultado= new Resultado();
                    if (fase.equals("1")) {
                        if (((String) sesion.getAttribute("consulta")).equals("lista")){
                            resultado.setResultados(buscarInterprete(listaDocuments));
                            jsp="/primeraPantallaLista.jsp";
                        }
                        if (((String) sesion.getAttribute("consulta")).equals("estilo")){
                            resultado.setResultados(buscarFechas(listaDocuments));
                            jsp="/primeraPantallaEstilos.jsp";
                        }
                    }
                    if (fase.length() > 1) {
                        if (fase.charAt(0) == '1') {//procesamos por artista
                            if (fase.equals("11")){
                                String interpr = (String) sesion.getAttribute("interprete");
                                resultado.setResultados(buscarAlbum(listaDocuments, interpr));
                                jsp="/segundaPantallaLista.jsp";
                            }
                            if (fase.length() > 2) {
                                String interpr = (String) sesion.getAttribute("interprete");
                                String album = (String) sesion.getAttribute("album");
                                resultado.setResultados(buscarCanciones(listaDocuments, album , interpr));
                                jsp="/terceraPantallaLista.jsp";
                            }
                        }
                        if (fase.charAt(0) == '2') { //procesamos por genero
                            if (fase.equals("21")) {
                                String año = (String) sesion.getAttribute("anho");
                                resultado.setResultados(buscarAlbumesDeAño(listaDocuments, año));
                                jsp="/segundaPantallaEstilos.jsp";
                            }
                            if (fase.length() == 3){
                                String año = (String) sesion.getAttribute("anho");
                                String album = (String) sesion.getAttribute("album");
                                resultado.setResultados(buscarEstilos(listaDocuments, año, album));
                                jsp="/terceraPantallaEstilos.jsp";
                            }
                            if (fase.length() == 4) {
                                String año = (String) sesion.getAttribute("anho");
                                String album = (String) sesion.getAttribute("album");
                                String estilo = (String) sesion.getAttribute("estilos");
                                resultado.setNum(buscarCancionesFinales(listaDocuments, año, album, estilo));
                                jsp="/cuartaPantallaEstilos.jsp";
                            }
                        }
                    }
                    request.setAttribute("resultadoBean",resultado);
                    delegateControl(request, response, sesion, jsp);
                }
            } else{
                sesion = request.getSession(true);
                sesion.setMaxInactiveInterval(20);
                Errores error = new Errores();
                error.setError(errores);
                request.setAttribute("errorBean", error);
                jsp="/pantallaInicio.jsp";
                delegateControl(request, response, sesion, jsp);
            }
        }catch (Throwable e){
            sesion = request.getSession(true);
            sesion.setMaxInactiveInterval(20);
            Errores error = new Errores();
            error.setError(errores);
            request.setAttribute("errorBean", error);
            jsp="/pantallaInicioCaducada.jsp";
            delegateControl(request, response, sesion, jsp);
        }
    }

    public void delegateControl(HttpServletRequest request, HttpServletResponse response, HttpSession sesion, String jsp) throws ServletException, IOException {
        ServletContext sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher(jsp);
        rd.forward(request,response);
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
        dbf.setNamespaceAware(true);
        dbf.setValidating(true);
        dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", "iml.xsd");
        DocumentBuilder db;
        Document doc;
        NodeList nodeList = null;
        XML_DTD_ErrorHandler errorHandler= new XML_DTD_ErrorHandler();
        try {
            db = dbf.newDocumentBuilder();
            db.setErrorHandler(errorHandler);
            doc=db.parse(new File(xml));
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema =sf.newSchema(new File("iml.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(doc));
           // if(xml.startsWith("http:")){
             //   doc = db.parse(new URL(xml).openStream(), "http://localhost:8013/sint13/");
            //}else{
              //  doc = db.parse(new URL("http://clave.det.uvigo.es:8080/~sintprof/15-16/p2/"+xml).openStream(), "http://localhost:8013/sint13/");
            //}
            //doc =db.parse(xml);
            listaDocuments.add(doc);
            NodeList nodosIMLenSabina = doc.getElementsByTagName("IML");

            for (int i = 0; i < nodosIMLenSabina.getLength(); i++) {
                String siguienteXML = nodosIMLenSabina.item(i).getTextContent();
                //if(!siguienteXML.startsWith("http:")) siguienteXML = "http://localhost"+siguienteXML;

                if (!siguienteXML.equals("") && (!listaIML.contains(siguienteXML))) listaIML.add(siguienteXML);
            }
        } catch (ParserConfigurationException e) {
            if(XML_DTD_ErrorHandler.hasError() || XML_DTD_ErrorHandler.hasWarning() || XML_DTD_ErrorHandler.hasFatalError()){
                errores.add(XML_DTD_ErrorHandler.getMessage()+"\n Fichero: "+xml+"\n");
                XML_DTD_ErrorHandler.clear();
            }else {
                errores.add("ERROR: " + e.toString()+"\n\n Fichero: "+xml+"\n\n");
            }
        } catch (SAXException e) {
            if(XML_DTD_ErrorHandler.hasError() || XML_DTD_ErrorHandler.hasWarning() || XML_DTD_ErrorHandler.hasFatalError()){
                errores.add(XML_DTD_ErrorHandler.getMessage()+"\n Fichero: "+xml+"\n");
                XML_DTD_ErrorHandler.clear();
            }else {
                errores.add("ERROR: " + e.toString()+"\n Fichero: "+xml+"\n");
            }
        } catch (IOException e) {
            if(XML_DTD_ErrorHandler.hasError() || XML_DTD_ErrorHandler.hasWarning() || XML_DTD_ErrorHandler.hasFatalError()){
                errores.add(XML_DTD_ErrorHandler.getMessage()+"\n Fichero: "+xml+"\n"+"\n");
                XML_DTD_ErrorHandler.clear();
            }else {
                errores.add("ERROR: " + e.toString()+"\n Fichero: "+xml+"\n");
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