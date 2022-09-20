package bgu.spl.mics.application.objects;


import bgu.spl.mics.Pair;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private Vector<GPU> gpus;
	private Vector<CPU> cpus;
	private AtomicInteger cpuTime;
	private AtomicInteger gpuTime;
	private AtomicInteger batchesProcessed;
	private ConcurrentLinkedQueue<String> modelsTrained; //stored the names of the models trained in a lined list
	private HashMap <Integer, Pair<ConcurrentLinkedQueue<DataBatch>, ConcurrentLinkedQueue<DataBatch>>> GpusMap;
	private AtomicInteger lastPulled;



	public Cluster() {
		this.cpuTime = new AtomicInteger(0);
		this.gpuTime = new AtomicInteger(0);
		this.batchesProcessed = new AtomicInteger(0);
		this.modelsTrained = new ConcurrentLinkedQueue<>();
		this.GpusMap = new HashMap<>();
		this.lastPulled = new AtomicInteger(0);

	}

	private static class SingletonHolder {
		private static Cluster instance = new Cluster();
	}

	public static Cluster getInstance() {
		return SingletonHolder.instance;
	}


	public void addTrainedModel (Model model) {modelsTrained.add(model.getName());}


	public void setGpus(Vector<GPU> gpus) {
		this.gpus = gpus;
		for (int i=0; i<gpus.size();i++){
			GpusMap.put(i,new Pair<ConcurrentLinkedQueue<DataBatch>,ConcurrentLinkedQueue<DataBatch>> (new ConcurrentLinkedQueue<DataBatch>(),new ConcurrentLinkedQueue<DataBatch>()));
		}
	}
	public void setCpus(Vector<CPU> cpus) {this.cpus = cpus;}


	public void incrementCpuTime () {cpuTime.incrementAndGet();}
	public void incrementGpuTime () {gpuTime.incrementAndGet();}

	public int getCpuTime () {return cpuTime.intValue();}
	public int getGpuTime () {return gpuTime.intValue();}

	public int getBatchesProcessed () {return batchesProcessed.intValue();}








	/////////////From the GPU to the cluster////////////////////
	public void GPUsendUnprocessedData (GPU gpu , ConcurrentLinkedQueue<DataBatch> data) {
		GpusMap.get(gpus.indexOf(gpu)).setFirstObject(data);
	}

	/////////////////From the cluster to the CPU////////////////
	public void CPUaddUnprocessedBatch (CPU cpu) {
		int i = 0;
		ConcurrentLinkedQueue<DataBatch> q = new ConcurrentLinkedQueue<>();
		while(q.size() == 0 && i < gpus.size()){
			q = GpusMap.get(lastPulled.getAndIncrement() % (gpus.size())).getFirstObject();
			i++;
		}

		//If we found one that is not empty retrieve a batch from it to the CPU
		if (q.size() > 0)
			cpu.addUnprocessedBatch(q.poll());

	}

	/////////////////////From the CPU to the cluster/////////////////
	public void CPUsendProcessedBatch(DataBatch batch){
		batchesProcessed.incrementAndGet();
		GpusMap.get(gpus.indexOf(batch.getGpu())).getSecondObject().add(batch);
	}

	////////////////////////From the cluster to the GPU/////////////
	public DataBatch GPUaddProcessedBatch (GPU gpu) {
		return GpusMap.get(gpus.indexOf(gpu)).getSecondObject().poll();
	}



}
