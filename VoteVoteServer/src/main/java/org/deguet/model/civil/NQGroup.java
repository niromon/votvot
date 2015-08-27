package org.deguet.model.civil;

import javax.persistence.Entity;

import org.deguet.model.Identifiable;

/**
 * Represents a group of people.
 * 
 * A link to this group can be confirmed or refuted by other people.
 * @author joris
 *
 */
//@Entity
public class NQGroup extends Identifiable
{

	public String name;
	
}
