package bgu.spl.mics.application.objects;


import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private ConcurrentLinkedQueue<DataBatch> data;
    private Cluster cluster;
    public DataBatch currBatch;
    public int currTrainTicks;


    //@INV: pre(cores) == post(cores)
    public CPU(int cores) {
        this.cores = cores;
        this.cluster = Cluster.getInstance();
        data = new ConcurrentLinkedQueue<>();
        currBatch = null;
        currTrainTicks = 0;
    }


    public int getNumOfCores() {return cores;}

    public void addUnprocessedBatch (DataBatch batch){data.add(batch);}


    public void processBatch () {
        //First retrieve unprocessed batches from the cluster to the CPU
        if(data.size() == 0){
            cluster.CPUaddUnprocessedBatch(this);
        }
        //Now update current batch to process to be the next available batch in the CPU data field
        if (currBatch == null)
            currBatch = data.poll();

        //If current batch is still null, then there are no unprocessed batches available to process right now
        if(currBatch != null){
            cluster.incrementCpuTime();
            currTrainTicks++;

            //If done processing this batch, send it to the cluster and update current batch
            if(currTrainTicks == (32 / cores) * currBatch.getData().getTypeToInt()){
                cluster.CPUsendProcessedBatch(currBatch);
                currTrainTicks = 0;
                currBatch = data.poll();
            }

        }

    }



    //For the tests
    public ConcurrentLinkedQueue<DataBatch> getData() {return data;}

    public int getCurrTrainTicks() {return currTrainTicks;}
}
