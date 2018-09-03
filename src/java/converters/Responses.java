package converters;

import org.json.JSONArray;
import org.json.JSONObject;
import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.Item;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Angeli
 */
public class Responses {

    public JSONArray toJSON( Instances data ) {

        JSONArray array = new JSONArray();
        JSONObject obj;
        for (Instance inst : data) {
            obj = new JSONObject();

            for (int cont = 0; cont < inst.numAttributes(); cont++) {
                Attribute att = inst.attribute(cont);
                if (att.isNominal()) {
                    obj.put(att.name(), att.value((int) inst.value(cont)));
                } else {
                    obj.put(att.name(), inst.value(cont));
                }
            }
            array.put(obj);
        }

        return array;
    }

    public JSONObject toJSON(Instance data) {

        JSONObject obj;

        obj = new JSONObject();

        for (int cont = 0; cont < data.numAttributes(); cont++) {
            Attribute att = data.attribute(cont);
            if (att.isNominal()) {
                obj.put(att.name(), att.value((int) data.value(cont)));
            } else {
                obj.put(att.name(), data.value(cont));
            }
        }

        return obj;
    }
    
    public JSONArray toJSON( AssociationRules rules ){
        
        JSONArray result = new JSONArray( ) ;
        JSONArray consequence ;
        JSONArray premise ;
        JSONObject objRule;
        
        
        for( AssociationRule rule : rules.getRules() ){
            
            objRule = new JSONObject() ;
            
            consequence = new JSONArray( ) ;
            for( Item item : rule.getConsequence() ){
                consequence.put( item.toString().replace("[", "").replace("=t","").replace("]", "") );
            }
            
            objRule.put( "consequence", consequence ) ;
            
            premise = new JSONArray() ;
            for( Item item : rule.getPremise() ){
                premise.put( item.toString().replace("[", "").replace("=t","").replace("]", "") );
            }
            
            objRule.put( "premise", premise ) ;
            
            result.put( objRule ) ;
        }
        
        return result;
    }

}
