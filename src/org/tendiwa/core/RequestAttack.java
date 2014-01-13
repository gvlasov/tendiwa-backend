package org.tendiwa.core;

public class RequestAttack implements Request {
public final Character aim;

public RequestAttack(Character aim) {
	this.aim = aim;
}

@Override
public void process() {
	Tendiwa.getPlayerCharacter().attack(aim);
}
}
