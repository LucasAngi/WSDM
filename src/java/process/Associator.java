package process;

import error.FloatParam;
import weka.associations.Apriori;
import weka.associations.AssociationRules;
import weka.core.Instances;
import error.NegativeParam;

/**
 *
 * @author Angeli
 */
public class Associator {

    private Apriori associator = null;
    
    public Associator() {
        this.associator = new Apriori();
        this.associator.setNumRules( 50 );
        System.out.println( this.associator.getMetricType() );
    }

    public int getNumRules() {
        return this.associator.getAssociationRules( ).getNumRules();
    }

    public void setConfidence(String confidence) throws NegativeParam, FloatParam {
        try{
            this.associator.setMinMetric( Double.parseDouble(confidence)) ;
            
            if( this.associator.getMinMetric() <= 0 ){
                throw new NegativeParam( "confidence" ) ;
            }
        }catch( Exception e){
            throw new FloatParam( "confidence" ) ;
        }
        
        
    }

    public void setSupport(String support) throws NegativeParam, FloatParam {
        try{
            this.associator.setLowerBoundMinSupport( Double.parseDouble(support)) ;
            
            if( this.associator.getLowerBoundMinSupport() <= 0 ){
                throw new NegativeParam( "support" ) ;
            }
        }catch( Exception e){
            throw new FloatParam( "support" ) ;
        }
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
