package core;

import weka.associations.Apriori;
import weka.associations.AssociationRules;
import weka.core.Instances;

/**
 *
 * @author Angeli
 */
public class Associator {

    private Apriori associator = null;
    
    public Associator() {
        this.associator = new Apriori();
    }

    public int getNumRules() {
        return this.associator.getNumRules();
    }

    public void setConfidence(double confidence) {
        this.associator.setMinMetric(confidence);
    }

    public void setSupport(double support) {
        this.associator.setLowerBoundMinSupport(support);
    }

    public void findRules(Instances data) throws Exception {
        this.associator.buildAssociations(data); 
    }
    
    public AssociationRules getRules(){
        return this.associator.getAssociationRules( ) ;
    }
    
    @Override
    public String toString( ){
        return  this.associator.toString() ;
    }



}
