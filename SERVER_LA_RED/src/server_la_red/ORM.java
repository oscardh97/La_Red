/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_la_red;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Date;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static server_la_red.SERVER_LA_RED.cmdBD;

/**
 *
 * @author SAMSUNG
 */
public class ORM {
    public static Object[] getColumNombres(String tablaNombre, boolean toString){
        String query = "SELECT * FROM `" + tablaNombre + "`";
        try {
            ResultSet result = cmdBD.executeQuery( query );
            System.out.println(query);
            ResultSetMetaData resultMD = result.getMetaData();
            if( resultMD.getColumnCount() > 0 ){
                String[] nombreColumnas = new String[ resultMD.getColumnCount() ];
                String stringColumnas = "";
                for(int i = 1; i <= resultMD.getColumnCount(); i++){
                    nombreColumnas[ i - 1] = resultMD.getColumnName(i);
                    stringColumnas += "`" + tablaNombre + "`.`" + resultMD.getColumnName(i) + "`";
                    stringColumnas += i == resultMD.getColumnCount() ? " " : ",";
                    
                }
                return toString ? new Object[]{stringColumnas, resultMD.getColumnCount()} : nombreColumnas;
            }
            return new String[]{};
        } catch (Exception e) {
            System.err.println("Error al obtener nombres de columnas");
            System.err.println(query);
        }
        return new String[]{};
    }
    public static JSONObject create(String tabla, JSONObject values){
        try {
//            String[] queryOptions = query.split("%VALUES");
//            Object[] nombreColumnas = getColumNombres( queryOptions[0], true );
//            String[] values = queryOptions[1].split("%&");
//            System.out.println(nombreColumnas[1].toString());
//            if( Integer.parseInt( nombreColumnas[1].toString() ) > 0 ){
//                if( Integer.parseInt( nombreColumnas[1].toString() ) == values.length){
                    String insertQuery = "INSERT INTO `" + tabla +"` (";
//                    for (String value : values) {
//                        insertQuery += value;
//                    }
                    String columNombres = "";
                    String valuesStr = " VALUES( ";
                    Set<String> keys = values.keySet();
                    int cont = 0;
                    for (String key : keys) {
                        System.out.println(key + " = " + values.get(key));
                        columNombres += "`" + key + "`";
                        if( values.get(key) instanceof String){
                            valuesStr += "'" + values.get(key) + "'";
                        }else{
                            valuesStr += values.get(key);
                        }
                        if( cont < keys.size() - 1){
                            columNombres += ",";
                            valuesStr += ",";
                        }else{
                            columNombres += ")";
                            valuesStr += ")";
                        }
                        cont++;
                    }
                    insertQuery += columNombres + valuesStr;
                    System.out.println(insertQuery);
                    boolean creado = cmdBD.execute( insertQuery );
                    if( creado ){
                        String read = "SELECT * FROM `"+ tabla  + "` WHERE `id` = ( SELECT MAX( `id` ) FROM `" + tabla+ "`)";
                        System.out.println(read);
                        ResultSet response = cmdBD.executeQuery(read);
                        ResultSetMetaData resultMD = response.getMetaData();
                        JSONObject tupla = new JSONObject();
                        
                        while( response.next()){
                            for (int i = 1; i <= resultMD.getColumnCount(); i++) {
                                tupla.put( resultMD.getColumnName(i), response.getObject(i) );
                            }
                        }
                        return tupla;
                    }
//                }
//            }
        } catch (Exception e) {
            System.err.println("Error al momento de crear");
        }
        return (JSONObject)new JSONObject().put("Error",true);
    }
    public static String read(JSONObject query){
        String readQuery = "SELECT ";
        String tabla = query.get("tabla").toString();
        
        if( query.containsKey("columnas") ){
            readQuery += query.get("columnas").toString();
        }else{
            readQuery += "*";
        }
        
        readQuery += " FROM " + tabla;
        
        if( query.containsKey("condiciones") ){
            readQuery += " WHERE " + query.get("condiciones").toString();
        }
        System.out.println( readQuery );
        try {
            ResultSet response = cmdBD.executeQuery( readQuery );  
            ResultSetMetaData resultMD = response.getMetaData();
            JSONArray arregloResponse = new JSONArray();
            while( response.next()){
                JSONObject tupla = new JSONObject();
                for (int i = 1; i <= resultMD.getColumnCount(); i++) {
                    tupla.put( resultMD.getColumnName(i), response.getObject(i) );
                }
                arregloResponse.add( tupla );
            }
            
            return arregloResponse.toJSONString();
        } catch (Exception e) {
            System.out.println("Error al leer ( Server )");
        }
        
        return "No pudo leer";
    }
    public static Object READ(String tabla, String[] columnas, Object[][] where, Object[][] join){
        JSONObject data = new JSONObject();
        String query = "SELECT ";
           data.put("tabla", tabla );
           String columnasStr = "";
           if( columnas != null && columnas.length > 0){
               columnasStr += "`" + tabla + "`.`id`,";
               for (int i = 0; i < columnas.length; i++) {
                   columnasStr += "`" + tabla + "`.`" + columnas[i] + ( i == columnas.length - 1 ? "` " : "`, ");
               }
           }else{
               columnasStr += getColumNombres(tabla, true)[0];
           }
           String joinStr = "";
           if( join != null && join.length > 0){
                for (int i = 0; i < join.length; i++) {
                    String joinTabla = join[i][0].toString();
                    if( join[i][4] != null){
                        columnasStr += ",";
                        String[] columnsJoin = join[i][4].toString().split(",");
                        for (int j = 0; j < columnsJoin.length; j++) {
                            columnasStr += "`" + joinTabla + i + "`.`" + columnsJoin[j] + ( j == columnsJoin.length - 1 ? "` " : "`, ");
                        }
                    }
                    joinStr += " " + ( join[i][5] != null ? join[i][5] : "INNER JOIN") + " `" + joinTabla + "` AS `" + joinTabla + i + "` ON `" + tabla + "`.`" + join[i][1];
                    joinStr += "` " + join[i][2] + "`" + joinTabla + i + "`.`" + join[i][3] + "`";
                }
           }
           query += columnasStr + "FROM " + tabla + joinStr;
           
           if( where != null && where.length > 0 ){
               query += " WHERE ";
               for (int i = 0; i < where.length; i++) {
                   if( where[i].length == 3){
                       query += "`" + tabla + "`.`" + where[i][0] + "` " + where[i][1] + " '" + where[i][2] + "'";
                   }else{
                       System.err.println("CONDICION INCOMPLETA");
                       return "CONDICION INCOMPLETA";
                   }
                   query += i == where.length - 1 ? "" : " AND ";
               }
           }
            System.out.println(query);
            try {
                ResultSet response = cmdBD.executeQuery( query );  
                ResultSetMetaData resultMD = response.getMetaData();
                JSONArray arregloResponse = new JSONArray();
                while( response.next()){
                    JSONObject tupla = new JSONObject();
                    for (int i = 1; i <= resultMD.getColumnCount(); i++) {
                        System.out.println(resultMD.getColumnName(i));
                        boolean esFecha = response.getObject(i) instanceof Date;
                        tupla.put( resultMD.getColumnName(i), esFecha ? response.getObject(i).toString() : response.getObject(i) );
                    }
                    arregloResponse.add( tupla );
                }
                System.out.println(arregloResponse);
                return arregloResponse;
            } catch (Exception e) {
                System.out.println("Error al leer ( Server )");
            }
        return new Object[]{};
    }
    public static boolean DELETE(String tabla, Object[][] where){
        String query = "DELETE FROM `" + tabla + "`";
        if( where != null && where.length > 0 ){
            query += " WHERE ";
            for (int i = 0; i < where.length; i++) {
                if( where[i].length == 3){
                    query += "`" + tabla + "`.`" + where[i][0] + "` " + where[i][1] + " '" + where[i][2] + "'";
                }else{
                    System.err.println("CONDICION INCOMPLETA");
                    return false;
                }
                query += i == where.length - 1 ? "" : " AND ";
            }
            try {
                return cmdBD.execute( query );
            } catch (Exception e) {}
        }
        return false;
    }
}
