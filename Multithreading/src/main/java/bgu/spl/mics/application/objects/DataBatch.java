package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int firstIndex;
    private GPU gpu;


    public DataBatch(Data data, int firstIndex, GPU gpu){
        this.data = data;
        this.firstIndex = firstIndex;
        this.gpu = gpu;
    }

    public Data getData() {
        return data;
    }

    public GPU getGpu() {return gpu;}
}
