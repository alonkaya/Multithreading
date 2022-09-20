package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadCast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;

    public GPUService(String name , GPU gpu) {
        super(name);
        this.gpu = gpu;

    }

    @Override
    protected void initialize() {
        subscribeEvent(TrainModelEvent.class, ev -> {
            ev.getTrainingModel().setStatus("PreTrained");
            gpu.toTrain.add(ev.getTrainingModel());
            gpu.trainModelEvents.put(ev.getTrainingModel(),ev);

        });



        subscribeBroadcast(TickBroadCast.class, ev ->{
            //If the GPU doesn't have a current model training, then get the next one from its model list
            //and send its data to the cluster
            if(gpu.getModel() == null & gpu.toTrain.size() > 0){
                gpu.setModel(gpu.toTrain.remove());
                gpu.getModel().setStatus("Training");
                gpu.divideAndSendData();
            }
            //Retrieve as many processed batches as the GPU can hold from the cluster and train the next batch
            gpu.getProcessedBatches();
            gpu.trainBatch();
            //If done training a model: set its status and its future's result
            if(gpu.getModel() != null && gpu.trainedData == gpu.getModel().getData().getSize()/1000) {
                gpu.getModel().setStatus("Trained");
                complete(gpu.trainModelEvents.get(gpu.getModel()), gpu.getModel());
                gpu.getCluster().addTrainedModel(gpu.getModel());
                gpu.setModel(null);
                gpu.trainedData = 0;
            }
        });

        subscribeBroadcast(TerminationBroadcast.class, b -> {
            terminate();
        });

        subscribeEvent(TestModelEvent.class, t -> {
            double result = Math.random();
            if(t.getModel() != null) {
                Student student = t.getModel().getStudent();
                Model model = t.getModel();
                if (student.getStudentDegree() == "MSc") {
                    if (result <= 0.6) {
                        model.setStatus("Tested");
                        model.setResult("Good");
                    } else {
                        model.setResult("Bad");
                        model.setStatus("Tested");
                    }
                } else {
                    if (result <= 0.8) {
                        model.setStatus("Tested");
                        model.setResult("Good");
                    } else {
                        model.setResult("Bad");
                        model.setStatus("Tested");
                    }
                }

                complete(t, model);
            }
        });

    }
}
