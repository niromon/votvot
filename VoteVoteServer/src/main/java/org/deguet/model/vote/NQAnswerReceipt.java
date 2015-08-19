package org.deguet.model.vote;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Lob;

import org.deguet.model.Identifiable;
import org.joda.time.DateTime;

@Entity
public class NQAnswerReceipt extends Identifiable implements Comparable<NQAnswerReceipt> {

	@Lob public DateTime timestamp; // will be set by the service layer inside the server.
	
	@Basic public String questionId;
	
	/**
	 * The voter's UUID
	 */
	@Basic public String voter;

	@Override
	public int compareTo(NQAnswerReceipt o) {
		return this.timestamp.compareTo(o.timestamp);
	}
	
}
