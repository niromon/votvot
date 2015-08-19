package org.deguet.model.civil;

import javax.persistence.Entity;

import org.deguet.model.Identifiable;

@Entity
public class IDPicture extends Identifiable{

	public byte[] content;
	
	public String personID;
	
}
