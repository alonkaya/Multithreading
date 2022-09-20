package bgu.spl.mics;

import bgu.spl.mics.application.messages.TickBroadCast;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	//holds the microservices with a vector for each Events and Broadcasts they subscribed to
	private ConcurrentHashMap <MicroService, Pair<Vector<Class<? extends Broadcast>>, Vector<Class<? extends Event>>>> microServicesMap;
	//a Map for the microservices and their message queue's
	private ConcurrentHashMap <MicroService, LinkedBlockingQueue<Message>> messageMap;
	//a Map to assign the Events in the correct order by the RoundRobin idea
	private ConcurrentHashMap <Class<? extends Event>, Pair<Vector<MicroService>, AtomicInteger>> roundRobinMap;
	//a Map for the events currently in process and their future 
	private ConcurrentHashMap <Event,Future> futureMap;

	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	private MessageBusImpl(){
		microServicesMap = new ConcurrentHashMap<>();
		messageMap = new ConcurrentHashMap<>();
		roundRobinMap = new ConcurrentHashMap<>();
		futureMap = new ConcurrentHashMap<>();
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		microServicesMap.get(m).getSecondObject().add(type); //adding the event the ms subscribing to the events vector
		if(!roundRobinMap.containsKey(type)) { //if the event type does not exist in the roundRobin map
			roundRobinMap.put(type,new Pair<>(new Vector<>(), new AtomicInteger())); //create the event in the roundRobin
		}
		roundRobinMap.get(type).getFirstObject().add(m); //adds the microservice to the subscribed event vector	

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//adds the broadcast message to the subscribed microservice broadcast's vector
		microServicesMap.get(m).getFirstObject().add(type);

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		//if the microservice finished working on the event resolve it to the given result
		futureMap.get(e).resolve(result);

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		//going through all the microservices and checking if they subscribed to the Broadcast type
		for (Map.Entry<MicroService,Pair <Vector<Class<? extends Broadcast>>, Vector<Class<? extends Event>>>> entry: microServicesMap.entrySet()) {
			if (entry.getValue().getFirstObject().contains(b.getClass())) {
				//if the subscribed insert the broadcast message to their message queue
				try { messageMap.get(entry.getKey()).put(b);}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		//creating a new future object and inserting to the future map
		futureMap.put(e,future);
		Pair <Vector<MicroService>,AtomicInteger> subVector;
		do {
			subVector = roundRobinMap.get(e.getClass());
		} while (subVector == null);

		//checking if any microservice registered for this type of events
		if(subVector.getFirstObject().size() > 0) {
			//assigning the event to the correct microservice by the roundrobin manner
			AtomicInteger ai = new AtomicInteger(0);
			do{
			ai.set(subVector.getSecondObject().getAndIncrement() % subVector.getFirstObject().size());
			}while (subVector.getFirstObject().size() <= ai.intValue());

			MicroService myTurn = subVector.getFirstObject().get(ai.intValue());
			try {
				//inserting the event to the microservice message queue
				messageMap.get(myTurn).put(e);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		else {
			//if no microservice registered for this type of events
			return null;
		}
		//returning the future for the corresponding event
		return future;
	}

	@Override
	public void register(MicroService m) {
		//creating the event and broadcast vectors to store the subscribed message types
		Vector<Class<? extends Broadcast>> broadCastVector = new Vector<>(); 
		Vector<Class<? extends Event>> eventVector = new Vector<>();
		//pairing them together for the microservice map insertion
		Pair <Vector<Class<? extends Broadcast>>,Vector<Class<? extends Event>>> vectorPair = new Pair<>(broadCastVector,eventVector);
		//inserting the new microservice registered to the ms Map and the messageMap
		microServicesMap.put(m,vectorPair);
		messageMap.put(m,new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		Vector<Class<? extends Event>> eventsVector = microServicesMap.get(m).getSecondObject();
		microServicesMap.remove(m);
		//removes the subscription on the roundrobin map for the unregistering microservice
		for (Class<? extends Event> ev : eventsVector ) {
			roundRobinMap.get(ev).getFirstObject().remove(m);
		}
		LinkedBlockingQueue<Message> toRemove = messageMap.get(m);
		//resolving the events that the microservice was supposed to work on to null
		for (Message message : toRemove) {
			if(message.getClass() != TickBroadCast.class) {
				futureMap.get(message).resolve(null);
			}
		}
		//removing from the message map as well
		messageMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		//checking on the queue to get the messages to work on, if empty keeps waiting
		return messageMap.get(m).take();
	}

	

}
