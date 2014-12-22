package org.tendiwa.math;

import com.google.common.collect.ImmutableMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import gnu.trove.strategy.IdentityHashingStrategy;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.ToIntFunction;

/**
 * This algorithm randomly and uniformly generates solutions for the following problem:
 * <p>
 * There is a set <i>A</i> of <i>n</i> distinguishable objects and <i>1 <= m <= n</i> its subsets
 * <i>B<sub>i</sub></i>. For each <i>B<sub>i</sub></i> there is defined <i>1<= c<sub>i</sub> <=n</i> that is
 * called a cap of <i>B<sub>i</sub></i>. Find a mapping from <i>A</i> to <i>{B<sub>i</sub>}</i> so that each
 * <i>B<sub>i</sub></i> has no more than <i>c<sub>i</sub></i> preimages.
 * <p>
 * This algorithm works in O(n*m)
 */
public class IntersectingSetsFiller<T> {

	private final TObjectIntMap<Set<T>> positionsLeft;
	private final Random random;
	private final ImmutableMap<T, Set<T>> answer;
	private final Class<?> elementSetClass;

	/**
	 * @param superset
	 * 	Set <i>A</i> of <i>n</i> distinguishable objects.
	 * @param subsets
	 * 	Subsets of {@code superset}.
	 * @param subsetsToCaps
	 * 	A function that maps from sets <i>B<sub>i</sub></i> to their caps <i>c<sub>i</sub></i>.
	 * @param random
	 * 	Source of randomness. Only one integer is taken from it by this algorithm.
	 * 	<p>
	 * 	In order for the result of this
	 * 	algorithm to be deterministic, this argument should contain {@link java.util.Set}s with deterministic
	 * 	iteration order (i.e. {@link java.util.LinkedHashSet}, but not {@link java.util.HashSet}).
	 */
	public IntersectingSetsFiller(
		Set<T> superset,
		Collection<? extends Set<T>> subsets,
		ToIntFunction<Set<T>> subsetsToCaps,
		Random random
	) {
		this.random = new Random(random.nextInt());
		assert areSubsets(subsets, superset);
		positionsLeft = new TObjectIntCustomHashMap<>(
			new IdentityHashingStrategy<>(),
			subsets.size()
		);
		elementSetClass = Set.class;

		int contentsSize = superset.size();

		for (Set<T> subset : subsets) {
			int value = subsetsToCaps.applyAsInt(subset);
			if (value > contentsSize) {
				throw new IllegalArgumentException(
					"One of subsets has greater cap than the number of elements " +
						"present in A (cap is " + value + ", cardinality of A is " + contentsSize
				);
			}
			positionsLeft.put(subset, value);
		}

		Map<T, Set<T>[]> containing = prepareContainingMap(superset, subsets);
		int[] indices = IntegerPermutationGenerator.generateUsingFisherYates(contentsSize, contentsSize, random);
		T[] arrayContents = (T[]) superset.toArray();
		ImmutableMap.Builder<T, Set<T>> builder = ImmutableMap.builder();
		for (int index : indices) {
			T element = arrayContents[index];
			Set<T>[] whereContained = containing.get(element);
			int indexOfSubset = getIndexOfSubset(whereContained);
			if (indexOfSubset == -1) {
				continue;
			}
			Set<T> subset = whereContained[indexOfSubset];
			if (positionsLeft.get(subset) == 0) {
				continue;
			}
			positionsLeft.adjustValue(subset, -1);
			builder.put(element, subset);
		}
		answer = builder.build();
	}

	public ImmutableMap<T, Set<T>> getAnswer() {
		return answer;
	}

	private int getIndexOfSubset(
		Set<T>[] whereContained
	) {
		int elementsLeft = 0;
		for (Set<T> set : whereContained) {
			elementsLeft += positionsLeft.get(set);
		}
		if (elementsLeft == 0) {
			return -1;
		}
		assert elementsLeft > 0;
		elementsLeft = random.nextInt(elementsLeft);
		int i;
		for (i = 0; i < whereContained.length; i++) {
			elementsLeft -= positionsLeft.get(whereContained[i]);
			if (elementsLeft <= 0) {
				return i;
			}
		}
		assert i == whereContained.length;
		return -1;
	}


	/**
	 * Finds out what element is contained in what subset.
	 *
	 * @param contents
	 * 	Elements.
	 * @param subsets
	 * 	Subsets.
	 * @return A mapping from contents to subsets.
	 */
	private Map<T, Set<T>[]> prepareContainingMap(Set<T> contents, Collection<? extends Set<T>> subsets) {
		Map<T, Set<T>[]> containing = new LinkedHashMap<>(contents.size());
		for (T element : contents) {
			LinkedHashSet<Set<T>> elementSubsets = new LinkedHashSet<>();
			for (Set<T> subset : subsets) {
				if (subset.contains(element)) {
					elementSubsets.add(subset);
				}
			}
			Set<T>[] array = (Set<T>[]) Array.newInstance(elementSetClass, elementSubsets.size());
			elementSubsets.toArray(array);
			containing.put(element, array);
		}
		return containing;
	}

	/**
	 * Checks that each set in {@code keys} is contained in {@code contents}.
	 *
	 * @param subsets
	 * 	An array of sets.
	 * @param contents
	 * 	A set of all available objects.
	 * @return true if contents contains all elements of each set in {@code subsets}, false otherwise.
	 */
	private boolean areSubsets(Collection<? extends Set<T>> subsets, Set<T> contents) {
		for (Set<T> subset : subsets) {
			if (!contents.containsAll(subset)) {
				return false;
			}
		}
		return true;
	}
}
