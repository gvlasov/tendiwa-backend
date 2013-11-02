package tendiwa.core;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Server receives requests from the {@link TendiwaClient}, calls core methods on receiving such a request, sends
 * occuring {@link org.tendiwa.events.Event}s to the client's receiving end and then sleeps until more requests are done
 * by client.
 */
public enum Server implements Runnable {
	SERVER;
private static final Queue<Request> queue = new LinkedList<>();
private World WORLD;
private boolean stopped;
private int sleepTime = 100;

public static void receive(Request request) {
	request.process();
}

public void setSleepTime(int sleepTime) {
	this.sleepTime = sleepTime;
}

@Override
public void run() {
	while (!stopped) {
		if (queue.isEmpty()) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				while (!queue.isEmpty()) {
					queue.remove().process();
				}
				continue;
			}
		}
	}
}

public void pushRequest(Request request) {
	int size = queue.size();
	queue.offer(request);
	if (queue.size() == 1) {
		Tendiwa.getServerThread().interrupt();
	}
}

void setWorld(WorldProvider provider) {
	this.WORLD = provider.createWorld();
}
public World getWorld() {
	assert WORLD != null;
	return WORLD;
}

void stop() {
	stopped = true;
}
}
