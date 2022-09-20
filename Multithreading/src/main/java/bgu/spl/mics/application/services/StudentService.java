package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private Event toResolve;
    private boolean terminated;



    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
        terminated = false;
    }

    @Override
    protected void initialize() {
        Thread thread = new Thread(()-> {
            while (student.getModels().length > student.getModelSent() & !terminated){
                TrainModelEvent trainmodel = new TrainModelEvent(student.getNextModel());
                Future future = sendEvent(trainmodel);
                toResolve = trainmodel;
                if(future != null & !terminated) {
                    Model model = (Model) future.get();
                    TestModelEvent test = new TestModelEvent(model);
                    future = sendEvent(test);
                    toResolve = test;
                    if(future != null & !terminated) {
                        model = (Model) future.get();
                        student.addTrainedModel(model);
                        PublishResultsEvent result = new PublishResultsEvent(model);
                        sendEvent(result);

                    }
                }
            }
        });
        thread.start();



        subscribeBroadcast(PublishConfrenceBroadcast.class , pbc -> {
            LinkedBlockingQueue<Model> confrence = pbc.getPublished().getModelsToPublish();
            for(Model model: confrence) {
                if (model.getStudent() == student)
                    student.incrementPublications();
                else
                    student.incrementPapersRead();
                model.setPublished(true);
            }

        });



        subscribeBroadcast(TerminationBroadcast.class, b -> {
            complete(toResolve,null);
            terminated = true;
            terminate();
        });

    }
}

