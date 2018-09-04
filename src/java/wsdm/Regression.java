package wsdm;

import converters.Responses;
import converters.Requests;
import core.Predictor;
import vendor.App;
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
import org.json.JSONArray;
import org.json.JSONObject;
import weka.core.Instances;

/**
 *
 * @author Angeli
 */
@Path("/regression")
public class Regression {

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response post(
        @FormParam("training") String training,
        @FormParam("test") String test,
        @FormParam("class") String className,
        @FormParam("type") String type,
        @FormParam("modelKey") String modelKey
    ) throws IOException, Exception {

        App app = new App();
        Requests request = new Requests();
        Responses response = new Responses();

        ArrayList<String> fields = new ArrayList();

        fields.add(test);
        fields.add(className);
        fields.add(type);

        try {
            if (!app.verifyParams(fields) || ( !app.verifyParam( modelKey ) && !app.verifyParam( training ) ) ) {
                return app.paramInvalid();
            }
        } catch (Exception ex) {
            return app.paramInvalid();
        }

        Predictor predictor  = null ;
        Instances model      = null ;
        Instances predict    = null ;  
        
        if( !app.verifyParam( training ) ){
            predictor = new Predictor(Predictor.NUMERIC_TYPE, type, modelKey);
            predictor.setClassName( className ) ;
            
            predict = request.convertToInstances( test, Requests.OBJECT_CONVERTION ) ;
            predict = predictor.mergeHeaderInfo( predict ) ;
        } else {
            predictor = new Predictor(Predictor.NUMERIC_TYPE, type );
            predictor.setClassName(className);
            
            model   = request.convertToInstances( training, Requests.OBJECT_CONVERTION ) ;
            predict = request.convertToInstances( test,     Requests.OBJECT_CONVERTION ) ;
            
            predict = predictor.mergeHeaderInfo( model , predict ) ;
            
            predictor.train(model);
        }

        predictor.classify(predict);

        JSONArray data = response.toJSON(predict);

        JSONObject result = new JSONObject();
        result.put("status", 0);
        result.put("type", predictor.getClassifierMethod());
        result.put("data", data);
        
        if( !predictor.isLoaded() ){
            result.put("Root Mean Squared Error", predictor.getRootMeanSquaredError());
            result.put("Root Relative Squared Error", predictor.getRootRelativeSquaredError());
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
