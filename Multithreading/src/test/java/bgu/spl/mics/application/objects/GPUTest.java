package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    GPU gpu;
    Model model;
    Cluster cluster;
    @Before
    public void setUp() throws Exception {
        cluster = new Cluster();
        model = new Model("Yossi", "Tested", 50000);
        gpu = new GPU("RTX3090");
    }

    @Test
    public void getType() {
        GPU.Type type = GPU.Type.RTX3090;
        assertEquals(type, gpu.getType());
    }

    @Test
    public void getModel() {
        gpu.setModel(model);
        assertEquals(gpu.getModel(), model);
    }

    @Test
    public void setModel() {
        gpu.setModel(model);
        assertEquals(model, gpu.getModel());
    }


    @Test
    public void trainBatch(){
        Data data = new Data("Text", 50000);
        DataBatch batch = new DataBatch(data, 0, gpu);
        gpu.processedData.add(batch);
        gpu.trainBatch();
        assertTrue(gpu.isInTraining());
        assertTrue(gpu.currTrainTicks == 1);
        gpu.trainBatch();
        assertFalse(gpu.isInTraining());
        assertTrue(gpu.trainedData == 1);
        assertTrue(gpu.processedData.isEmpty());
    }
}