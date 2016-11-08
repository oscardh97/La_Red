/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package la_red;

import CORE.VISTA_USER_SMALL;
import java.awt.Component;
import static java.lang.Integer.parseInt;
import org.json.simple.JSONObject;

/**
 *
 * @author SAMSUNG
 */
public class USUARIO {
    private int id, tipo, activo;
    private String nombre, apellido, user_name, nacionalidad, telefono = "";
    private VISTA_USER_SMALL vistaSmall;

    public USUARIO(JSONObject data, Component parent) {
        this.id = parseInt( data.get("id").toString() );
        this.nombre = data.get("nombre").toString();
        this.apellido = data.get("apellido").toString();
        this.user_name = data.get("user_name").toString();
        this.tipo = parseInt( data.get("tipo").toString() );
        this.activo = parseInt( data.get("activo").toString() );
        this.nacionalidad = data.get("nacionalidad").toString();
        try {
            this.telefono = data.get("telefono").toString();
        } catch (Exception e) {
        }
        try {
            vistaSmall = new VISTA_USER_SMALL(id, nombre, apellido, user_name, parent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public int getId() {
        return id;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return user_name;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
    
    
    public JSONObject toJSON(){
        JSONObject retorno = new JSONObject();
        retorno.put("id", id);
        retorno.put("nombre", nombre);
        retorno.put("apellido", apellido);
        retorno.put("user_name", user_name);
        retorno.put("tipo", tipo);
        retorno.put("activo", tipo);
        return retorno;
    }

    public VISTA_USER_SMALL getVistaSmall() {
        return vistaSmall;
    }
    
    
}
