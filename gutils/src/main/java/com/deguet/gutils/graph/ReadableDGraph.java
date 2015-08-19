package com.deguet.gutils.graph;

import java.util.HashSet;
import java.util.Set;

import com.deguet.gutils.nuplets.Duo;
import com.deguet.gutils.nuplets.Trio;

/**
 * Readable DGraph
 * @author joris
 *
 */
public interface ReadableDGraph<V,E>  {

	public E  getEdge(V a, V b);
	
	public Set<Duo<V,E>> labeledPredecessors(V v);
	
	public Set<Duo<V,E>> labeledSuccessors(V v);
	
	public Set<Duo<V, V>> couples();
	
	public Set<V> vertices();
	
	public Set<V> sinks();
	
	public Set<V> sources();
	
	public Set<Trio<V, V, E>> triplets();
	
	public boolean contains(V v);

	public Set<Iterable<V>> pathsFromTo2(Set<V> starters, Set<V> finishers);
	
}
