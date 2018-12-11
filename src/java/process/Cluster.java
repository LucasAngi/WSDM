package process;

import weka.clusterers.Cobweb;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import error.NegativeParam;
import error.ConvertionError;
import error.FloatParam;

/**
 *
 * @author Angeli
 */
public class Cluster {

    private static final String GROUP_ATTRIBUTE = "group";
    private static final String RELATION_NAME = "relation";

    private Integer numGroups = null;
    private SimpleKMeans clusterer = null;
    private Instances dataset = null;
    private Instances centroids = null;
    private Instances stdDevs = null;

    public Cluster() {
        this.clusterer = new SimpleKMeans();
        this.clusterer.setDisplayStdDevs(true);
    }

    public Integer getNumGroups() {
        return numGroups;
    }

    public void setNumGroups(String numGroups) throws NegativeParam, FloatParam {
        try{
            this.numGroups = Integer.parseInt( numGroups );
            
            if( this.numGroups <= 0 ){
                throw new NegativeParam( "number of groups" );
            }
        }catch(NumberFormatException e){
            throw new FloatParam( "number of groups" ) ;
        }
    }

    public void genereateGroups( ) throws Exception {
        
        if( this.dataset == null ){
            throw new ConvertionError( "input" ) ;
        }

        if (this.numGroups == null) {
            Cobweb aux = new Cobweb();
            aux.buildClusterer(this.dataset);
            this.clusterer.setNumClusters(aux.numberOfClusters());
        } else {
            this.clusterer.setNumClusters(numGroups);
        }

        this.clusterer.buildClusterer(this.dataset);

        this.centroids = this.clusterer.getClusterCentroids();
        this.stdDevs = this.clusterer.getClusterStandardDevs();
    }

    public void clusterInstances() throws Exception {

        Attribute att = new Attribute(GROUP_ATTRIBUTE);

        Instances aux = new Instances(this.dataset);

        this.dataset.insertAttributeAt(att, this.dataset.numAttributes());

        Instance inst;

        for (int cont = 0; cont < this.dataset.numInstances(); cont++) {
            inst = this.dataset.get(cont);
            inst.setValue(inst.numAttributes() - 1, this.clusterer.clusterInstance(aux.get(cont)));
        }

    }

    public Instance getCentroid(int index) {
        return this.centroids.get(index);
    }

    public Instance getSTDDev(int index) {
        return this.stdDevs.get(index);
    }

    public Instances getDataset() {
        return dataset;
    }

    public void setDataset(Instances dataset) {
        this.dataset = dataset;
    }

}
