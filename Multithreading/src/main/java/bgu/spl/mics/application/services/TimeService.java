package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadCast;

import java.util.Timer;
import java.util.TimerTask;


/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
	private final long duration;
	private final long tickTime;
	private Timer timer;
	private long ticks;
	private TickBroadCast tickBroadCast;

	public TimeService(long duration, long tickTime) {
		super("Timer");
		this.duration = duration;
		this.tickTime = tickTime;
		this.timer = new Timer(true);
		ticks = 0;
		this.tickBroadCast = new TickBroadCast();
	}
	@Override
	protected void initialize() {
		//create a new TimerTask such that its run method basically does sendBroadcast(tickBroadcast)
		TimerTask tasker = new TimerTask() {
			@Override
			public void run() {
				if(ticks < duration) {
					ticks++;
					sendBroadcast(tickBroadCast);
				} else
					cancel();
			}
		};
		//create a new TimerTask such that its run method basically does sendBroadcast(TerminationBroadcast)
		TimerTask terminate = new TimerTask() {
			@Override
			public void run() {
				tasker.cancel();
				TerminationBroadcast t = new TerminationBroadcast();
				sendBroadcast(t);
			}
		};
		timer.schedule(terminate,duration); //after this duraion send termination broadcast
		timer.scheduleAtFixedRate(tasker, 0, tickTime); //sends a tickBroadcast every 'ticktime' milliseconds



		subscribeBroadcast(TerminationBroadcast.class, b -> {
			terminate();
		});
	}
}




























