package wsdm;

import converters.Responses;
import converters.Requests;
import java.io.IOException;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import vendor.App;
import core.Associator;
import org.json.JSONObject;
import weka.core.Instances;

/**
 *
 * @author Angeli
 */
@Path("/association")
public class Association {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response post(
            @FormParam("data") String json,
            @FormParam("support") double support,
            @FormParam("confidence") double confidence
    ) throws IOException, Exception {

        App app = new App();
        Requests request = new Requests();
        Responses response = new Responses();
        Associator associator = new Associator();

        ArrayList<String> fields = new ArrayList();
        fields.add(json);

        if (!app.verifyParams(fields)) {
            return app.paramInvalid();
        }

        Instances data = request.convertToInstances(json, Requests.ARRAY_CONVERTION ) ;
        
        associator.setConfidence(confidence);
        associator.setSupport(support);
        
        associator.findRules(data);
        
        JSONObject result = new JSONObject(); 
        result.put( "status", 0 ) ;
        result.put( "numRules" , associator.getNumRules( ) ) ;
        result.put( "data", response.toJSON( associator.getRules() ) ) ;
        
        return app.response( result ) ;

    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response get() throws IOException, Exception {
        App app = new App();
        return app.error("The requires to this Web Service support only POST method");
    }
}
