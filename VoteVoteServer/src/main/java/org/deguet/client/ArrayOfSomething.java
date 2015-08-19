package org.deguet.client;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * Thanks to http://stackoverflow.com/questions/14139437/java-type-generic-as-argument-for-gson
 * @author joris
 *
 * @param <G>
 */
public class ArrayOfSomething<G> implements GenericArrayType {
	Class<G> classe;
	public ArrayOfSomething(Class<G> cl){ this.classe = cl;}
	@Override public Type getGenericComponentType() {return classe;}
}