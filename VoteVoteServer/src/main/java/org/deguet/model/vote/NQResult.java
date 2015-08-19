package org.deguet.model.vote;

import java.util.Map;

/**
 * Should hold results for both simple and preferential votes 
 * @author joris
 *
 */
public class NQResult {

	public NQQuestion question;
	
	public Map<String, Integer> result;
	
}
