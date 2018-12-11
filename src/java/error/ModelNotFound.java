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
public class ModelNotFound  extends Exception{
    private String key  ;
    
    public ModelNotFound( String key ){
        this.key = key ;
    }
    
    @Override
    public String getMessage(){
        return "Model "+ key +" not found." ;
    }
    
}
