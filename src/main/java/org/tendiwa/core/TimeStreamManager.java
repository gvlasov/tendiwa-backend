package org.tendiwa.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.core.factories.TimeStreamFactory;
import org.tendiwa.core.player.SinglePlayerMode;

import java.util.Collection;
import java.util.HashSet;

@Singleton
public class TimeStreamManager {
	private final SinglePlayerMode singlePlayerMode;
	private final TimeStreamFactory factory;
	private final Collection<TimeStream> timeStreams = new HashSet<>();

	@Inject
	TimeStreamManager(
		SinglePlayerMode singlePlayerMode,
		TimeStreamFactory factory
	) {

		this.singlePlayerMode = singlePlayerMode;
		this.factory = factory;
	}

	public void populate(World world) {
		TimeStream playerTimeStream = createTimeStream();
		for (HorizontalPlane horizontalPlane : world.getPlanes()) {
			for (Chunk chunk : horizontalPlane.getChunks()) {
				for (Character character : chunk.getCharacters()) {
					if (singlePlayerMode.isPlayer(character)) {
						playerTimeStream.addPlayerCharacter(character);
					} else {
						playerTimeStream.addNonPlayerCharacter((NonPlayerCharacter) character);
					}
				}
			}
		}
		assert timeStreams.size() > 0;
	}

	public TimeStream createTimeStream() {
		TimeStream timeStream = factory.create();
		timeStreams.add(timeStream);
		return timeStream;
	}
}
