/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FRIENDLY;

import java.util.ArrayList;
import la_red.USUARIO;

/**
 *
 * @author SAMSUNG
 */
public class grupo {
    private String nombre;
    private int id;
    private ArrayList< USUARIO > usuarios = new ArrayList();

    public grupo(String nombre, int id) {
        this.nombre = nombre;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<USUARIO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<USUARIO> usuarios) {
        this.usuarios = usuarios;
    }
    public void setUsuario(USUARIO nUsuario){
        usuarios.add(nUsuario);
    }

    @Override
    public String toString() {
        return "grupo - " + nombre ;
    }
    
}
