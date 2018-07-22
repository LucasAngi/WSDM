package core;

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

/**
 *
 * @author Angeli
 */
public class Predictor {

    public final static String NUMERIC_TYPE = "numeric";
    public final static String NOMINAL_TYPE = "nominal";
    public final static String TREE = "tree";
    public final static String BAYES = "bayes";
    public final static String RULES = "rules";
    public final static String LAZY = "lazy";

    private Classifier classifier = null;
    private String classificationMethod = "";
    private String classificationType = "";
    private String className = "";
    private String accuracy = "";

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

    private void accuracy(Instances data) throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(this.classifier, data);
        this.accuracy = (eval.correct() / data.numInstances()) * 100 + "%";
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private void setClassIndex(Instances data) throws Exception {

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

        if (!find) {
            throw new Exception("Invalid attribute class");
        }

    }

    public void train(Instances data) throws Exception {

        if (this.className == null || this.className.isEmpty()) {
            throw new Exception("Empty attribute class");
        } else {
            this.setClassIndex(data);
        }

        this.classifier.buildClassifier(data);

        if (this.classificationType.equals(NOMINAL_TYPE)) {
            this.accuracy(data);
        }

    }

    public void classify(Instances data) throws Exception {
        this.setClassIndex(data);

        for (Instance obj : data) {
            obj.setMissing(obj.classIndex());
            obj.setMissing(obj.classIndex());
            obj.setClassValue(this.classifier.classifyInstance(obj));
        }

    }

    public String getClassName() {
        return this.className;
    }

    public String getClassifierMethod() {

        if (this.classifier == null) {
            return "undefined";
        }

        return this.classificationMethod;
    }

    public String getAccuracy() {
        return this.accuracy;
    }

}
