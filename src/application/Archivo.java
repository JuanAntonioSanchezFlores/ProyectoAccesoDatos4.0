package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Archivo {

	String nombreArchivo;

	public Archivo(String nombreArchivo) {
		super();
		this.nombreArchivo = nombreArchivo;
		
	}

	//Método para crear un nuevo archivo que recibe dos parámetros, "nombre" que representa el nombre del archivo o la ruta al mismo
	// "texto" que representa el texto a escribir en el archivo
	public void crearArchivo(String nombre, String texto) {
		
		try {
			File archivo = new File(nombre);
			if(!archivo.exists()) {
				archivo.createNewFile();
				FileWriter fw = new FileWriter(archivo);
				fw.write(texto);
				fw.close();
			}else {
				FileWriter fw = new FileWriter(archivo);
				fw.write(texto);
				fw.close();
			}
		} catch (IOException ex) {
			System.out.println(ex);
			}
		    
	}
	
}
