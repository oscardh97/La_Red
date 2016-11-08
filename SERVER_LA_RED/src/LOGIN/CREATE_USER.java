/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LOGIN;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server_la_red.ORM;

/**
 *
 * @author SAMSUNG
 */
public class CREATE_USER {
    public static boolean createUser(JSONObject objeto){
        JSONArray usuarios = (JSONArray)(ORM.READ("usuario", new String[]{"user_name"} , new Object[][]{
            {"user_name","=",objeto.get("user_name")}
        }, null));
        System.out.println(usuarios);
        if( usuarios.size() == 0){
            ORM.create("usuario", objeto);
            return true;
        }else{
            System.out.println("Usuario ya existe");
            return false;
        }
    }
}
