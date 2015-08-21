package org.deguet.model.civil;

import org.deguet.model.Identifiable;

/**
 * Confirmation made by a user.
 * 
 * At that point there are two things that are confirmable:
 * - the link between a person and an email
 * - the link between a person and a group
 * @author joris
 *
 */
public class NQConfirmation extends Identifiable{

	public NQConfirmable confirmed;
	
	public String confirmerID;
	
}
