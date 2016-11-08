/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CODE;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PROYECTO extends CARPETA{
    private class COMENTARIO{
        private int idUser;
        private String comentario;

        public COMENTARIO(int idUser, String comentario) {
            this.idUser = idUser;
            this.comentario = comentario;
        }

        public int getIdUser() {
            return idUser;
        }

        public void setIdUser(int idUser) {
            this.idUser = idUser;
        }

        public String getComentario() {
            return comentario;
        }

        public void setComentario(String comentario) {
            this.comentario = comentario;
        }

        
        @Override
        public String toString() {
            return "COMENTARIO{" + "idUser=" + idUser + ", comentario=" + comentario + '}';
        }
        
    }
    private int id = -1;
     private int id_usuario_original;
    private ArrayList< COMENTARIO > comentarios;
    private String readme;
    public PROYECTO(String nombre) {
        super(nombre);
    }

    public PROYECTO(JSONObject JSON) {
        super(JSON);
        this.id = Integer.parseInt( JSON.get("id").toString() );
        if( JSON.containsKey("readme") && JSON.get("readme") != null){
            this.readme = JSON.get("readme").toString();
        }
        if( JSON.containsKey("id_usuario_original") ){
            id_usuario_original = Integer.parseInt( JSON.get("id_usuario_original").toString() );
        }
    }

    public PROYECTO(int id, String nombre) {
        super(nombre);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getReadme() {
        return readme;
    }

    public void setReadme(String readme) {
        this.readme = readme;
    }

    public int getId_usuario_original() {
        return id_usuario_original;
    }

    public void setId_usuario_original(int id_usuario_original) {
        this.id_usuario_original = id_usuario_original;
    }
    
    
    @Override
    public String toString() {
        return super.getNombre();
    }
}
