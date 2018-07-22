package converters;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import weka.core.Instances;
import weka.core.json.JSONInstances;
import weka.core.json.JSONNode;

/**
 *
 * @author Angeli
 */
public class Requests {
    private static final String NUMERIC_TYPE  = "numeric";
    private static final String NOMINAL_TYPE  = "nominal";
    private static final String MISSING_VALUE = "?" ;
    private static final int    NUM_HEADERS   = 2 ;
    public  static final String OBJECT_CONVERTION = "object" ;
    public  static final String ARRAY_CONVERTION  = "array" ;
    
    private String isArrayJSON( String json ) {

        int isArray;

        try {
            JSONArray jArray = new JSONArray(json);
            isArray = 1;
        } catch (Exception e) {
            isArray = 0;
        }

        if (isArray == 0) {
            json = "[" + json + "]";
        }

        return json;
    }
   

    private String appendJSON(String jsonString1, String jsonString2) {

        jsonString1 = this.isArrayJSON(jsonString1);
        jsonString2 = this.isArrayJSON(jsonString2);

        JSONArray json1 = new JSONArray(jsonString1);
        JSONArray json2 = new JSONArray(jsonString2);

        for (int contObj = 0; contObj < json2.length(); contObj++) {
            json1.put(json2.getJSONObject(contObj));
        }

        return json1.toString();

    }

    private String defineType(HashMap<String, Integer> count) {
        if (count.get(NOMINAL_TYPE) > count.get(NUMERIC_TYPE)) {
            return NOMINAL_TYPE;
        } else {
            return NUMERIC_TYPE;
        }
    }
    
    public ArrayList<JSONNode> buildHeader(String jsonString1, String jsonString2) {

        ArrayList<JSONNode> nodes = new ArrayList();

        HashMap<String, ArrayList<String>> mapLabel = new HashMap();
        HashMap<String, HashMap<String, Integer>> mapType = new HashMap();

        ArrayList<String> listLabel;
        HashMap<String, Integer> map;

        String type;

        String nominalValue = "";
        double doubleValue = 0.0;

        JSONArray json = new JSONArray(this.appendJSON(jsonString1, jsonString2));

        for (int contObj = 0; contObj < json.length(); contObj++) {

            JSONObject obj = json.getJSONObject(contObj);

            for (String name : JSONObject.getNames(obj)) {

                try {
                    nominalValue = (String) obj.getString(name);
                    type = NOMINAL_TYPE;

                } catch (Exception e) {
                    doubleValue = obj.getDouble(name);
                    type = NUMERIC_TYPE;
                }

                String value = (type.equals(NOMINAL_TYPE)) ? nominalValue : Double.toString(doubleValue);

                map = mapType.get(name);

                if (map == null) {
                    map = new HashMap();
                    if (type.equals(NOMINAL_TYPE)) {
                        map.put(NOMINAL_TYPE, 1);
                        map.put(NUMERIC_TYPE, 0);
                    } else {
                        map.put(NOMINAL_TYPE, 0);
                        map.put(NUMERIC_TYPE, 1);
                    }

                    mapType.put(name, map);
                } else {
                    map = mapType.get(name);
                    map.put(type, map.get(type) + 1);
                    mapType.put(name, map);
                }

                if (type.equals(NOMINAL_TYPE)) {
                    listLabel = mapLabel.get(name);

                    if (listLabel == null) {
                        listLabel = new ArrayList();
                        listLabel.add(value);
                        mapLabel.put(name, listLabel);
                        listLabel.clear();
                    } else {
                        listLabel = mapLabel.get(name);

                        if (!listLabel.contains(value) && !value.isEmpty() && !value.equals(MISSING_VALUE)) {
                            listLabel.add(value);
                            mapLabel.put(name, listLabel);
                        }
                    }
                }
            }
        }

        for (int cont = 0; cont < NUM_HEADERS; cont++) {
            JSONNode node = new JSONNode();
            JSONNode result = node.addObject("header");
            result.addPrimitive("relation", "relation");
            JSONNode resultAttributes = result.addArray("attributes");

            for (String name : mapType.keySet()) {
                JSONNode attribute = resultAttributes.addObjectArrayElement();
                attribute.addPrimitive("name", name);
                attribute.addPrimitive("type", this.defineType(mapType.get(name)));
                attribute.addPrimitive("clase", Boolean.FALSE);
                attribute.addPrimitive("weight", 1.0);

                if (this.defineType(mapType.get(name)).equals(NOMINAL_TYPE)) {
                    JSONNode attributeLabel = attribute.addArray("labels");
                    listLabel = mapLabel.get(name);
                    for (String value : listLabel) {
                        attributeLabel.addArrayElement(value);
                    }
                }
            }
            nodes.add(node);
        }

        return nodes;
    }

