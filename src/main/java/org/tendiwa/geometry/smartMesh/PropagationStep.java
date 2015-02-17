package org.tendiwa.geometry.smartMesh;

interface PropagationStep {
	Ray ray();
	boolean isTerminal();
}
