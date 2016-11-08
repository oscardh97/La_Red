/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CODE;

import java.io.Serializable;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author SAMSUNG
 */
public class CARPETA implements Serializable{
    private String nombre;
    private ArrayList< ARCHIVO > archivos = new ArrayList();
    private ArrayList< CARPETA > carpetas = new ArrayList();
    private JSONObject JSON;

    public CARPETA(String nombre) {
        this.nombre = nombre;
    }
    
    public ArrayList<ARCHIVO> getArchivos() {
        return archivos;
    }

    public CARPETA(JSONObject JSON) {
        System.out.println("JSON --->" + JSON);
        this.nombre = JSON.get("nombre").toString();
        for (Object nObj : (JSONArray)JSON.get("carpetas")) {
            CARPETA nCarpeta = new CARPETA((JSONObject)nObj);
            this.carpetas.add(nCarpeta);
        }
        for (Object nObj : (JSONArray)JSON.get("archivos")) {
            ARCHIVO nArchivo = new ARCHIVO((JSONObject)nObj);
            this.archivos.add(nArchivo);
        }
        this.JSON = JSON;
    }

    public void setArchivos(ArrayList<ARCHIVO> archivos) {
        this.archivos = archivos;
    }

    public void addItem(ARCHIVO nArchivo){
        this.archivos.add(nArchivo);
    }
    public void deleteItem(CARPETA nCarpeta ){
        this.carpetas.remove(nCarpeta);
    }
    public void deleteItem(ARCHIVO nArchivo ){
        this.archivos.remove(nArchivo);
    }

    public void addItem(CARPETA nCarpeta){
        this.carpetas.add(nCarpeta);
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<CARPETA> getCarpetas() {
        return carpetas;
    }

    public void setCarpetas(ArrayList<CARPETA> carpetas) {
        this.carpetas = carpetas;
    }
    public JSONObject toJSON( boolean isFork){
        JSONObject myJSON = new JSONObject();
        myJSON.put("nombre", nombre);
        JSONArray carpetasJSON = new JSONArray();
        JSONArray archivosJSON = new JSONArray();
        for (CARPETA col : carpetas ) {
            carpetasJSON.add( col.toJSON(isFork) );
        }
        for (ARCHIVO col : archivos ) {
            archivosJSON.add( col.toJSON(isFork) );
        }
        myJSON.put("carpetas", carpetasJSON);
        myJSON.put("archivos", archivosJSON);
        return myJSON;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
    
}
