package tendiwa.core;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Server receives requests from the {@link TendiwaClient}, calls core methods on receiving such a request, sends
 * resulting {@link org.tendiwa.events.Event}s to the client's receiving end and then sleeps until more requests are
 * done by client.
 */
public enum Server implements Runnable {
	SERVER;
private static final Queue<Request> requestQueue = new LinkedList<>();
private World WORLD;
private boolean stopped;
private int sleepTime = 100;
private boolean turnComputing = false;

public static void receive(Request request) {
	request.process();
}

public static boolean isTurnComputing() {
	return SERVER.turnComputing;
}

public void setSleepTime(int sleepTime) {
	this.sleepTime = sleepTime;
}

@Override
public void run() {
	while (!stopped) {
		if (requestQueue.isEmpty()) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				turnComputing = true;
				synchronized (requestQueue) {
					if (!requestQueue.isEmpty()) {
						requestQueue.remove().process();
					}
					requestQueue.notify();
				}
				turnComputing = false;
				continue;
			}
		}
	}
}

public void pushRequest(Request request) {
	requestQueue.offer(request);
	if (requestQueue.size() == 1) {
		Tendiwa.getServerThread().interrupt();
	}
}

public World getWorld() {
	assert WORLD != null;
	return WORLD;
}

void setWorld(WorldProvider provider) {
	this.WORLD = provider.createWorld();
}

void stop() {
	stopped = true;
}
}
