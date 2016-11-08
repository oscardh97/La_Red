

package server_la_red;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import org.json.simple.parser.JSONParser;
import static server_la_red.SERVER_LA_RED.Server;

/**
 *
 * @author SAMSUNG
 */
public class SERVER_LA_RED {
    public static Connection conexionBD = null;
    public static Statement cmdBD = null;
    public static ServerSocket Server;
    public static Socket Cliente;
    public static JSONParser JSON = new JSONParser();
    public static ArrayList< CLIENTE > clientes = new ArrayList();
    public static void main(String[] args) {
        try {
            Server = new ServerSocket(40);
            System.out.println("Iniciando...");
            
            try {
                Class.forName( "com.mysql.jdbc.Driver" );
                conexionBD = DriverManager.getConnection ("jdbc:mysql://127.0.0.1/LA_RED","root", "");
                cmdBD = conexionBD.createStatement();
                System.out.println("Se ha logrado la conexion con la BD");
            
                    while(true){
                        Cliente = Server.accept();
                        CLIENTE nCliente = new CLIENTE( Cliente );
                        nCliente.start();
                        clientes.add( nCliente );
                        System.out.println(clientes);
                    }
            } catch (Exception e) {
                System.out.println("No se ha logrado la conexion con la BD");
            }
//            DataInputStream ENTRADA = new DataInputStream(Cliente.getInputStream());
            
            
//            DataOutputStream SALIDA = new DataOutputStream(Cliente.getOutputStream());
//            
//            SALIDA.writeUTF("Bienvenido a mi servidor mandame tu opcion");
//            System.out.println(ENTRADA.readUTF());
//            try {
//                while( ! Cliente.isClosed() ){
//                    JSONObject data = (JSONObject)(JSON.parse( ENTRADA.readUTF() ) );  
//                    switch( data.get("type").toString() ){
//                        case "READ":
//                            read( data.get("query").toString() );
//                            break;
//                        case "CREATE":
//                            create( data.get("query").toString());
//                            break;
//                    }
//                }   
//            } catch (Exception e) {
//                System.out.println("Error al leer la data");
//            }   
            
        } catch (IOException ex) {
        }
    }
    
    
}
