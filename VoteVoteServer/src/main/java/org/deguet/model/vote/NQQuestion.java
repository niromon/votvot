package org.deguet.model.vote;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;

import jersey.repackaged.com.google.common.collect.Lists;

import org.deguet.model.Identifiable;
import org.joda.time.DateTime;

/**
 * A question in the system, will initially be a proposition and maybe then a real question.
 * @author joris
 *
 */
@Entity
public class NQQuestion extends Identifiable implements Comparable<NQQuestion>{

	public static enum Status {Proposed, Accepted, Closed}
	public static enum Type {SingleChoice, Preferential} // YesNo is actually a SingleChoice

	public Status status;
	public Type type;
	public String question;
	@ElementCollection		public List<String> choices;
	@Lob					public DateTime acceptedDate;
	@Lob					public DateTime proposedDate;
	@Lob					public DateTime closedDate;

	public NQQuestion(){}

	public NQQuestion(Type type, String string, String... choicesArray) {
		super();
		question = string;
		this.type = type;
		choices = new ArrayList<String>();
		for (String c :  choicesArray){
			choices.add(c);
		}	
		System.out.println("Poll creation " + choices);
		status = Status.Proposed;
		this.proposedDate = DateTime.now();
		//this.acceptedDate = DateTime.now().minusYears(1);
	}

	public NQQuestion(String string, String... choicesArray) {
		this(Type.SingleChoice,string ,choicesArray);
	}

	public String toString(){
		if (question == null) return super.toString();
		if (question.length() <32) return question;
		else return question.substring(0,28)+" ...";
	}

	@Override
	public int compareTo(NQQuestion o) {
		return this.question.compareTo(o.question);
	}
}
