package org.tendiwa.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Server receives requests from the {@link TendiwaClient}, calls core methods on receiving such a request, sends
 * resulting {@link org.tendiwa.core.observation.Event}s to the client's receiving end and then sleeps until more
 * requests are done by client.
 */
@Singleton
public class Server extends Thread {
private static Request currentRequest;
private boolean stopped = false;
private int sleepTime = 100;
private boolean hasRequestProcessing = false;

@Inject
Server(
) {
	setName("Tendiwa Backend");
}

public boolean hasRequestToProcess() {
	return hasRequestProcessing;
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

public void passRequest(Request request) {
	assert currentRequest == null : "Pushed " + request.getClass().getName() + " when there is already a request " + currentRequest.getClass().getName()
		+ "; hasRequestProcessing = " + hasRequestProcessing;
	hasRequestProcessing = true;
	currentRequest = request;
	interrupt();
}

//public void pushRequest(Request request) {
//	assert currentRequest == null : "Pushed "+request.getClass().getName()+" when there is already a request "+currentRequest.getClass().getName()
//		+"; hasRequestProcessing = "+ hasRequestProcessing;
//	hasRequestProcessing = true;
//	currentRequest = request;
//	Tendiwa.getServerThread().interrupt();
//}

}
