package wsdm;

import converters.Responses;
import converters.Requests;
import core.Predictor;
import vendor.App;
import java.io.IOException;
import java.util.ArrayList;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import weka.core.Instances;

/**
 *
 * @author Angeli
 */
@Path("/classification")
public class Classification {

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(
            @FormParam("training") String training,
            @FormParam("test") String test,
            @FormParam("class") String className,
            @FormParam("type") String type
    ) throws IOException, Exception {

        App app = new App();
        Requests request = new Requests();
        Responses response = new Responses();
        Predictor predictor = new Predictor(Predictor.NOMINAL_TYPE, type);

        ArrayList<String> fields = new ArrayList();

        fields.add(training);
        fields.add(test);
        fields.add(className);

        try {
            if (!app.vertifyParams(fields)) {
                return app.paramInvalid();
            }
        } catch (Exception ex) {
            return app.paramInvalid();
        }

        ArrayList<String> jsons = new ArrayList<String>();

        jsons.add(training);
        jsons.add(test);

        ArrayList<Instances> datas = request.convertToInstances(jsons);

        Instances model = datas.get(0);
        Instances predict = datas.get(1);

        predictor.setClassName(className);

        predictor.train(model);

        predictor.classify(predict);

        JSONArray data = response.toJSON(predict);

        JSONObject result = new JSONObject();
        result.put("status", 0);
        result.put("type", predictor.getClassifierMethod());
        result.put("data", data);
        result.put("accuracy", predictor.getAccuracy());

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
