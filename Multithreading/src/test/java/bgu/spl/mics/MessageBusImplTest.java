package bgu.spl.mics;

import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.services.GPUService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    MessageBusImpl msb;
    EventImpl e;
    MicroService ms;
    BroadcastImpl b;


    public class EventImpl implements Event<String> {}
    public class BroadcastImpl implements Broadcast {}


    @Before
    public void setUp(){
        msb = MessageBusImpl.getInstance();
        e = new EventImpl();
        b = new BroadcastImpl();
        ms = new GPUService("shlomo", new GPU(("RTX3090")));

    }


    @Test
    public void complete() {
        msb.register(ms); //ms registers itself
        msb.subscribeEvent(e.getClass(),ms); //ms subscribes itself to an event.
        Future<String> future = msb.sendEvent(e); //msb sends the event to some microservice that is subscribed to receive this kind
        //of event. the only microservice that is capable of getting this event in this case is ms.

        assertFalse(future.isDone()); //insures that the future object is not resolved yet
        String result = "result";
        msb.complete(e, result);
        //checks that the complete action has worked as should:
        assertTrue(future.isDone()); //1. notifies that the future object has been resolved
        assertEquals(result, future.get());//2. checks the result of the future object.

    }

    @Test
    public void sendBroadcast() {
        msb.register(ms); //ms registers itself
        msb.subscribeBroadcast(b.getClass(),ms); //ms subscribes itself to an event.
        msb.sendBroadcast(b);

        Message msg = null;
        try {
            msg = msb.awaitMessage(ms); //now ms takes the event message from its queue
        }
        catch(Exception e){ //is the actions did not succeed then ms won't have a message waiting in its queue
            System.out.println("Something went wrong");
        }
        assertTrue(msg != null);

    }

    @Test
    public void sendEvent() {
        msb.register(ms); //ms registers itself
        msb.subscribeEvent(e.getClass(),ms); //ms subscribes itself to an event.
        msb.sendEvent(e); //msb sends the event to some microservice that is subscribed to receive this kind
                    //of event. the only microservice that is capable of getting this event in this case is ms.

        Message msg = null;
        try {
            msg = msb.awaitMessage(ms); //now ms takes the event message from its queue
        }
        catch(Exception e){ //is the actions did not succeed then ms won't have a message waiting in its queue
            System.out.println("Something went wrong");
        }
        assertTrue(msg != null);


        //now check that unregister works as should:
        msb.unregister(ms);
        try{
            msb.awaitMessage(ms);
            assertTrue(false); //if we got here then it means that the ms still has a queue after it was unregistered
                                        //so it should fail this test
        }
        catch (Exception e){
            assertTrue(true);// if we got here then it means that there was indeed a problem-
                                      //the ms could not locate its queue (which is good because we unregistered it).
        }


    }
}

