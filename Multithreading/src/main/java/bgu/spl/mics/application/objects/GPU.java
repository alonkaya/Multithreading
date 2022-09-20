package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.TrainModelEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private final Type type;
    public final int maxProcessedBatches;
    private final Cluster cluster;
    private final int ticksPerBatch;
    private boolean inTraining;
    private Model model;
    public HashMap<Model,TrainModelEvent> trainModelEvents; //This Field is to indicate which GPU trained which trainModelEvent
    // so that we know which model to resolve later
    public LinkedList<DataBatch> processedData;
    public int trainedData; //this field is to let us know when the GPU is done handling this model
    public int currTrainTicks; //this field is to let us know when the GPU is done training this batch
    public LinkedList<Model> toTrain; //models needed to be trained


    //@INV: pre(this.type) = post (this.type)
    public GPU(String type) {
        this.cluster = Cluster.getInstance();
        if (type.equals("RTX3090")) {
            this.type = Type.RTX3090;
            maxProcessedBatches = 32;
            ticksPerBatch = 1;
        } else if (type.equals("RTX2080")) {
            this.type = Type.RTX2080;
            maxProcessedBatches = 16;
            ticksPerBatch = 2;
        } else {
            this.type = Type.GTX1080;
            maxProcessedBatches = 8;
            ticksPerBatch = 4;
        }
        model = null;
        inTraining = false;
        trainModelEvents = new HashMap<>();
        processedData = new LinkedList<>();
        trainedData = 0;
        currTrainTicks = 0;
        toTrain = new LinkedList<>();
    }

    public void setModel(Model model) {
        this.model = model;
    }
    public Model getModel() {
        return model;
    }

    public Cluster getCluster(){ return cluster;}

    public Type getType() {return type;}

    //Send all unprocessed batches to cluster
    public void divideAndSendData () {
       int numberOfBatches = model.getData().getSize()/1000;
       int firstIndex = 0;
       ConcurrentLinkedQueue<DataBatch> toSend = new ConcurrentLinkedQueue<>();
       for (int i = 0; i < numberOfBatches; i++, firstIndex = firstIndex + 1000) {
           toSend.add(new DataBatch(model.getData(),firstIndex ,this));
       }
       cluster.GPUsendUnprocessedData(this,toSend);
    }



    //Retrieve processed batches from cluster up to the max limit it can hold
    public void getProcessedBatches () {
        while (processedData.size() < maxProcessedBatches) {
            DataBatch batch = cluster.GPUaddProcessedBatch(this);
            if (batch != null)
                processedData.add(batch);
            else
                break;
        }
    }



    public void trainBatch() {
        if(!inTraining & processedData.size()>0){
            currTrainTicks = 1;
            inTraining = true;
            cluster.incrementGpuTime();
        }
        else if(currTrainTicks == ticksPerBatch){
            processedData.poll();
            trainedData++;
            inTraining = false;
            currTrainTicks = 0;
            if(processedData.size()>0) {
                currTrainTicks = 1;
                inTraining = true;
                cluster.incrementGpuTime();
            }
        }
        else if(inTraining){
            currTrainTicks++;
            cluster.incrementGpuTime();
        }
    }




    //For tests:


    public boolean isInTraining() {return inTraining;}
}