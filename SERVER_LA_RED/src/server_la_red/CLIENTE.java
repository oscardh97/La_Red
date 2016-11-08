/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_la_red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import static java.lang.Integer.parseInt;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import static server_la_red.SERVER_LA_RED.cmdBD;

/**
 *
 * @author SAMSUNG
 */
public class CLIENTE extends Thread{
    private Socket miSocket;
    private JSONParser JSON = new JSONParser();
    private int id = -1;
    private JSONObject datos = new JSONObject();
    public int getIdCliente() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
        
    public CLIENTE(Socket miSocket) {
        this.miSocket = miSocket;
        
        System.out.println("El Usuario se desconecto");
    }
    @Override
    public void run(){
        try{
        while( !miSocket.getKeepAlive() ){
            try {
                DataOutputStream SALIDA = new DataOutputStream(miSocket.getOutputStream());
                SALIDA.writeUTF( "BIENVENIDO" );
                DataInputStream ENTRADA = new DataInputStream(miSocket.getInputStream());
                String query = ENTRADA.readUTF();
                System.out.println("41" + query);
                JSONObject jsonQuery = (JSONObject)JSON.parse( query );
                System.out.println(jsonQuery);
                JSONObject objeto = (JSONObject)jsonQuery.get("objeto");
                System.out.println(objeto);
                if( jsonQuery.get("endpoint").equals("createUser")){
                    JSONObject respuesta = new JSONObject();
                    if( LOGIN.CREATE_USER.createUser(objeto) ){
                        respuesta.put("mensaje", "Usuario Creado");
                        responder( true, respuesta );
                    }else{
                        respuesta.put("mensaje", "Usuario Ya Existe");
                        responder( false, respuesta );
                    }
                }else if( jsonQuery.get("endpoint").equals("modificarUser")){
//                    ORM.create("usuario", objeto);
                    String queryStr = "UPDATE `usuario` SET ";
                    Set<String> keys = objeto.keySet();
                    int cont = 0;
                    for (String key : keys) {
                        System.out.println(key + " = " + objeto.get(key));
                        queryStr += key + " = ";
                        if( objeto.get(key) instanceof String){
                            queryStr += "'" + objeto.get(key) + "'";
                        }else{
                            queryStr += objeto.get(key);
                        }
                        
                        queryStr += cont == keys.size() - 1 ? "" :",";
                        cont++;
                    }
                    queryStr += " WHERE id = " + objeto.get("id").toString();
                    System.out.println(queryStr);
                    cmdBD.execute(queryStr);
                }else if( jsonQuery.get("endpoint").equals("ingresar")){
                    JSONArray response = (JSONArray)ORM.READ("usuario", null, new Object[][]{
                        {"password","=",objeto.get("password").toString()},
                        {"user_name","=",objeto.get("user_name").toString()}
                    }, null);
                    if( response.size() == 0 ){
                        JSONObject respuesta = new JSONObject();
                        respuesta.put("mensaje", "Usuario o contraseña invalido");
                        responder( true, respuesta );
                    }else{
                        JSONObject usuario = (JSONObject)response.get(0);
                        if( parseInt( usuario.get("activo").toString() ) == 1){
                            this.setId( Integer.parseInt( usuario.get("id").toString() ) );
                            this.datos = (JSONObject)usuario;
                            System.out.println(usuario);
                            responder( true, usuario.toJSONString() );
                        }else{
                            JSONObject respuesta = new JSONObject();
                            respuesta.put("mensaje", "Usuario deshabilitado");
                            responder( true, respuesta );
                        }
                    }
                }else if(jsonQuery.get("endpoint").equals("listarAmigos")){
                    
                    String strQuery = "SELECT U.`id`,U.`nombre`,U.`apellido`, U.`user_name`, `U`.tipo,`U`.nacionalidad,`U`.activo " +
                        "FROM `usuario` AS U,`amigo_usuario` AS AU " +
                        "WHERE AU.`id_usuario` = " + id + " AND AU.`id_amigo` = U.`id` GROUP BY U.`id`";
                    System.out.println(strQuery);
                    try {
                        ResultSet response = cmdBD.executeQuery( strQuery );  
                        ResultSetMetaData resultMD = response.getMetaData();
                        JSONArray arregloResponse = new JSONArray();
                        while( response.next()){
                            JSONObject tupla = new JSONObject();
                            for (int i = 1; i <= resultMD.getColumnCount(); i++) {
                                System.out.println(resultMD.getColumnName(i));
                                tupla.put( resultMD.getColumnName(i), response.getObject(i) );
                            }
                            arregloResponse.add( tupla );
                        }
                        System.out.println(arregloResponse);
                        responder( true, arregloResponse.toJSONString() );
//                        return arregloResponse;
                    } catch (Exception e) {
                        System.out.println(strQuery);
                        e.printStackTrace();
                        System.out.println("Error al leer ( Server )");
                    }
                }else if(jsonQuery.get("endpoint").equals("listarUsuarios")){
                    String strQuery = "SELECT U.`id`,U.`nombre`,U.`apellido`, U.`user_name`, `U`.tipo, `U`.activo, `U`.nacionalidad," +
                        "S.`id_receptor` = " + id + " AND S.`id_remitente` = U.`id` as solicitado," +
                        "S.`id_remitente` = " + id + " AND S.`id_receptor` = U.`id` as enviada, AU.`id_usuario`, AU.`id_amigo` " +
                        "FROM `usuario` AS U,`solicitud` AS S , `amigo_usuario` AS AU " +
                        "WHERE U.`id` != " + id + " AND AU.`id_usuario` = " + id + " AND U.`id` != AU.`id_amigo` GROUP BY U.`id`";
                    System.out.println(strQuery);
                    try {
                        ResultSet response = cmdBD.executeQuery( strQuery );  
                        ResultSetMetaData resultMD = response.getMetaData();
                        JSONArray arregloResponse = new JSONArray();
                        while( response.next()){
                            JSONObject tupla = new JSONObject();
                            for (int i = 1; i <= resultMD.getColumnCount(); i++) {
                                System.out.println(resultMD.getColumnName(i));
                                tupla.put( resultMD.getColumnName(i), response.getObject(i) );
                            }
                            arregloResponse.add( tupla );
                        }
                        System.out.println(arregloResponse);
                        responder( true, arregloResponse.toJSONString() );
//                        return arregloResponse;
                    } catch (Exception e) {
                        System.out.println(strQuery);
                        e.printStackTrace();
                        System.out.println("Error al leer ( Server )");
                    }
                }else if(jsonQuery.get("endpoint").equals("enviarSolicitud")){
                    JSONObject values = new JSONObject();
                        values.put("id_remitente", id);
                        values.put("id_receptor", Integer.parseInt(objeto.get("id").toString()));
                   responder( true, ORM.create("solicitud", values) );
                }else if(jsonQuery.get("endpoint").equals("listarSolicitudes")){
                    JSONArray response = (JSONArray)ORM.READ("solicitud", null, 
                        new Object[][]{
                            {"id_receptor","=",id}
                        }, 
                        new Object[][]{
                            {"usuario", "id_receptor","=", "id", null, null},
                            {"usuario", "id_remitente","=", "id","id,nombre,apellido,user_name,tipo,activo,nacionalidad", null}
                        });
                    System.out.println(response);
                    responder( true, response.toJSONString() );
                }else if(jsonQuery.get("endpoint").equals("eliminarAmigo")){
                    String delete = "DELETE FROM `amigo_usuario` WHERE ";
                    delete += "( `id_usuario` = " + id + " AND `id_amigo` = " + objeto.get("id").toString() + ") OR ";
                    delete += "( `id_usuario` = " + objeto.get("id").toString() + " AND `id_amigo` = " + id + ")";
                    cmdBD.execute(delete);
                }else if(jsonQuery.get("endpoint").equals("aceptarSolicitud")){
                    String insert = "INSERT INTO `amigo_usuario`( id_usuario, id_amigo ) VALUES";
                    insert += "(" + id + "," + objeto.get("id").toString() + "),";
                    insert += "(" + objeto.get("id").toString() + "," + id + ")";
                    String delete = "DELETE FROM `solicitud` WHERE `id_receptor` = " + id + " AND `id_remitente` = " + objeto.get("id").toString();
                    cmdBD.execute(delete);
                    cmdBD.execute(insert);
//                    ORM.DELETE("solicitud", new Object[][]{{"id","=", Integer.parseInt(objeto.get("id").toString())}});
//                    ORM.create("amigo_usuario", values2);
//                    responder( true, ORM.create("amigo_usuario", values) );
                }else if( jsonQuery.get("endpoint").equals("guardarArchivo") ){
                    JSONObject values = new JSONObject();
                        values.put("id_archivo", 1);
                        values.put("archivo", objeto.get("atchivo"));
                        System.out.println( values );
                   responder( true, ORM.create("archivo_version", values) );
                    
                }else if(jsonQuery.get("endpoint").equals("crearProyecto") ){
                    String insertar = "INSERT INTO `proyecto` (`nombre`, `id_usuario`, `archivo`) VALUES ";
                    boolean nuevos = false;
                    try{
                        JSONArray proyectos = (JSONArray)objeto.get("proyectos");
                        String update = "";
                        for (Object proyecto : proyectos) {
                            JSONObject pJSON = (JSONObject)JSON.parse( proyecto.toString() );
                            int idProyecto = Integer.parseInt( pJSON.get("id").toString());
                            if( idProyecto == -1 ){
                                nuevos = true;
                                insertar += "('" + pJSON.get("nombre") + "', ";
                                insertar += ( pJSON.containsKey("id_usuario") ? pJSON.get("id_usuario").toString() : id )+ ", ";
                                pJSON.remove("nombre");
                                pJSON.remove("id");
                                insertar += "'" + pJSON.toJSONString() + "')";
                                if( proyectos.indexOf(proyecto) != proyectos.size() - 1){
                                    insertar += ",";
                                }
                            }else{
                                update = "UPDATE `" +  objeto.get("tabla").toString() + "` SET ";
                                update += "`nombre` = '" + pJSON.get("nombre") + "', ";
                                pJSON.remove("nombre");
                                pJSON.remove("id");
                                update += "`archivo` = '" + pJSON.toJSONString() + "'";
//                                modificados = true;
                                update += " WHERE `id` = " + idProyecto + ";";
                                System.out.println(update);
                                cmdBD.execute( update );
                            }
                        }
                        if( objeto.containsKey("elimProyects")){
                            JSONArray elimProyects = (JSONArray)objeto.get("elimProyects");
                            for (Object proy : elimProyects) {
                                if( parseInt( proy.toString() ) == -1){
                                    continue;
                                }
                                String delete = "DELETE FROM `proyecto` WHERE id = " + parseInt( proy.toString() );
                                System.out.println(delete);
                                cmdBD.execute( delete );
                            }
                        }
                        if( nuevos ){
                            System.out.println("INSERTAR --> " + insertar);
                            cmdBD.execute( insertar );
                        }
//                        if( modificados ){
//                            System.out.println("ACTUALIZAR --> " + update);
//                            cmdBD.execute( update );
//                        }
                    }catch (Exception e ) {
                        System.out.println("ERROR --> 249");
                        System.err.println(e);
                    }
                }else if(jsonQuery.get("endpoint").equals("pullear") ){
                    String update = "UPDATE `copia_fork` SET `forkeado` = 1 WHERE `id` = " + objeto.get("id_proyecto");
                    boolean pulleado = cmdBD.execute(update);
                    JSONObject response = new JSONObject();
                    response.put("pulleado", pulleado);
                    responder( true, response );
                }else if(jsonQuery.get("endpoint").equals("listarProyectos") ){
                   String strQuery = "SELECT * FROM `proyecto` WHERE `id_usuario` = " + parseInt( objeto.get("id").toString() );
                    System.out.println(strQuery);
                    try {
                        ResultSet response = cmdBD.executeQuery( strQuery );  
                        ResultSetMetaData resultMD = response.getMetaData();
                        JSONArray arregloResponse = new JSONArray();
                        while( response.next()){
                            JSONObject tupla = new JSONObject();
                            for (int i = 1; i <= resultMD.getColumnCount(); i++) {
                                System.out.println(response.getObject(i));
                                if( resultMD.getColumnName(i).equals("archivo") ){
                                    JSONObject test = (JSONObject)(JSON.parse(response.getObject(i).toString()) );
                                    tupla.put( "archivos", test.get("archivos") );
                                    tupla.put( "carpetas", test.get("carpetas") );
                                    if( test.containsKey("readme")){
                                        tupla.put( "readme", test.get("readme"));
                                    }
                                }
//                                else if( resultMD.getColumnName(i).equals("archivo_original") ){
//                                    JSONObject test = (JSONObject)(JSON.parse(response.getObject(i).toString()) );
//                                    tupla.put( "archivo_original", test.get("archivo_original") );
////                                    tupla.put( "carpetas_originales", test.get("carpetas") );
//                                }
                                else{
                                    tupla.put( resultMD.getColumnName(i), response.getObject(i).toString() );
                                }
                            }
                            arregloResponse.add( tupla );
                        }
                        System.out.println(arregloResponse);
                        responder( true, arregloResponse );
//                        return arregloResponse;
                    } catch (Exception e) {
                        System.out.println(strQuery);
                        e.printStackTrace();
                        System.out.println("Error al leer ( Server )");
                    }
                }else if(jsonQuery.get("endpoint").equals("listarForks") ){
                   String strQuery = "SELECT * FROM `copia_fork` WHERE `id_usuario" + (objeto.get("pulleados").toString().equals("1") ?
                           "_original" : "") +
                           "` = " + parseInt( objeto.get("id").toString() )+
                           " AND `forkeado` = " + objeto.get("pulleados").toString();
                    System.out.println(strQuery);
                    try {
                        ResultSet response = cmdBD.executeQuery( strQuery );  
                        ResultSetMetaData resultMD = response.getMetaData();
                        JSONArray arregloResponse = new JSONArray();
                        while( response.next()){
                            JSONObject tupla = new JSONObject();
                            for (int i = 1; i <= resultMD.getColumnCount(); i++) {
                                System.out.println(resultMD.getColumnName(i));
                                if( resultMD.getColumnName(i).equals("archivo") ){
                                    JSONObject test = (JSONObject)(JSON.parse(response.getObject(i).toString()) );
                                    tupla.put( "archivos", test.get("archivos") );
                                    tupla.put( "carpetas", test.get("carpetas") );
                                    if( test.containsKey("readme")){
                                        tupla.put( "readme", test.get("readme"));
                                    }
                                }else{
                                    tupla.put( resultMD.getColumnName(i), response.getObject(i).toString() );
                                }
                            }
                            arregloResponse.add( tupla );
                        }
                        System.out.println(arregloResponse);
                        responder( true, arregloResponse );
//                        return arregloResponse;
                    } catch (Exception e) {
                        System.out.println(strQuery);
                        e.printStackTrace();
                        System.out.println("Error al leer ( Server )");
                    }
                }
                else if(jsonQuery.get("endpoint").equals("enviarMensaje") ){
                    int indexAmigo = -1;
                    for (CLIENTE cliente : SERVER_LA_RED.clientes) {
                        if( cliente.getIdCliente() == Integer.parseInt( objeto.get("id_amigo").toString() ) ){
                            indexAmigo = cliente.getIdCliente();
                        }
                    }
                    System.out.println("INDICE --------->" + indexAmigo);
                    JSONObject data = new JSONObject();
                    data.put("id_remitente", id);
                    data.put("id_receptor", Integer.parseInt( objeto.get("id_amigo").toString() ));
                    data.put("contexto", objeto.get("contexto").toString() );
                    data.put("asunto", objeto.get("asunto").toString().equals("") ? "Sin Asunto" :  objeto.get("asunto").toString());
                    data.put("leido", 0 );
                    if( objeto.containsKey("urgencia") ){
                        data.put("urgencia", objeto.get("urgencia"));
                        responder( true, ORM.create("notificacion", data) );
                    }else{
                        responder( true, ORM.create("mensaje", data) );
                    }
                    System.out.println(data);
//                    data.put("fecha", new Date().t );
                }else if(jsonQuery.get("endpoint").equals("obtenerMsj") ){
                    String queryStr = "";
                    String update = "UPDATE";
                    if( objeto.get("tipo").equals("mensaje")){
                        queryStr = "SELECT * FROM `mensaje` " +
                                "WHERE (`id_remitente` = "  + id +" AND `id_receptor` = " + objeto.get("id_amigo") + ") OR" + 
                                "(`id_remitente` = " + objeto.get("id_amigo") + " AND `id_receptor` = " + id + ") ORDER BY `fecha` ASC";
                        update += " `mensaje`";
                    }else{
                        queryStr = "SELECT * FROM `notificacion`" +
                                "WHERE `id_receptor` = " + id;
                        update += " `notificacion`";
                    }
                        update += " set leido = 1 WHERE `id_receptor` = " + id +" AND `id_remitente` = " + objeto.get("id_amigo") + " AND leido = 0";
                        cmdBD.execute(update);
                    try {
                        ResultSet response = cmdBD.executeQuery( queryStr );  
                        ResultSetMetaData resultMD = response.getMetaData();
                        JSONArray arregloResponse = new JSONArray();
                        while( response.next()){
                            JSONObject tupla = new JSONObject();
                            for (int i = 1; i <= resultMD.getColumnCount(); i++) {
                                tupla.put( resultMD.getColumnName(i), response.getObject(i).toString() );
                            }
                            arregloResponse.add( tupla );
                        }
                        System.out.println(arregloResponse);
                        responder( true, arregloResponse );
//                        return arregloResponse;
                    } catch (Exception e) {
                        System.out.println(queryStr);
                        e.printStackTrace();
                        System.out.println("Error al leer ( Server )");
                    }
//                    responder( true, response.toJSONString() );
                }else if(jsonQuery.get("endpoint").equals("comentarProyecto") ){
                    String insertar = "INSERT INTO `comentario_proyecto`(`id_proyecto`, `id_usuario`,`comentario`) VALUES( ";
                    insertar += objeto.get("id_proyecto").toString() + ", ";
                    insertar += id + ", '" + objeto.get("comentario").toString() + "')";
                    cmdBD.execute(insertar);
                }else if(jsonQuery.get("endpoint").equals("listarComentarios") ){
                    String queryStr = "SELECT CP.`id`, U.`user_name`, CP.`comentario`" + 
                            "FROM `comentario_proyecto` AS CP" + 
                            " INNER JOIN `usuario` AS U ON U.`id` = CP.`id_usuario` " + 
                            "WHERE CP.`id_proyecto` = " + objeto.get("id_proyecto").toString();
                    try {
                        ResultSet response = cmdBD.executeQuery( queryStr );  
                        ResultSetMetaData resultMD = response.getMetaData();
                        JSONArray arregloResponse = new JSONArray();
                        while( response.next()){
                            JSONObject tupla = new JSONObject();
                            for (int i = 1; i <= resultMD.getColumnCount(); i++) {
                                tupla.put( resultMD.getColumnName(i), response.getObject(i).toString() );
                            }
                            arregloResponse.add( tupla );
                        }
                        System.out.println(arregloResponse);
                        responder( true, arregloResponse );
//                        return arregloResponse;
                    } catch (Exception e) {
                        System.out.println(queryStr);
                        e.printStackTrace();
                        System.out.println("Error al leer ( Server )");
                    }
                }else if(jsonQuery.get("endpoint").equals("calificarProyecto") ){
                    String insertar = "INSERT INTO `calificacion_proyecto`(`id_proyecto`, `id_usuario`,`valor`) VALUES( ";
                    insertar += objeto.get("id_proyecto").toString() + ", ";
                    insertar += id + ", " + objeto.get("valor").toString() + ")";
                    System.out.println(insertar);
                    cmdBD.execute(insertar);
                    
                }else if(jsonQuery.get("endpoint").equals("obtenerCalificacion") ){
                    String queryStr = "SELECT `valor` FROM `calificacion_proyecto` WHERE `id_usuario` = " + id;
                    queryStr += " AND `id_proyecto` = " + objeto.get("id_proyecto");
                    JSONObject tupla = new JSONObject();
                    ResultSet response = cmdBD.executeQuery(queryStr);
                    ResultSetMetaData resultMD = response.getMetaData();
                    
                    while( response.next()){
                        for (int i = 1; i <= resultMD.getColumnCount(); i++) {
                            tupla.put( resultMD.getColumnName(i), response.getObject(i) );
                        }
                    }
                    responder( true, tupla );
                }else if(jsonQuery.get("endpoint").equals("hacerFork") ){
                    String insertar = "INSERT INTO `copia_fork`(`nombre`, `id_usuario`,`id_usuario_original`,`id_proyecto`,`archivo`) VALUES( ";
                    insertar += "'" + objeto.get("nombre").toString() + "(FORK FROM " + objeto.get("user_name") + ")', " + id + ", ";
                    insertar += objeto.get("id_usuario") + ", ";
                    insertar += objeto.get("id_proyecto") + ", '";
                    insertar += objeto.get("archivo") + "')";
//                    insertar += objeto.get("archivo_original") + "') ";
                    System.out.println(insertar);
                    cmdBD.execute(insertar);
                }else if(jsonQuery.get("endpoint").equals("crearGrupo") ){
                    String insertar = "INSERT INTO `grupo`(`id_administrador`,`nombre`, `usuarios` ) VALUES( " +
                        id + ", '" + objeto.get("name") + "', '" + objeto.get("usuarios")+ "')";
                    cmdBD.execute(insertar);
                }

            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        }catch( Exception e ){
                
        }
    }
    public void getEndpoint(String endpoint){
        if( endpoint.equals("createUser") ){
            
        }
    }

    public JSONObject getDatos() {
        return datos;
    }

    public void setDatos(JSONObject datos) {
        this.datos = datos;
    }

    @Override
    public String toString() {
        return "CLIENTE{" + "datos=" + datos + '}';
    }
    
    public void responder(boolean success, Object respuesta) {
        try {
            if( !(respuesta instanceof String) ){
                if( respuesta instanceof JSONObject ){
                    respuesta = ((JSONObject)respuesta).toJSONString();
                }else{
                    respuesta = respuesta.toString();
                }
            }
            JSONObject jsonRespuesta = new JSONObject();
            jsonRespuesta.put("success", success);
            jsonRespuesta.put("respuesta", respuesta);
            jsonRespuesta.put("type", "endpoint");
            System.out.println(jsonRespuesta);
            DataOutputStream SALIDA = new DataOutputStream(miSocket.getOutputStream());
            SALIDA.writeUTF( jsonRespuesta.toJSONString() );

        } catch (Exception e) {
        }
    }
    
    public void enviarMensaje( int indexAmigo, String mensaje ){
        System.out.println("ENVIAR " + mensaje + " a " + SERVER_LA_RED.clientes.get(indexAmigo).getIdCliente() );
        SERVER_LA_RED.clientes.get(indexAmigo).recibirMensaje(id, mensaje);
    }
    public void recibirMensaje( int id_amigo, String mensaje ){
        try {
            System.out.println("RECIBI DE " + id + " mensaje " + mensaje );
            JSONObject jsonRespuesta = new JSONObject();
            JSONObject respuesta = new JSONObject();
            respuesta.put("idAmigo", id_amigo);
            respuesta.put("mensaje", mensaje);
            jsonRespuesta.put("success", true);
            jsonRespuesta.put("respuesta", respuesta);
            jsonRespuesta.put("type", "mensaje");
            DataOutputStream SALIDA = new DataOutputStream(miSocket.getOutputStream());
            SALIDA.writeUTF( jsonRespuesta.toJSONString() );        
        } catch (Exception e) {
            System.out.println("ERROR AL MENSAJEAR");
            e.printStackTrace();
        }
    }
}