    public void buildData(String jsonString, JSONNode node) {

        HashMap<Integer, ArrayList<String>> mapValues = new HashMap();
        ArrayList<String> listValues;

        String nominalValue = "";
        double doubleValue = 0.0;

        String type;

        JSONArray json = new JSONArray(this.isArrayJSON(jsonString));

        for (int contObj = 0; contObj < json.length(); contObj++) {

            JSONObject obj = json.getJSONObject(contObj);

            listValues = new ArrayList();

            for (String name : JSONObject.getNames(obj)) {

                try {
                    nominalValue = (String) obj.getString(name);
                    type = NOMINAL_TYPE;

                } catch (Exception e) {
                    doubleValue = obj.getDouble(name);
                    type = NUMERIC_TYPE;
                }

                String value = (type.equals(NOMINAL_TYPE)) ? nominalValue : Double.toString(doubleValue);

                listValues.add(value);

            }

            mapValues.put(contObj, listValues);

        }

        JSONNode result = node.addArray("data");
        JSONNode objValue;
        JSONNode obj;

        for (int i : mapValues.keySet()) {
            listValues = mapValues.get(i);
            obj = new JSONNode();
            obj.addPrimitive("sparse", false);
            obj.addPrimitive("weight", 1.0);
            objValue = obj.addArray("values");
            for (String value : listValues) {
                objValue.addArrayElement(value);
            }
            result.add(obj);
        }

    }
    
    private Instances toInstances(JSONNode node) {
        return JSONInstances.toInstances(node);
    }
    
    private JSONNode objectConversion( String jsonString ) {

        HashMap<String, ArrayList<String>> mapLabel = new HashMap();
        HashMap<String, HashMap<String, Integer>> mapType = new HashMap();
        HashMap<Integer, ArrayList<String>> mapValues = new HashMap();

        ArrayList<String> listLabel;
        HashMap<String, Integer> map;

        ArrayList<String> listValues;

        String type;

        String nominalValue = "";
        double doubleValue = 0.0;

        JSONArray json = new JSONArray(jsonString);

        for (int contObj = 0; contObj < json.length(); contObj++) {

            JSONObject obj = json.getJSONObject(contObj);

            listValues = new ArrayList();

            for (String name : JSONObject.getNames(obj)) {

                try {
                    nominalValue = (String) obj.getString(name);
                    type = NOMINAL_TYPE;

                } catch (Exception e) {
                    doubleValue = obj.getDouble(name);
                    type = NUMERIC_TYPE;
                }

                String value = (type.equals(NOMINAL_TYPE)) ? nominalValue : Double.toString(doubleValue);

                listValues.add(value);
                map = mapType.get(name);

                if (map == null) {
                    map = new HashMap();
                    if (type.equals(NOMINAL_TYPE)) {
                        map.put(NOMINAL_TYPE, 1);
                        map.put(NUMERIC_TYPE, 0);
                    } else {
                        map.put(NOMINAL_TYPE, 0);
                        map.put(NUMERIC_TYPE, 1);
                    }

                    mapType.put(name, map);
                } else {
                    map = mapType.get(name);
                    map.put(type, map.get(type) + 1);
                    mapType.put(name, map);
                }

                if (type.equals(NOMINAL_TYPE)) {
                    listLabel = mapLabel.get(name);

                    if (listLabel == null) {
                        listLabel = new ArrayList();
                        listLabel.add(value);
                        mapLabel.put(name, listLabel);
                        listLabel.clear();
                    } else {
                        listLabel = mapLabel.get(name);

                        if (!listLabel.contains(value) && !value.isEmpty() && !value.equals(MISSING_VALUE)) {
                            listLabel.add(value);
                            mapLabel.put(name, listLabel);
                        }
                    }
                }
            }
            mapValues.put(contObj, listValues);
        }

        JSONNode node = new JSONNode();
        JSONNode header = node.addObject("header");
        header.addPrimitive("relation", "relation");
        JSONNode resultAttributes = header.addArray("attributes");

        for (String name : mapType.keySet()) {
            JSONNode attribute = resultAttributes.addObjectArrayElement();
            attribute.addPrimitive("name", name);
            attribute.addPrimitive("type", this.defineType(mapType.get(name)));
            attribute.addPrimitive("clase", Boolean.FALSE);
            attribute.addPrimitive("weight", 1.0);

            if (this.defineType(mapType.get(name)).equals(NOMINAL_TYPE)) {
                JSONNode attributeLabel = attribute.addArray("labels");
                listLabel = mapLabel.get(name);
                for (String value : listLabel) {
                    attributeLabel.addArrayElement(value);
                }
            }
        }

        JSONNode data = node.addArray("data");
        JSONNode objValue;
        JSONNode obj;

        for (int i : mapValues.keySet()) {
            listValues = mapValues.get(i);
            obj = new JSONNode();
            obj.addPrimitive("sparse", false);
            obj.addPrimitive("weight", 1.0);
            objValue = obj.addArray("values");
            for (String value : listValues) {
                objValue.addArrayElement(value);
            }
            data.add(obj);
        }

        return node;
    }
    
