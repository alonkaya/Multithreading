package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadCast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConfrenceInformation confrence;
    private int currTicks;

    public ConferenceService(String name , ConfrenceInformation confrence) {
        super(name);
        this.confrence = confrence;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadCast.class, t ->{
            currTicks++;
            if(confrence.getDate() == currTicks){
                PublishConfrenceBroadcast pcb = new PublishConfrenceBroadcast(confrence);
                sendBroadcast(pcb);
                terminate();
            }
        });


        subscribeEvent(PublishResultsEvent.class, pre -> {
            if (pre.getModel().getCurrResult().equals("Good")) {
                try {
                    confrence.modelsToPublish.put(pre.getModel());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    });

        subscribeBroadcast(TerminationBroadcast.class, b -> {
            terminate();
        });
}
}
