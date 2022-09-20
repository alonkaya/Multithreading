package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private boolean isDone;
	private T result;
	
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		this.isDone = false;
		this.result = null;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	//@pre: none
	//@post: none
	public synchronized T get() {
		while (!isDone) {
			try {
				this.wait(); //waiting until the result is resolved
			} catch (InterruptedException e) {}
		}
		return this.result;
	}
	
	/**
     * Resolves the result of this Future object.
     */
	//@param: result
	//@pre: none
	//@post: get() == result
	//@post: isDone() == True
	public synchronized void resolve (T result) {
		if(!isDone) { //resolving the result and updating the status
			this.isDone = true;
			this.result = result;
		}
		this.notifyAll(); //notifying the threads sleeping/waiting on this object to let them get the result
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	//@pre: none
	//@post: none
	public synchronized boolean isDone() {
		return isDone;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	//@pre: none
	//@post: none
	public synchronized T get(long timeout, TimeUnit unit) {
		if (isDone) { //first checking if the result is resolved
			return result;
		} else {
			try {
				unit.sleep(timeout); //sleeping for the timeout given
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result; //returning the result after the given timout, if it hasn't been resolved the result will be null
	}
}
