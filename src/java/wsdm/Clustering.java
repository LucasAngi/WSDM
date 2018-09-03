
package wsdm;

import converters.Responses;
import converters.Requests;
import core.Cluster;
import vendor.App;
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
import org.json.JSONArray;
import org.json.JSONObject;
import weka.core.Instances;

/**
 *
 * @author Angeli
 */

@Path("/clustering")
public class Clustering {
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response post( 
            @FormParam("data") String json,
            @FormParam("numGroups") int numGroups  
    ) throws IOException, Exception {

        App app = new App();
        Requests request  = new Requests();
        Responses   response = new Responses();
        Cluster cluster = new Cluster( ) ;

        ArrayList<String> fields = new ArrayList( );
        fields.add( json ) ;
        
        if( !app.verifyParams( fields ) ){
            return app.paramInvalid( ) ;
        }
        
        Instances data = request.convertToInstances(json , Requests.OBJECT_CONVERTION ) ;
        
        cluster.setNumGroups( numGroups ) ;
        cluster.setDataset( data ) ;
        
        cluster.genereateGroups( ) ;        
        cluster.clusterInstances( ) ; 
        
        JSONObject result = new JSONObject() ;
        result.put( "status", 0 ) ;
        result.put( "number of groups", cluster.getNumGroups() ) ;
        
        JSONArray groups = new JSONArray() ;
        JSONObject group ;
        
        for( int cont = 0 ; cont < cluster.getNumGroups() ; cont ++ ){
            group = new JSONObject() ;
            group.put( "centroid", response.toJSON( cluster.getCentroid(cont) ) ) ;
            group.put( "stdDev",   response.toJSON( cluster.getSTDDev(cont) ) ) ;
            
            groups.put(group) ;
        }
        
        result.put( "groups" , groups ) ;
        
        result.put( "data" , response.toJSON(data) ) ;

        return app.response( result ) ; 
        
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response get( ) throws IOException, Exception {
        
        App app = new App( ) ;
        return app.error( "The requires to this Web Service support only POST method" ) ;
        
    }
}
