package application;

import java.io.File; 
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class VistaController implements Initializable {

	
	    @FXML
	    private Button btnConvertir, btnCrearDB, btnEliminar, btnCrearTabla, btnEliminarTabla, btnVerTablas, 
	    				btnInsertarRegistro, btnModificarRegistro, btnEliminarRegistro;
	    @FXML
	    private MenuItem btnVerProyecto;
	    @FXML
	    private Pane paneNombreXML, paneBaseDeDatos, paneTablas, paneCombo;
	    @FXML
	    private TextField txfNombreXML, txfNombreDB, txfNombreTabla, txfId, txfNombre;
	    @FXML
	    private Label lblPrueba, lblCerrar, lblTitulo, lblInfoDB, lblNombreTabla, lblModificar, lblVerXml;
	    @FXML
	    private VBox vbArchivos, vbInfo;
	    @FXML
	    private ComboBox cbDB,cbTablas;
	    @FXML
	    private SplitPane paneDB;
	    @FXML
	    private HBox hbBD, hbTablas;
	    @FXML
	    private TableView<Persona> tbTabla;
	    @FXML
	    private TableColumn<Persona, Integer> tcId;
	    @FXML
	    private TableColumn<Persona, String> tcNombre;
	   
	    String nombreDB = "";
	    String usuario = "";
	    String contrasenia = "";
	    private ArchivoXml archivoXml = new ArchivoXml();
	    private static Connection conexion;
	    private String nombreTabla;
		private int id;
		private Integer idRegistro;
		private String nombre;
		
	   
	    ObservableList<Persona> persona = FXCollections.observableArrayList();
    	ArrayList<String> listaArchivos = new ArrayList<String>();
    	Map<String, List<String>> basesDeDatos = new HashMap<>();
    	
        @FXML
        public void verXML() throws IOException {
        	ProcessBuilder pb = new ProcessBuilder("notepad","infoDb.xml");
        	pb.start();
        }
     	
	    @FXML
	   public void convertirXML(ActionEvent event) {
	    	if(this.txfNombreXML.getText().isEmpty()||this.txfNombreXML.getText().isBlank()) {
	    		Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				alert.setContentText("Error: El campo nombre de archivo está vacio");
				alert.showAndWait();
	    	}else {
		    	File archivo=new File(this.txfNombreXML.getText());
				Document doc = null; // doc es de tipo Document y representa al árbol DOM
			    
				 // Se crea un objeto DocumentBuilderFactory
		        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
		        fabrica.setIgnoringComments(true);
		        fabrica.setIgnoringElementContentWhitespace(true);
		        DocumentBuilder constructor;
				try {
					constructor = fabrica.newDocumentBuilder();
					doc = constructor.parse(archivo);
					datosClientes(doc);
					this.txfNombreXML.clear();
				
				} catch (ParserConfigurationException e) {
					// TODO Bloque catch generado automáticamente
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setContentText("Error: Se ha producido un error en la carga del archivo");
					alert.showAndWait();
					e.printStackTrace();
				} catch (SAXException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setContentText("Error: Se ha producido un error en la carga del archivo");
					alert.showAndWait();
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Bloque catch generado automáticamente
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setContentText("Error: El sistema no puede encontrar el archivo especificado");
					alert.showAndWait();
						
					e.printStackTrace();
				}
		    	}
		}
	    

	    @FXML
	    void verProyecto() {
	    	this.vbArchivos.setVisible(false);
	    	paneNombreXML.setVisible(true);
	    }
	    
	    @FXML
	    void cerrarVistaArchivos() {
	    	this.vbArchivos.setVisible(false);
	    	//ObservableList<Node> hijos;
	    	List<javafx.scene.Node> hijos = this.vbArchivos.getChildren();
	    	for(int i = 0; i<hijos.size(); i++) {
	    		if(hijos.get(i).getId() != "lblTitulo" && hijos.get(i).getId() != "lblCerrar") {
	    			hijos.remove(i);
	    		}
	    	}
	    
	    }
	    
	    void ocultarPaneXML(boolean visible) {
	    	if(visible)
	    		paneNombreXML.setVisible(true);
	    	else
	    		paneNombreXML.setVisible(false);
	    }
	
	    public void datosClientes(Document doc) throws DOMException {
	    	
	    	String nombreArchivo = "";
	     	String nombre = "";
	     	String direccion = "";
	     	String email = "";
	    	
	     	Node raiz=doc.getFirstChild();// <Ficheros>
			NodeList listaFichero = raiz.getChildNodes();//Lista de hijos "fichero"
			
			
			for(int i = 0; i<listaFichero.getLength();i++) {
				
				if(listaFichero.item(i).getNodeType()==Node.ELEMENT_NODE) {
					NodeList registros = listaFichero.item(i).getChildNodes();// Obtener hijos "registro"
					for(int j = 0;j<registros.getLength();j++) {
						if(registros.item(j).getNodeType()==Node.ELEMENT_NODE) {
							NamedNodeMap atributos = registros.item(j).getAttributes();//Atributos de "registro"
							for(int a = 0;a<atributos.getLength();a++) {//Recorre los posibles atributos como conozco el xml se que solo hay 1
								Node atributo = atributos.item(a);
								nombreArchivo = atributo.getNodeValue()+".txt";//Obtengo el valor del atributo que será el nombre del archivo
							}
							NodeList campos = registros.item(j).getChildNodes();
							for(int k = 0; k<campos.getLength();k++) {
								
								switch(campos.item(k).getNodeName()) {
								case "nombre":
									nombre = campos.item(k).getTextContent();
									break;
								case "direccion":
									direccion = campos.item(k).getTextContent();
									break;
								case "email":
									email = campos.item(k).getTextContent();
									break;
								}
								
							}
						}
					}
					
					String textoArchivo = nombre+","+direccion+","+email;
					
					try {
						Archivo nuevo = new Archivo(nombreArchivo);
						nuevo.crearArchivo(nombreArchivo, textoArchivo);//Crea el archivo si no existe e introduce los datos que queremos
						listaArchivos.add(nombreArchivo);
						
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
					}
					}
				 
				}
			 	Alert alert = new Alert(Alert.AlertType.INFORMATION);
			    alert.setTitle("Archivos creados");
			    alert.setContentText("Han sido creados los siguientes archivos: \n"+listaArchivos.toString());
			    alert.showAndWait();
			    enlacesArchivos();
			    listaArchivos.clear();
			    
	    }

		
		
		 void enlacesArchivos() {
			
			for(int i = 0; i<listaArchivos.size();i++) {
				String nombreArchivo = listaArchivos.get(i).toString();
		    	Hyperlink hyperlink = new Hyperlink(nombreArchivo);
		    	hyperlink.setOnAction(e -> {
		          ProcessBuilder pb = new ProcessBuilder("notepad", nombreArchivo);
		          try {
					pb.start();
				} catch (IOException e1) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setContentText("Error: No se pudo abrir el archivo solicitado");
					alert.showAndWait();
					e1.printStackTrace();
				}
		        });
		    	
		    	vbArchivos.getChildren().add(hyperlink);
			}
			ocultarPaneXML(false);
			vbArchivos.setVisible(true);
			
		 }

		 /******* BASE DE DATOS ***********************************************/

		 @FXML
		 public void crearDB() {
			 String nombre = this.txfNombreDB.getText();
			 if(nombre.isBlank()||nombre.isEmpty()) {
				 Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setContentText("Error: El nombre de la base de datos está vacio");
					alert.showAndWait();
			 }else {
				 BaseDatos.conectarBaseDeDatos(nombre, conexion, basesDeDatos, true);
				 archivoXml.crearXML(basesDeDatos);
			     verPaneBD();
				
			 }
			 
			 this.txfNombreDB.clear();
		}
		
		//Obtener el item seleccionado del combobox
		@FXML
		public void eliminarBaseDeDatos() {
			String seleccionado = obtenerSeleccionado(cbDB);
			if(seleccionado != null) {
				this.btnEliminar.setDisable(false);
				this.btnEliminar.setOnAction(event->{
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setHeaderText("Eliminar base de datos");
					alert.setTitle("Confirmación");
					alert.setContentText("¿Estas seguro que desea eliminar la base de datos?");
					Optional<ButtonType> action = alert.showAndWait();
					if (action.get() == ButtonType.OK) {
						BaseDatos.eliminarDB(seleccionado);
						basesDeDatos.remove(seleccionado);
						xmlInfoDB(basesDeDatos);
						Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
				        alert2.setHeaderText("EXITO");
				        alert2.setTitle("Info");
				        alert2.setContentText("La Base de Datos ha sido eliminada");
				        alert2.showAndWait();
				        limpiarDatosMostrados();
				        verPaneBD();
				        this.btnEliminar.setDisable(true);
					} 
					
			    });
					
			}else {
				this.cbDB.setPromptText("Bases de datos creadas");
			}
		}
		 
	@FXML
	public void crearTabla() throws SQLException {
		if(this.txfNombreTabla.getText().isBlank() || this.txfNombreTabla.getText().isEmpty()) {
			 Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("Error: El campo Nombre Tabla está vacio");
			alert.showAndWait();
		}else {
		String nombreDB = obtenerSeleccionado(cbTablas);//Obtenemos el nombre de la base de datos seleccionada
		String nombreTabla = this.txfNombreTabla.getText();//Obtenemos el nombre de la tabla introducido en el textField
		this.conexion =  DriverManager.getConnection("jdbc:hsqldb:file:src/DDBB/"+ nombreDB, usuario,contrasenia);
		BaseDatos.crearTabla(nombreDB, this.conexion, nombreTabla);
		basesDeDatos.get(nombreDB).add(nombreTabla);// Añadimos el nombre de la tabla a la base de datos seleccionada
		verPaneTablas();
		xmlInfoDB(basesDeDatos);
		this.cbTablas.getSelectionModel().clearSelection();
		this.txfNombreTabla.setText(null);
		}
	}
	
	@FXML
	public void eliminarTabla(){
		String datosConexion = this.lblNombreTabla.getText();
		String[] partes = datosConexion.split("/");
		String nombreBD = partes[0];
		String nombreTabla = partes[1];
		try {
			this.conexion =  DriverManager.getConnection("jdbc:hsqldb:file:src/DDBB/"+ nombreBD, usuario,contrasenia);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText("Eliminar tabla");
		alert.setTitle("Confirmación");
		alert.setContentText("¿Estas seguro que desea eliminar esta tabla?");
		Optional<ButtonType> action = alert.showAndWait();
		if (action.get() == ButtonType.OK) {
			BaseDatos.eliminarTabla(nombreBD,conexion,nombreTabla);
			 String elementoAEliminar = nombreTabla;
		        List<String> listaValores = basesDeDatos.get(nombreBD);
		        if (listaValores.contains(elementoAEliminar)) {
		            listaValores.remove(elementoAEliminar);
		            System.out.println("Se eliminó el elemento " + elementoAEliminar);
		        } 
			
			System.out.println(basesDeDatos.values());
			verPaneTablas();
			xmlInfoDB(basesDeDatos);
			
		} 
	}
	
	
	@FXML
	public void activarBtnCrearTablas() {
		if(this.cbTablas.getSelectionModel().getSelectedItem() == null) {
			this.btnCrearTabla.setDisable(true);
		}else {
			this.btnCrearTabla.setDisable(false);
		}
	}
	
	
	 public void limpiarDatosMostrados() {
		 this.vbInfo.getChildren().clear();
	    	this.cbDB.getItems().clear();
	    	this.cbTablas.getItems().clear();
	 }
	
	//Ver TEMA 3
	@FXML
	public void verTema3() {
		this.paneDB.setVisible(true);
		this.paneNombreXML.setVisible(false);
	}
	
	// Ver TEMA 2
	@FXML
	public void verTema2() {
		this.paneNombreXML.setVisible(true);
		this.paneDB.setVisible(false);
	}
	
	
	
	@FXML
	public void verPaneBD() {

		this.paneBaseDeDatos.setVisible(true);
		this.paneTablas.setVisible(false);
		this.lblInfoDB.setText("BD Creadas");
		this.vbInfo.getChildren().clear();
		limpiarDatosMostrados();
		cargarCombos();
		for(String clave : basesDeDatos.keySet()) {
			Label nueva = new Label("+"+clave);
			nueva.getStyleClass().add("custom-label");
			this.vbInfo.getChildren().add(nueva);
		}
		
	}
	@FXML
	public void verPaneTablas() {

		this.paneBaseDeDatos.setVisible(false);
		this.paneTablas.setVisible(true);
		this.lblInfoDB.setText("Tablas");
		this.vbInfo.getChildren().clear();
		limpiarDatosMostrados();
		cargarCombos();
		for(String clave : basesDeDatos.keySet()) {
			Label nueva = new Label("+"+clave);
			nueva.getStyleClass().add("custom-label");
			this.vbInfo.getChildren().add(nueva);
			for (String valor : basesDeDatos.get(clave)) {//Recorremos el valor de "basesDeDatos" que es una lista, en este caso de posibles tablas
				Hyperlink link = new Hyperlink(valor);
			    link.setOnAction(event -> {
			            this.lblNombreTabla.setText(clave+"/"+valor);
						double divisor = 0.2677;
						this.paneDB.setDividerPosition(1, divisor);
						persona.clear();
						try {
							this.conexion =  DriverManager.getConnection("jdbc:hsqldb:file:src/DDBB/"+clave, usuario,contrasenia);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						BaseDatos.mostrarDatos(this.conexion, valor, persona);
						
			 });
           	 this.vbInfo.getChildren().add(link);
			}
		}
	}

	
	@FXML
	public void insertar() throws SQLException{
		persona.clear();
		this.lblModificar.setText("Insertar Registro");
		this.btnInsertarRegistro.setVisible(true);
		if(this.txfId.getText().isBlank() || this.txfId.getText().isEmpty() || this.txfNombre.getText().isBlank() || this.txfNombre.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("Error: Aguno de los campos están vacios");
			alert.showAndWait();
		}else {
			String datosConexion = this.lblNombreTabla.getText();
			String[] partes = datosConexion.split("/");
			String nombreBD = partes[0];
			String nombreTabla = partes[1];
			this.conexion =  DriverManager.getConnection("jdbc:hsqldb:file:src/DDBB/"+nombreBD, usuario,contrasenia);
			int id = Integer.parseInt(this.txfId.getText());
			String nombre = this.txfNombre.getText();
			BaseDatos.insertarRegistro(nombreTabla,id, nombre, this.conexion);
			this.txfId.clear();
			this.txfNombre.clear();
			this.conexion =  DriverManager.getConnection("jdbc:hsqldb:file:src/DDBB/"+nombreBD, usuario,contrasenia);
			BaseDatos.mostrarDatos(this.conexion, nombreTabla, persona);
		
			
		}
		
	}
	
	@FXML
	public void eliminarRegistro() {
		String datosConexion = this.lblNombreTabla.getText();
		String[] partes = datosConexion.split("/");
		String nombreBD = partes[0];
		String nombreTabla = partes[1];
		try {
			
			this.conexion =  DriverManager.getConnection("jdbc:hsqldb:file:src/DDBB/"+nombreBD, usuario,contrasenia);
			Integer id = eventosTabla();
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setHeaderText("Eliminar registro");
			alert.setTitle("Confirmación");
			alert.setContentText("¿Estas seguro que desea eliminar el registro seleccionado?");
			Optional<ButtonType> action = alert.showAndWait();
			if (action.get() == ButtonType.OK) {
				BaseDatos.eliminarRegistro(this.conexion, nombreTabla, id);
				for(int i = 0; i<persona.size();i++) {
					if(persona.get(i).getId()==id) {
						persona.remove(i);
						this.tbTabla.getSelectionModel().clearSelection();
					}
				}
				this.btnEliminarRegistro.setDisable(true);
				this.btnModificarRegistro.setVisible(false);
				this.btnInsertarRegistro.setVisible(true);
			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@FXML
	public void actualizarRegistro() {
		if(this.txfId.getText().isBlank() || this.txfId.getText().isEmpty() || this.txfNombre.getText().isBlank() || this.txfNombre.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("Error: Alguno de los campos están vacios");
			alert.showAndWait();
		}else {
			String datosConexion = this.lblNombreTabla.getText();
			String[] partes = datosConexion.split("/");
			String nombreBD = partes[0];
			String nombreTabla = partes[1];
			try {
				Integer id = eventosTabla();
				Integer id2 = Integer.parseInt(this.txfId.getText());
				String nombre = this.txfNombre.getText();
				
				this.conexion =  DriverManager.getConnection("jdbc:hsqldb:file:src/DDBB/"+nombreBD, usuario,contrasenia);
				BaseDatos.actualizarRegistro(conexion,nombreTabla, id2, nombre, id);
				for(int i = 0; i<persona.size();i++) {
					if(persona.get(i).getId()==id) {
						persona.get(i).setId(id2);
						persona.get(i).setNombre(nombre);
						this.tbTabla.getSelectionModel().clearSelection();
					}
				}
				this.btnEliminarRegistro.setDisable(true);
				this.btnModificarRegistro.setVisible(false);
				this.btnInsertarRegistro.setVisible(true);
				this.tbTabla.getSelectionModel().clearSelection();
				this.txfId.clear();
				this.txfNombre.clear();
				//this.tbTabla.setItems(persona);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	}
	public Integer eventosTabla() {
		
		this.tbTabla.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			
			if (newValue != null) {
                idRegistro = newValue.getId();
                this.btnEliminarRegistro.setDisable(false);
                this.btnModificarRegistro.setVisible(true);
                this.btnInsertarRegistro.setVisible(false);
                this.lblModificar.setText("Modificar registro");
               
                
            }
            else {
            	this.btnEliminarRegistro.setDisable(false);
                this.btnModificarRegistro.setVisible(false);
                this.btnInsertarRegistro.setVisible(true);
                this.lblModificar.setText("Insertar Registro");
               return;
            }
            
        });
		this.tbTabla.refresh();
		return idRegistro;
	}
	
	
	public void seleccionarDB() {
		
	}
	
	public String obtenerSeleccionado(ComboBox combo) {
		String seleccionado = (String) combo.getSelectionModel().getSelectedItem();
		return seleccionado;
	}
	
	public void cargarCombos() {
	
		this.cbDB.getItems().addAll(basesDeDatos.keySet());
    	this.cbTablas.getItems().addAll(basesDeDatos.keySet());
	}
	
	public void xmlInfoDB(Map<String, List<String>> basesDeDatos2) {
		
	    ArchivoXml nuevo = new ArchivoXml(basesDeDatos2);
	    nuevo.crearXML(basesDeDatos2);
	}
	
	

	public void pruebaMap() {
		System.out.println(basesDeDatos.keySet().toString());
		System.out.println(basesDeDatos.values());
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//dbExistentes();//Bases de datos reales
		
		archivoXml.verXML(basesDeDatos);
		this.tcId.setCellValueFactory(new PropertyValueFactory("id"));
        this.tcNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        this.tbTabla.setItems(persona);
		eventosTabla();
	}
	

}
