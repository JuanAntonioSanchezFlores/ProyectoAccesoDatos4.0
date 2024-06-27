package application;

import java.io.File; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;


public class BaseDatos {

	public BaseDatos() {}
	
	private static Connection conexion;
	private static Map<String, List<String>> basesDeDatos2 = new HashMap<>();
	
	public static void conectarBaseDeDatos(String nombreDB, Connection conexion, Map<String, List<String>> basesDeDatos2, boolean alerta) {
		boolean mostrarAlert = alerta;
		String nombre = nombreDB;
		String cadenaConexion="jdbc:hsqldb:file:src/DDBB/"+ nombreDB;
		
		//Objeto para gestionar la conexion
		
		try {
			//Cargarmos el driver para HSQLDB
            Class.forName("org.hsqldb.jdbcDriver"); 
          
            
           
            try {   // Nos conectamos a la base de datos o la creamos si no existe 
                conexion = DriverManager.getConnection(cadenaConexion,"","");
                basesDeDatos2.put(nombreDB, new ArrayList<String>());
                if(mostrarAlert) {
	                Alert alert = new Alert(Alert.AlertType.INFORMATION);
	                alert.setHeaderText("EXITO");
	                alert.setTitle("Info");
	                alert.setContentText("La Base de Datos ha sido creada");
	                alert.showAndWait();
                }
            }catch(Exception e) {
            	Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Herror al cargar el Driver");
                alert.setTitle("Error");
                alert.setContentText("ERROR: No se ha podido cargar la base de datos\n"+ e.getMessage());
                alert.showAndWait();
             return;
            }
        }catch(Exception e) {  
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Herror al cargar el Driver");
            alert.setTitle("Error");
            alert.setContentText("ERROR: No se ha podido cargar la base de datos");
            alert.showAndWait();
              
        } 
		
	}
	
	public static void eliminarDB(String nombre) {
		File carpeta = new File("src/DDBB");
		
		File[] carpetas = carpeta.listFiles();
		for (int i=0; i<carpetas.length; i++) {
		    String separador = Pattern.quote(".");
		    String[] partes = carpetas[i].getName().split(separador);
		    if(partes[0].equals(nombre)) {
		    	 carpetas[i].delete();
			}
		    	
		}
	}
	
