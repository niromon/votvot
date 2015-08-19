package org.deguet.model.transfer;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.persistence.Basic;

import org.deguet.model.civil.NQPerson;
import org.deguet.model.civil.NQPerson.Sex;
import org.deguet.model.NQPosition;
import org.deguet.service.ServiceSocial;
import org.joda.time.DateTime;

public class C2SSignUpRequest {

	public String email;
	public String firstName;
	public String lastName;
	
	public String password;
	public NQPosition adress;
	public NQPosition birthPlace;
	
	public DateTime birthDate;				//as millisec from Epoch
	public Sex sex;
	
	public static NQPerson convert(C2SSignUpRequest r) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		NQPerson p = new NQPerson();
		p.email = 			r.email;
		p.firstName = 		r.firstName;
		p.lastName = 		r.lastName;
		p.sex = 			r.sex;
		if (r.adress.getId() == null)		r.adress.setId(UUID.randomUUID().toString()); 
		if (r.birthPlace.getId() == null) 	r.birthPlace.setId(UUID.randomUUID().toString());
		p.adress = 			r.adress;
		p.birthPlace = 		r.birthPlace;
		p.birthDate = 		r.birthDate;
		p.password = 		ServiceSocial.hash(r.password);
		return p;
	}
	
	
}
