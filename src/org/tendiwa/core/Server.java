package org.tendiwa.core;

import java.lang.*;

/**
 * Server receives requests from the {@link TendiwaClient}, calls core methods on receiving such a request, sends
 * resulting {@link org.tendiwa.core.observation.Event}s to the client's receiving end and then sleeps until more requests are
 * done by client.
 */
public enum Server implements Runnable {
	SERVER;
private static Request currentRequest;
private World WORLD;
private boolean stopped = false;
private int sleepTime = 100;
private boolean hasRequestProcessing = false;


public static boolean hasRequestToProcess() {
	return SERVER.hasRequestProcessing;
}

public void setSleepTime(int sleepTime) {
	this.sleepTime = sleepTime;
}

@Override
public void run() {
	while (!stopped) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			if (currentRequest != null) {
				currentRequest.process();
				currentRequest = null;
			}
			hasRequestProcessing = false;
		}
	}
}

//public void pushRequest(Request request) {
//	assert currentRequest == null : "Pushed "+request.getClass().getName()+" when there is already a request "+currentRequest.getClass().getName()
//		+"; hasRequestProcessing = "+ hasRequestProcessing;
//	hasRequestProcessing = true;
//	currentRequest = request;
//	Tendiwa.getServerThread().interrupt();
//}

public World getWorld() {
	assert WORLD != null;
	return WORLD;
}

void setWorld(WorldProvidingModule provider) {
	this.WORLD = provider.createWorld();
	for (Character character : WORLD.getTimeStream().getCharacters()) {
		WORLD.getTimeStream().notifyNeighborsVisiblilty(character);
	}
}

}
