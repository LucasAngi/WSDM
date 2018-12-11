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
public class NegativeParam extends Exception{
    private String param;
    
    public NegativeParam( String param ){
        this.param = param ;
    }
    
    @Override
    public String getMessage( ){
        return "The "+this.param+" must be greater than 0." ;
    }
}
