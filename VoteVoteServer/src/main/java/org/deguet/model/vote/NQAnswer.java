package org.deguet.model.vote;

import org.deguet.model.Identifiable;
import org.deguet.model.civil.NQPerson;
import org.joda.time.DateTime;

public class NQAnswer extends Identifiable{
	
	public NQQuestion question;
	
	public String choice;

// DateTime was there but now is in the HasVoted
	
	public String toString(){
		return choice+" - "+question.question;
	}
	
}
