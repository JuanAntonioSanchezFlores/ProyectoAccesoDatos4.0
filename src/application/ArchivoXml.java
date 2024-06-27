package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ArchivoXml {

	private final String ROOT = "BasesDeDatos";
	private final String BASEDATOS = "baseDatos";
	private final String ATRIBUTO = "nombre";
	private String atributoValor;//Nombre de la base de datos
	private final String TABLA = "tabla";
	private String nombreTabla;//Nombre de la tabla
	
	
	Map<String, List<String>> basesDeDatos = new HashMap<>();
	ArrayList<String> tablas = new ArrayList<String>();
	public ArchivoXml(String atributoValor, String nombreTabla) {
		
		this.atributoValor = atributoValor;
		this.nombreTabla = nombreTabla;
		
	}
	
	public ArchivoXml(Map<String, List<String>> basesDeDatos2) {
		this.basesDeDatos = basesDeDatos2;
	}
	
	public ArchivoXml() {}

	public String getAtributoValor() {
		return atributoValor;
	}

	public void crearXML(Map<String, List<String>> basesDeDatos2) {
		
		try {
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.newDocument();

         // Crear el elemento raíz
         Element basesDatos = document.createElement(ROOT);
         document.appendChild(basesDatos);

         
         // Crear elementos y agregar datos
         for (String clave : basesDeDatos2.keySet()) {
        	 Element bd = document.createElement(BASEDATOS);//Crear elemento baseDatos 
             bd.setAttribute(ATRIBUTO, clave );//Crear atributo nombre y su valor
              basesDatos.appendChild(bd);
             for (String valor : basesDeDatos2.get(clave)) {//Recorremos el valor de "basesDeDatos2" que es una lista, en este caso de posibles tablas
            	 Element tabla = document.createElement(TABLA);
            	 Text texto =  document.createTextNode(valor);
                 bd.appendChild(tabla);
                 tabla.appendChild(texto);
                         
             }
         }
            	  
         //Guardar el documento en un archivo xml     
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         DOMSource source = new DOMSource(document);
         StreamResult result = new StreamResult("infoDb.xml");

         // Escribir el contenido del documento en el archivo XML
         transformer.transform(source, result);

         System.out.println("Archivo XML creado con éxito.");
     } catch (Exception e) {
         e.printStackTrace();
     }
 
	}
	
	public void aniadirTablasXml(Map<String, List<String>> basesDeDatos2) {
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder;
         
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         

	}
	
	
	
	public void verXML (Map<String, List<String>> basesDeDatos2) {
		basesDeDatos2.clear();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			 Document document;
			 document = builder.parse("infoDB.xml");
			 Element raiz = document.getDocumentElement();//Root
			 String nombreDB;
			 NodeList basesDatos = raiz.getChildNodes();//Cada base de datos
			 
			
			 for (int i = 0; i < basesDatos.getLength(); i++) {
	                if (basesDatos.item(i).getNodeType() == Node.ELEMENT_NODE ) {
	                    Node baseDatos = basesDatos.item(i);//Elemento Node
	                    NamedNodeMap atributos = baseDatos.getAttributes();//Nombre del Nodo (nombre base de datos)
	                    nombreDB = atributos.item(0).getNodeValue();
	                    NodeList tablas = baseDatos.getChildNodes();
	                    List<String> datos = new ArrayList<String>();
	                    for(int j = 0; j<tablas.getLength();j++) {
	                    	
		                    if(tablas.item(j).getNodeType() == Node.ELEMENT_NODE) {
			                    Node tabla = tablas.item(j);
			                    String nombreTabla = tabla.getTextContent();
			                    System.out.println(nombreTabla);
			                    datos.add(nombreTabla);
			                    basesDeDatos2.put(nombreDB,datos);
			                    System.out.println("Se ha añadido a "+nombreDB+ " la tabla "+ datos );
			                   
		                    }
	                   
	                    }
	                }
			 }
			 
			 
		} catch (ParserConfigurationException |SAXException |IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    }

}
	
	
	
	

