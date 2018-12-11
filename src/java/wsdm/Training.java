/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wsdm;

import converters.Requests;
import error.ClassNotFound;
import error.ConvertionError;
import process.Predictor;
import java.io.IOException;
import java.util.ArrayList;
import javax.ws.rs.POST;
import javax.ws.rs.FormParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import vendor.App;
import weka.core.Instances;

/**
 *
 * @author lucasangi
 */
@Path("/training")
public class Training {
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response trainPost (
            @FormParam("training") String training,
            @FormParam("class")    String className,
            @FormParam("type")     String type,
            @FormParam("function") String function
    ) throws IOException, Exception {
        
        App app = new App();
        Requests  request = new Requests();
        ArrayList<String> fields = new ArrayList();

        fields.add(training);
        fields.add(type);
        fields.add(className);
        fields.add(function);

        try {
            if (!app.verifyParams(fields)) {
                return app.paramInvalid();
            }
        } catch (Exception ex) {
            return app.paramInvalid();
        }
        
        Predictor predictor = null;
        
        if( function.equals( App.CLASSIFICATION ) ){
            predictor = new Predictor(Predictor.NOMINAL_TYPE, type);
        } else if ( function.equals( App.REGRESSION ) ) {
            predictor = new Predictor(Predictor.NUMERIC_TYPE, type);
        }
        
        try{
            Instances train = request.convertToInstances( training, Requests.OBJECT_CONVERTION ) ;
        
            predictor.setClassName(className);
            predictor.train( train )  ;
        }catch( ClassNotFound e ){
            return app.error( e.getMessage() ) ;
        }catch( ConvertionError e ){
            return app.error( e.getMessage() ) ;
        }
        
        JSONObject result = new JSONObject();
        result.put("status", 0);
        result.put("modelKey", predictor.saveModel() ) ;
        
        if( function.equals( App.CLASSIFICATION ) ){
            result.put("accuracy", predictor.getAccuracy());
        } else if ( function.equals( App.REGRESSION ) ) {
            result.put("mae", predictor.getMeanAbsoluteError());
        }
        
        return app.response(result);
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response get() throws IOException, Exception {

        App app = new App();
        return app.error("The requires to this Web Service support only POST method");

    }
}
