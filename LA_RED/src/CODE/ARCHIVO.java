/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CODE;

import java.io.Serializable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author SAMSUNG
 */
public class ARCHIVO implements Serializable{
    private String nombre;
    private String archivo;
    private String archivoOriginal;
    private boolean haCambiado = false;
    private JSONParser myParser = new JSONParser();

    public ARCHIVO(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public ARCHIVO(JSONObject myJSON) {
        this.nombre = myJSON.get("nombre").toString();
        try {
            JSONObject archivoJSON = (JSONObject)myParser.parse( myJSON.get("archivo").toString() );
            this.archivo = archivoJSON.get("archivo").toString();
            this.archivoOriginal = archivoJSON.get("archivo_original").toString();
            haCambiado = !archivo.equals(archivoOriginal);
        } catch (Exception e) {
            this.archivo = myJSON.get("archivo").toString();
            this.archivoOriginal = myJSON.get("archivo").toString();
        }
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArchivo() {
        archivo = archivo.replaceAll( "%25","\"");
        archivo = archivo.replaceAll( "%24","\'");
        System.out.println("archivo ---> " + archivo);
        return archivo;
    }

    public String getArchivoOriginal() {
        archivoOriginal = archivoOriginal.replaceAll( "%25","\"");
        archivoOriginal = archivoOriginal.replaceAll( "%24","\'");
        return archivoOriginal;
    }

    public void setArchivoOriginal(String archivoOriginal) {
        archivoOriginal = archivoOriginal.replaceAll("\"", "%25");
        archivoOriginal = archivoOriginal.replaceAll("\'", "%24");
        this.archivoOriginal = archivoOriginal;
    }

    public void setArchivo(String archivo) {
        archivo = archivo.replaceAll("\"", "%25");
        archivo = archivo.replaceAll("\'", "%24");
        this.archivo = archivo;
    }

    @Override
    public String toString() {
        return nombre + ( haCambiado ? "( CAMBIÓ )" : "");
    }
    public JSONObject toJSON(boolean isFork){
        JSONObject myJSON = new JSONObject();
        myJSON.put("nombre", nombre);
        archivo = archivo.replaceAll("\"", "%25");
        archivo = archivo.replaceAll("\'", "%24");
        if( isFork ){
            JSONObject archivoJSON = new JSONObject();
            archivoJSON.put("archivo", archivo);
            archivoOriginal = archivoOriginal.replaceAll("\"", "%25");
            archivoOriginal = archivoOriginal.replaceAll("\'", "%24");
            archivoJSON.put("archivo_original", archivoOriginal);
            myJSON.put("archivo", archivoJSON );
        }else{
            myJSON.put("archivo", archivo);
        }
        
        return myJSON;
    }
    
}
