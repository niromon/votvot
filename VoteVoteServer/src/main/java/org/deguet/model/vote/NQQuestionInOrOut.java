package org.deguet.model.vote;

import javax.persistence.Entity;
import javax.persistence.Lob;

import org.deguet.model.Identifiable;
import org.joda.time.DateTime;
/**
 * A simple object where a person indicates if he thinks we should includes the question in the system or not.
 * @author joris
 *
 */
@Entity
public class NQQuestionInOrOut extends Identifiable{

	public static enum Opinion {NO_OPINION, INCLUDE, EXCLUDE};
	
	public String questionId;
	public String voterId;
	public Opinion opinion;
	//@Lob public DateTime timestamp = DateTime.now();
	
}
