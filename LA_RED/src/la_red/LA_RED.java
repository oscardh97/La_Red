/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package la_red;

import java.io.DataInputStream;
import java.net.Socket;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * 
 * @author SAMSUNG
 */
public class LA_RED extends Thread{
    public static Socket globalSocket;
    public static USUARIO usuario;
    private static JSONParser JSON = new JSONParser();
    public static Object responseJSON;

    public LA_RED() {
        start();
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            globalSocket = new Socket("localhost", 40);
            new LA_RED();
//            new CODE.PROJECTS().show();
            new LOGIN.REGISTRO(null, true).show();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con el servidor, contacte a su administrador\n"+
                    "Ing. Oscar Diaz\nCel: 9999-9999", "ERORR", JOptionPane.ERROR_MESSAGE);
            System.err.println("error socket");
        }
//        new LOGIN.CREATE_USER(null, true).show();
    }
    @Override
    public void run(){
        while( globalSocket.isConnected() ){
            try{
                DataInputStream ENTRADA = new DataInputStream(globalSocket.getInputStream());
                String response = ENTRADA.readUTF();
                System.out.println("----------------------------------------------------------");
                responseJSON = JSON.parse( ((JSONObject)JSON.parse( response)).get("respuesta").toString() );
                if( ((JSONObject)JSON.parse( response)).get("success").toString().equals("false") ){
                    JOptionPane.showMessageDialog(null, ((JSONObject)responseJSON).get("mensaje") , "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                System.out.println(responseJSON);
                System.out.println(response + "<------");
            }catch( Exception e){
            }
        }
    }
    
}