	//Metodo con tres parametros, nombre (nombre BD), conexion (la conexion), nombreTabla (nombre de la tabla con los campos definidos)
	public static void crearTabla(String nombre, Connection conexion, String nombreTabla) {
		// Consulta SQL para crear la tabla
		BaseDatos.conectarBaseDeDatos(nombre, conexion, basesDeDatos2, false);
		
        String consulta = "CREATE TABLE IF NOT EXISTS "+nombreTabla+" (id INT, nombre VARCHAR(255))";
        
        try  
        {
        	// Creamos la sentencia
        	Statement sentencia = conexion.createStatement();
             // Ejecutar la consulta para crear la tabla
             sentencia.executeUpdate(consulta);
             Alert alert = new Alert(Alert.AlertType.INFORMATION);
             alert.setHeaderText("EXITO");
             alert.setTitle("Info");
             alert.setContentText("La tabla ha sido creada correctamente");
             alert.showAndWait();
             conexion.close();
            
        } catch (SQLException e) {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("ERROR");
            alert.setTitle("Error");
            alert.setContentText("ERROR: No se ha podido crear la tabla\n"+ e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
	}
	
	 public static void eliminarTabla(String nombre, Connection conexion, String nombreTabla){
		
		 BaseDatos.conectarBaseDeDatos(nombre, conexion, basesDeDatos2, false);
		 try 
	      {  
			 Statement  sentencia   = conexion.createStatement();  
			 //Consulta que elimina las tablas
			 sentencia.executeUpdate("DROP TABLE IF EXISTS "+nombreTabla);
			 Alert alert = new Alert(Alert.AlertType.INFORMATION);
             alert.setHeaderText("EXITO");
             alert.setTitle("Info");
             alert.setContentText("La tabla se ha eliminado correctamente");
             alert.showAndWait();
             conexion.close();
	      }
		 catch (Exception e)
		 {  
			 Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setHeaderText("ERROR");
	            alert.setTitle("Error");
	            alert.setContentText("ERROR: La tabla no se ha podido eliminar\n"+ e.getMessage());
	            alert.showAndWait();
	            e.printStackTrace();   
		 }
	 }
	
	
	
	public static void insertarRegistro(String nombreTabla, int id, String valor, Connection conexion){
		
		
		// Consulta SQL para la inserción
        String consulta= "INSERT INTO "+nombreTabla+" (id, nombre) VALUES (?, ?)";
        
        // Crear una declaración preparada con los valores a insertar
        try {
        	PreparedStatement sentenciaPreparada = conexion.prepareStatement(consulta);
            sentenciaPreparada.setInt(1, id); //id=0
            sentenciaPreparada.setString(2, valor); //nombre=Juan
            
            // Ejecutar la declaración de inserción
            int rowsAffected = sentenciaPreparada.executeUpdate();
            
            if (rowsAffected > 0) {
            	Alert alert = new Alert(Alert.AlertType.INFORMATION);
	            alert.setHeaderText("EXITO");
	            alert.setTitle("Info");
	            alert.setContentText("El registro se ha insertado correctamente");
	            alert.showAndWait();
	            conexion.close();
	            
            }else {
            	Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setHeaderText("ERROR");
	            alert.setTitle("Error");
	            alert.setContentText("ERROR: El registro no ha sido insertado");
	            alert.showAndWait();
	            
            } 
        
        } 
        catch (SQLException e) {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("ERROR");
            alert.setTitle("Error");
            alert.setContentText("ERROR:El registro no se ha insertado\n"+ e.getMessage());
            alert.showAndWait();
            
        }
    
	}
	
	public static void mostrarDatos(Connection conexion, String nombreTabla, ObservableList<Persona> persona){
		try
		{
			
			Statement sentencia = conexion.createStatement();
              
			// Consulta SQL para seleccionar todos los registros de la tabla
			String cadenaSQL = "SELECT * FROM "+nombreTabla; 
        
			// Ejecutar la consulta y obtener el conjunto de resultados
			ResultSet resultSet = sentencia.executeQuery(cadenaSQL);
        
			System.out.println("* Mostrando Registros");
			// Procesar los resultados
			while (resultSet.next()) {
				int id = resultSet.getInt("id"); //podemos poner el índice de la columna
				String nombre = resultSet.getString("nombre");
				Persona nueva = new Persona(id, nombre);
				persona.add(nueva);
				System.out.println("ID: " + id + ", Nombre: " + nombre);
			}
			conexion.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public static void eliminarRegistro(Connection conexion,String nombreTabla, int id){
		try
		{
			
			Statement sentencia = conexion.createStatement();
              
			// Consulta SQL para eliminar un registro de la tabla
			String cadenaSQL = "DELETE FROM "+nombreTabla+" WHERE id = "+id;
        
			// Ejecutar la consulta y obtener los registros afectados
			int filasAfectadas = sentencia.executeUpdate(cadenaSQL);

			if (filasAfectadas > 0) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
	            alert.setHeaderText("EXITO");
	            alert.setTitle("Info");
	            alert.setContentText("El registro se ha eliminado correctamente");
	            alert.showAndWait();
	            conexion.close();
			}else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setHeaderText("ERROR");
	            alert.setTitle("Error");
	            alert.setContentText("ERROR:El registro no se ha podido eliminar");
			}
				
    
			} catch (SQLException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setHeaderText("ERROR");
	            alert.setTitle("Error");
	            alert.setContentText("ERROR:El registro no se ha insertado\n"+ e.getMessage());
				
			}
		
	}
	
	public static void actualizarRegistro(Connection conexion,String nombreTabla, int id2, String nombre, int id){
		try
			{
				
				Statement sentencia = conexion.createStatement();
		              
				// Consulta SQL para modificar un registro de la tabla
				String cadenaSQL = "UPDATE "+nombreTabla+" SET id = "+id+", nombre= '"+nombre+"' WHERE id = "+id;
	            
					// Ejecutar la consulta y obtener los registros afectados
					int filasAfectadas = sentencia.executeUpdate(cadenaSQL);

					if (filasAfectadas > 0)
						System.out.println("Registro actualizado");
					else
						System.out.println("Error al actualizar el registro");
	        
					} catch (SQLException e) {
						e.printStackTrace();
					}
				
			}
}

