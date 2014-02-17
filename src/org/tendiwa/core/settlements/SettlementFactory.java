package org.tendiwa.core.settlements;

public interface SettlementFactory {
public Settlement create(
	Namer<Settlement> namer,
	int size
);
}
