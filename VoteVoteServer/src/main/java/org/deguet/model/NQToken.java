package org.deguet.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Lob;

import org.deguet.model.civil.NQPerson;
import org.joda.time.DateTime;

@Entity
public class NQToken extends Identifiable{

	@Override
	public String toString() {
		return "Token [userID=" + userID + ", expirationDate=" + expirationDate
				+ ", getId()=" + getId() + "]";
	}

	public String userID;
	
	@Lob public DateTime expirationDate;

	public static NQToken forUser(NQPerson p, int validityInDays) {
		NQToken t = new NQToken();
		t.expirationDate = DateTime.now().plusDays(validityInDays);
		t.userID = p.getId();
		t.setId(UUID.randomUUID().toString());
		return t;
	}
	
}
