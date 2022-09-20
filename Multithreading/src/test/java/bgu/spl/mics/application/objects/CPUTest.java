package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

public class CPUTest {
    CPU cpu;
    GPU gpu;
    Cluster cluster;
    Queue<DataBatch> dataQueue;
    Data data;

    @Before
    public void setUp(){
        cluster = new Cluster();
        cpu = new CPU(32);
        gpu = new GPU("RTX3090");
        dataQueue = new ConcurrentLinkedQueue<>();
        data = new Data("Images", 50000);
    }

    @Test
    public void getNumOfCores() {
        int numOfCores = 32;
        assertEquals(numOfCores, cpu.getNumOfCores());
    }

    @Test
    public void processBatch(){
        DataBatch batch = new DataBatch(data, 0, gpu);
        cpu.addUnprocessedBatch(batch);
        cpu.processBatch();
        assertTrue(cpu.getData().isEmpty());
        assertTrue(cpu.currBatch == batch);
        assertTrue(cpu.getCurrTrainTicks() == 1);

    }
}

