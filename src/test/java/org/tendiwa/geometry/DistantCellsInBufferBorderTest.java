package org.tendiwa.geometry;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.data.DistantCellsInBufferBorderModule;

import java.util.Set;

import static org.junit.Assert.*;

@RunWith(JukitoRunner.class)
@UseModules(DistantCellsInBufferBorderModule.class)
public class DistantCellsInBufferBorderTest {

	@Inject
	DistantCellsFinder distantCells;


	@Test
	public void shouldShow5Points() {
		Set<Cell> cells = Sets.newHashSet(distantCells);
		assertFalse(
			String.valueOf(cells.size()),
			cells.isEmpty()
		);
	}
}
