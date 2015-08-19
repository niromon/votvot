package org.deguet.model.civil;

import org.deguet.model.Identifiable;
import org.joda.time.DateTime;

/**
 * Holds the information of a user that certifies the existence of another user
 * @author joris
 *
 */
public class Reference extends Identifiable{

	public NQPerson referer;
	
	public NQPerson validated;
	
	public DateTime date;
	
}
