package core;

import java.util.ArrayList;
import java.util.HashMap;
import weka.clusterers.Cobweb;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

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

    public void setNumGroups(Integer numGroups) {
        this.numGroups = numGroups;
    }

    public void genereateGroups( ) throws Exception {
        
        if( this.dataset == null ){
            // lança exeção
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
