/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CORE;

import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author SAMSUNG
 */
public class UTIL {
    private static JSONParser JSON = new JSONParser();
    public static JSONArray toJSONArray( JSONObject json ){
        JSONArray nArray = new JSONArray();
       
        try {
            for (Object key : json.keySet()) {
                
            }
            nArray = (JSONArray)JSON.parse( json.toJSONString() );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR AL CASTER JSONARRAY");
        }
        return nArray;
    }
}
