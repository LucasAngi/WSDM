/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package error;

/**
 *
 * @author lucas
 */
public class ConvertionError extends Exception {
       
    private String msg;
    public ConvertionError( String msg ){
        this.msg = msg ; 
    }
    
    @Override
    public String getMessage( ){
        return "Error on "+msg+" convertion." ;
    }
}
