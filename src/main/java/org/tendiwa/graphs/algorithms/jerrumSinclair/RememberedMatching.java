package org.tendiwa.graphs.algorithms.jerrumSinclair;

/**
 * A perfect or a near perfect matching. Remembers changes done to a {@link Matching}
 * it, so updating it with another adjacency
 * values is independent of {@link RememberedMatching}'s length,
 * but is dependent on how many changes have been done on a
 * source {@link Matching} since last update.
 */
class RememberedMatching {
	private final int[] adjacency;
	private final int numberOfAllVertices;
	private int numberOfChangedIndices = Integer.MAX_VALUE;
	/**
	 * In this variable we save indices that were changed in the current {@link Matching} since the
	 * last remembering of a good Matching. This is necessary in order to not copy the whole array each time we need to
	 * save a good Matching.
	 */
	private final int[] changedIndices;
	/**
	 * This limit is in order to, on the other hand, copy the whole matching to the remembered array if there
	 * were so many changes noted that it is inefficient to apply them one by one instead of just copying the
	 * whole array.
	 */
	private final int maxNumberOfChangedIndices;
	private boolean isPerfect = false;

	RememberedMatching(int numberOfAllVertices) {
		this.numberOfAllVertices = numberOfAllVertices;
		this.adjacency = new int[numberOfAllVertices];
		this.maxNumberOfChangedIndices = numberOfAllVertices / 4;
		this.changedIndices = new int[maxNumberOfChangedIndices];
	}

	public void addChange(int indexOfUnderlyingMatching) {
		if (numberOfChangedIndices == maxNumberOfChangedIndices) {
			return;
		}
		changedIndices[numberOfChangedIndices++] = indexOfUnderlyingMatching;
	}

	void updateWith(Matching matching) {
		if (numberOfChangedIndices < maxNumberOfChangedIndices) {
			for (int i = 0; i < numberOfChangedIndices; i++) {
				adjacency[changedIndices[i]] = matching.adjacency[changedIndices[i]];
			}
		} else {
			loadMatching(matching);
		}
		numberOfChangedIndices = 0;
		isPerfect = matching.isPerfect();
	}

	/**
	 * Copies adjacencies of {@link Matching} to this
	 * RememberedMatching.
	 *
	 * @param matching
	 * 	A matching to copy into this one.
	 */
	void loadMatching(Matching matching) {
		System.arraycopy(matching.adjacency, 0, adjacency, 0, numberOfAllVertices);
	}


	/**
	 * This is the Type 2 transition from the Jerrum and Sinclair paper "Approximating the permanent".
	 *
	 * @param index1
	 * 	An index of a vertex.
	 * @param index2
	 * 	An index of another vertex.
	 * @see QuasiJerrumSinclairMarkovChain for more info on how the paper is related to the
	 * problem this class solves.
	 */
	private void addEdge(int index1, int index2) {
		assert index1 != index2;
		assert adjacency[index1] == BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		assert adjacency[index2] == BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		adjacency[index1] = index2;
		adjacency[index2] = index1;
	}

	/**
	 * Returns the whole adjacency array of the remembered matching.
	 * <p>
	 * This is an instance of a Public Morozov antipattern. It is done so because doing it the right way (i.e.,
	 * accessing individual elements of the array) would only bring unnecessary complexity.
	 *
	 * @return This matching's adjacency array.
	 */
	public int[] getAdjacencyArray() {
		return adjacency;
	}

	public boolean isPerfect() {
		return isPerfect;
	}
}
