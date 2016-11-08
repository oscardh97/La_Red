package la_red;


import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author SAMSUNG
 */
public class linea implements Serializable{
        public String codigo;
        public String texto;
        private static final long SerialVersionUID = 777L;

        public linea(String codigo, String texto) {
            this.codigo = codigo;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
        
        
    }