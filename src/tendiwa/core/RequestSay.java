package tendiwa.core;

public class RequestSay implements Request {
private final String speech;

public RequestSay(String speech) {
	this.speech = speech;
}

@Override
public void process() {
	Tendiwa.getPlayerCharacter().say(speech);
}
}
