package org.deguet.model.civil;

import javax.persistence.Entity;
import javax.persistence.Lob;

import org.deguet.model.Identifiable;
import org.joda.time.DateTime;

/**
 * Confirmation made by a user.
 * 
 * At that point there are two things that are confirmable:
 * - the link between a person and an email
 * - the link between a person and a group
 * @author joris
 *
 */
//@Entity
public class NQConfirmation extends Identifiable{

	public String confirmedID;
	
	public enum Type {BasicInfo, GroupAffiliation};
	public enum Answer {Confirm, Infirm};
	
	public Type type;
	public Answer answer;
	
	public String confirmerID;
	
	@Lob public DateTime date;
	
}
