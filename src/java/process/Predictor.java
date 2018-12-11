package process;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.M5Rules;
import weka.classifiers.rules.OneR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import java.util.UUID;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import error.ModelNotFound;
import error.ClassNotFound;
import error.ConvertionError;

/**
 *
 * @author Angeli
 */
public class Predictor {

    public final static String NUMERIC_TYPE = "numeric";
    public final static String NOMINAL_TYPE = "nominal";
    public final static String TREE  = "tree";
    public final static String BAYES = "bayes";
    public final static String RULES = "rules";
    public final static String LAZY  = "lazy";

    private Classifier classifier       = null;
    private String classificationMethod = "undefined";
    private String classificationType   = "undefined";
    private double rootMeanSquaredError ;
    private double meanAbsoluteError ;
    private double rootRelativeSquaredError ;
    private String className  = "" ;
    private String accuracy   = "" ;
    private String modelsPath = System.getProperty("user.dir")+"/models/";
    private boolean isLoaded  = false ;
    private Instances header  = null ;

    public Predictor(String type, String method) {
        if (type.equals(NUMERIC_TYPE)) {
            this.classificationType = NUMERIC_TYPE;
            switch (method) {
                case "tree": {
                    this.classifier = new M5P();
                    this.classificationMethod = method;
                }
                break;
                case "rules": {
                    this.classifier = new M5Rules();
                    this.classificationMethod = method;
                }
                break;
                case "lazy": {
                    this.classifier = new IBk();
                    this.classificationMethod = method;
                }
                break;
                default: {
                    this.classifier = new M5P();
                    this.classificationMethod = "tree";
                }
            }
        } else {
            if (type.equals(NOMINAL_TYPE)) {
                this.classificationType = NOMINAL_TYPE;
                switch (method) {
                    case "tree": {
                        this.classifier = new J48();
                        this.classificationMethod = method;
                    }
                    break;
                    case "rules": {
                        this.classifier = new OneR();
                        this.classificationMethod = method;
                    }
                    break;
                    case "bayes": {
                        this.classifier = new NaiveBayes();
                        this.classificationMethod = method;
                    }
                    break;
                    case "lazy": {
                        this.classifier = new IBk();
                        this.classificationMethod = method;
                    }
                    break;
                    default: {
                        this.classifier = new J48();
                        this.classificationMethod = "tree";
                    }
                }
            }
        }
    }
    
    public Predictor(String type, String method , String key) throws Exception {
        try{
            this.header = this.getInfoHeader(key) ;
            this.classificationMethod = method;
            this.isLoaded = true;
            if (type.equals(NUMERIC_TYPE)) {
            this.classificationType = NUMERIC_TYPE;
            switch (method) {
                case "tree": {
                    this.classifier = (M5P) weka.core.SerializationHelper.read( this.modelsPath+key+".model" ) ;
                }
                break;
                case "rules": {
                    this.classifier = (M5Rules) weka.core.SerializationHelper.read( this.modelsPath+key+".model" );
                }
                break;
                case "lazy": {
                    this.classifier = (IBk) weka.core.SerializationHelper.read( this.modelsPath+key+".model" );
                }
                break;
            }
            } else {
                if (type.equals(NOMINAL_TYPE)) {
                    this.classificationType = NOMINAL_TYPE;
                    switch (method) {
                        case "tree": {
                            this.classifier = (J48) weka.core.SerializationHelper.read( this.modelsPath+key+".model" ) ;
                        }
                        break;
                        case "rules": {
                            this.classifier = (OneR) weka.core.SerializationHelper.read( this.modelsPath+key+".model" );
                        }
                        break;
                        case "bayes": {
                            this.classifier = (NaiveBayes) weka.core.SerializationHelper.read( this.modelsPath+key+".model" );;
                        }
                        break;
                        case "lazy": {
                            this.classifier = (IBk) weka.core.SerializationHelper.read( this.modelsPath+key+".model" );
                        }
                        break;

                    }
                }
            }
        }catch( Exception e ){
            throw new ModelNotFound( key ) ;
        } 
        
    }
    
    
    private void statistics(Instances data) throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(this.classifier, data, 5, new Random(1));
        if( this.classificationType.equals( this.NOMINAL_TYPE ) ){
            this.accuracy = Double.toString(eval.pctCorrect())+"%";
        } else{
            this.rootMeanSquaredError = eval.rootMeanSquaredError();
            this.meanAbsoluteError = eval.meanAbsoluteError() ;
            this.rootRelativeSquaredError = eval.rootRelativeSquaredError() ;            
        }
        
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private void setClassIndex(Instances data) throws Exception {
        
        if( data == null ){
            throw  new ConvertionError( "input" ) ;
        }

        Instance obj = data.get(0);
        int cont = 0;
        boolean find = false;

        while (!find && cont < obj.numAttributes()) {
            Attribute att = obj.attribute(cont);
            if (att.name().equals(this.className)) {
                data.setClassIndex(cont);
                find = true;
            }
            cont++;
        }
        
        if( !find ){
           throw new ClassNotFound( this.className );
        }

    }

    public void train(Instances data) throws Exception {
       
        this.setClassIndex(data);        

        this.classifier.buildClassifier(data);

        this.statistics(data);
    
        this.saveInfoHeader( data ) ;
    }

    public void classify(Instances data) throws Exception {
        
        this.setClassIndex(data);
                        
        for (Instance obj : data) {
            obj.setMissing(obj.classIndex());
            obj.setClassValue(this.classifier.classifyInstance(obj));
        }
        
    }
    
    public boolean isLoaded(){
        return this.isLoaded;
    }

    public String getClassName() {
        return this.className;
    }

    public String getClassifierMethod() {
        return this.classificationMethod;
    }

    public String getAccuracy() {
        return this.accuracy;
    }
    
    public double getRootMeanSquaredError() {
        return this.rootMeanSquaredError;
    }
    
    public double getMeanAbsoluteError() {
        return this.meanAbsoluteError;
    }
    
    public double getRootRelativeSquaredError(){
        return this.rootRelativeSquaredError;
    }
    
    private void saveInfoHeader( Instances data ){
        this.header = new Instances( data , 0 , 0 ) ;
    }
    
    private Instances getInfoHeader( String key ) throws IOException{
        ArffLoader loader = new ArffLoader(  );
        loader.setFile( new File( this.modelsPath+key+".arff") );
        return loader.getDataSet();
    }
    
    public Instances mergeHeaderInfo( Instances data ) throws Exception{
        this.setClassIndex(data);
        data.replaceAttributeAt(this.header.attribute( data.classAttribute().name() ), data.classIndex() ) ;
        return data;
    }
    
    public Instances mergeHeaderInfo( Instances model , Instances predict ) throws Exception{
        this.setClassIndex(predict);
        predict.replaceAttributeAt(model.attribute( predict.classAttribute().name() ), predict.classIndex() ) ;
        return predict;
    }
    
    private String generateKey(){
        return UUID.randomUUID().toString();
    }
    
    public String saveModel() throws Exception{
        String key = this.generateKey() ;
        
        File dir = new File( this.modelsPath ); 
        
        if (!dir.exists()) {
            dir.mkdirs(); 
        } else {
            weka.core.SerializationHelper.write( this.modelsPath+key+".model", this.classifier ) ;
            ArffSaver saver = new ArffSaver();
            saver.setInstances( this.header);
            saver.setFile(new File(this.modelsPath+key+".arff"));
            saver.writeBatch();
        }
        
        return key ;
    }
    
}
