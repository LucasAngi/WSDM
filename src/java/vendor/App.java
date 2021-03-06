package vendor;

import java.util.ArrayList;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import weka.core.json.JSONNode;

public class App {
    
    public final static String CLASSIFICATION = "classification";
    public final static String REGRESSION     = "regression";

    public boolean verifyParams(ArrayList<String> fields) {
        if (!fields.stream().noneMatch((field) -> (field.isEmpty()))) {
            return false;
        }
        return true;
    }
    
    public boolean verifyParam( String field) {
        if ( field == null || field.isEmpty() ) {
            return false;
        }
        return true;
    }

    public Response debug(String msg) {
        return Response.ok(msg, MediaType.TEXT_PLAIN).build();
    }

    public Response debugJSON(JSONNode json) {
        StringBuffer sb = new StringBuffer();

        json.toString(sb);
        return Response.ok(sb.toString(), MediaType.APPLICATION_JSON).build();
    }

    public Response paramInvalid() {
        JSONObject response = new JSONObject();
        response.put("message", "Mandatory parameters not reported");
        response.put("status", -1);
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

    public Response error(String msg) {
        JSONObject response = new JSONObject();
        response.put("message", msg);
        response.put("status", -1);
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

  

    public Response response(JSONObject response) {
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

    public Response response(JSONArray response) {
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

}
