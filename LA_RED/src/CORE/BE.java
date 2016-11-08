/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CORE;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import la_red.LA_RED;
import static la_red.LA_RED.globalSocket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author SAMSUNG
 */
public class BE extends Thread{
    private static JSONParser JSON = new JSONParser();
    public static Object read( String endpoint, JSONObject objeto){
        try {
            JSONObject data = new JSONObject();
            DataOutputStream SALIDA = new DataOutputStream(globalSocket.getOutputStream());
            data.put("endpoint", endpoint);
            data.put("objeto",objeto);
            SALIDA.writeUTF( data.toJSONString() );
//            DataInputStream ENTRADA = new DataInputStream(globalSocket.getInputStream());
//            String response = ENTRADA.readUTF();
            try {
                sleep( 500 );
            } catch (Exception e) {
            }
            System.out.println("El Resultado es: " + LA_RED.responseJSON);
            return LA_RED.responseJSON;
        } catch (Exception e) {
            System.err.println("Error al leer");
            return "ERROR";
        }
    }
    public static boolean CREATE(String tabla, Object[] valores){
        
        if( globalSocket != null && valores.length != 0){
            try {
                String query = tabla + "%VALUES";
                for (int i = 0; i < valores.length; i++) {
                    if( valores[i] instanceof String){
                        query += "'" + valores[i] + "'";
                    }else{
                        query += valores[i];
                    }
                    query += i == valores.length - 1 ? ")" : ",%&";
                }
                DataOutputStream SALIDA = new DataOutputStream(globalSocket.getOutputStream());
                JSONObject data = new JSONObject();
                data.put("query", query);
                data.put("type", "CREATE");
 
                SALIDA.writeUTF( data.toJSONString() );
                DataInputStream ENTRADA = new DataInputStream(globalSocket.getInputStream());
                String response = ENTRADA.readUTF();
                System.out.println("El Resultado es: " + response);
            } catch (Exception e) {
            }
        }
        return false;
    }
    public static Object READ(String tabla, String[] columnas, Object[][] where){
        JSONObject data = new JSONObject();
        String query = "SELECT ";
        if( globalSocket != null ){
           data.put("tabla", tabla );
           if( columnas != null && columnas.length > 0){
               query += "`id`,";
               for (int i = 0; i < columnas.length; i++) {
                   query +=  "`" + columnas[i] + ( i == columnas.length - 1 ? "` " : "`, ");
               }
           }else{
               query += "* ";
           }
           query += "FROM " + tabla;
           if( where != null && where.length > 0 ){
               query += " WHERE ";
               for (int i = 0; i < where.length; i++) {
                   if( where[i].length == 3){
                       query += "`" + where[i][0] + "` " + where[i][1] + " '" + where[i][2] + "'";
                   }else{
                       System.err.println("CONDICION INCOMPLETA");
                       return "CONDICION INCOMPLETA";
                   }
                   query += i == where.length - 1 ? "" : " AND ";
               }
           }
            System.out.println(query);
            try {
                DataOutputStream SALIDA = new DataOutputStream(globalSocket.getOutputStream());
                data.put("type", "READ");
                SALIDA.writeUTF( data.toJSONString() );
                DataInputStream ENTRADA = new DataInputStream(globalSocket.getInputStream());
                String response = ENTRADA.readUTF();
                System.out.println("El Resultado es: " + response);
                return JSON.parse(response);
            } catch (Exception e) {
                System.out.println("Error al leer");
            }
        }
        return new Object[]{};
    }
}