    private JSONNode arrayConversion(String jsonString) {

        HashMap< Integer, ArrayList<String>> mapValue = new HashMap();

        ArrayList<String> attributes = new ArrayList();
        ArrayList<String> values = null;

        JSONArray json = new JSONArray(jsonString);

        String item;

        for (int contList = 0; contList < json.length(); contList++) {
            JSONArray list = json.getJSONArray(contList);
            for (int contItem = 0; contItem < list.length(); contItem++) {
                item = (String) list.get(contItem);

                if (!attributes.contains(item)) {
                    attributes.add(item);
                }
            }
        }

        for (int contList = 0; contList < json.length(); contList++) {

            JSONArray list = json.getJSONArray(contList);
            values = new ArrayList();

            for (int contAtt = 0; contAtt < attributes.size(); contAtt++) {
                item = attributes.get(contAtt);
                if (list.toList().contains(item)) {
                    values.add("t");
                } else {
                    values.add("?");
                }
            }

            mapValue.put(contList, values);
        }

        JSONNode node = new JSONNode();
        JSONNode header = node.addObject("header");
        header.addPrimitive("relation", "relation");
        JSONNode headerAttributes = header.addArray("attributes");

        for (String att : attributes) {
            JSONNode attribute = headerAttributes.addObjectArrayElement();
            attribute.addPrimitive("name", att);
            attribute.addPrimitive("type", this.NOMINAL_TYPE);
            attribute.addPrimitive("clase", Boolean.FALSE);
            attribute.addPrimitive("weight", 1.0);
            JSONNode attributeLabel = attribute.addArray("labels");
            attributeLabel.addArrayElement("t");
            attributeLabel.addArrayElement("?");
        }

        JSONNode data = node.addArray("data");
        JSONNode objValue;
        JSONNode obj;

        for (int i : mapValue.keySet()) {
            values = mapValue.get(i);
            obj = new JSONNode();
            obj.addPrimitive("sparse", false);
            obj.addPrimitive("weight", 1.0);
            objValue = obj.addArray("values");
            for (String value : values) {
                objValue.addArrayElement(value);
            }
            data.add(obj);
        }

        return node;

    }
    
    public Instances convertToInstances( String json , String type ){
        if( type.equals( OBJECT_CONVERTION ) ){
            return this.toInstances( this.objectConversion( json ) ) ;
        } else{
            if( type.equals( ARRAY_CONVERTION ) ){
                return this.toInstances( this.arrayConversion( json ) ) ;
            } else{
                System.err.println("Eita porra");
                return this.toInstances( this.arrayConversion( json ) ) ;
            }
        }
    }
    
    public ArrayList<Instances> convertToInstances( ArrayList<String> jsons ){
        
        ArrayList<JSONNode> jsonNodes = null ; 
        
        jsonNodes = this.buildHeader( jsons.get(0) , jsons.get(1) ) ;
                   
        this.buildData( jsons.get(0), jsonNodes.get( 0 ) ) ; 
        this.buildData( jsons.get(1), jsonNodes.get( 1 ) ) ;        
        
        ArrayList<Instances> result = new ArrayList<Instances>();
        result.add( this.toInstances( jsonNodes.get( 0 ) ) ) ;
        result.add( this.toInstances( jsonNodes.get( 1 ) ) ) ;
        
        return result ;
    }
    
}
