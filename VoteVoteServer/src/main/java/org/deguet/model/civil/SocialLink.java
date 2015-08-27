package org.deguet.model.civil;

import javax.persistence.Entity;

import org.deguet.model.Identifiable;

//@Entity
public class SocialLink extends Identifiable{

	public enum Type {Friend, Family, Professional, Other}
	
	public Type type;
	
	//@ManyToOne(cascade = CascadeType.ALL)
	public String userA,userB;
	
}
