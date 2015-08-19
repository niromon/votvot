package org.deguet.tests;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import junit.framework.Assert;

import org.deguet.model.NQPosition;
import org.deguet.model.civil.NQPerson;
import org.deguet.model.civil.Reference;
import org.deguet.model.civil.SocialLink;
import org.deguet.model.civil.NQPerson.Sex;
import org.deguet.model.civil.SocialLink.Type;
import org.deguet.model.transfer.C2SSignUpRequest;
import org.deguet.service.ServiceSocial;
import org.deguet.service.ServiceSocial.BadAddress;
import org.deguet.service.ServiceSocial.BadBirth;
import org.deguet.service.ServiceSocial.BadEmail;
import org.deguet.service.ServiceSocial.BadSex;
import org.joda.time.DateTime;
import org.junit.Test;

public class TestReferences {

	@Test
	public void testRefCount() throws BadEmail, BadBirth, BadSex, BadAddress, NoSuchAlgorithmException, UnsupportedEncodingException{
		C2SSignUpRequest p = new C2SSignUpRequest();
		p.email = "jo@blo.com";
		p.password = "hello";
		p.birthDate = DateTime.now().minusYears(18);
		p.birthPlace = new NQPosition("ici", 54.0, 45.0);
		p.sex = Sex.Male;
		p.adress = new NQPosition("ici", 54.0, 45.0);
		ServiceSocial sersoc = new ServiceSocial();
		sersoc.deletePeople();
		NQPerson person = sersoc.signUp(p);
		// to reference they have to know them
		for (int i = 0 ; i < 100 ; i++){
			C2SSignUpRequest friend = new C2SSignUpRequest();
			friend.email = "jo"+i+"@blo.com";
			friend.password = "pipo";
			friend.birthDate = DateTime.now().minusYears(18);
			friend.birthPlace = new NQPosition("ici", 54.0, 45.0);
			friend.sex = Sex.Male;
			friend.adress = new NQPosition("ici", 54.0, 45.0);
			NQPerson friendPerson = sersoc.signUp(friend);
			SocialLink link = new SocialLink();
			link.userA = person.getId();
			link.userB = friendPerson.getId();
			link.type = Type.Friend;
			sersoc.create(link);
		}
		for (int i = 0 ; i < 100 ; i++){
			NQPerson a = sersoc.findByLogin("jo@blo.com");
			NQPerson b = sersoc.findByLogin("jo"+i+"@blo.com");
			Reference ref= new Reference();
			ref.date = DateTime.now();
			ref.referer = b;
			ref.validated = a;
			sersoc.add(ref);
		}
		NQPerson ppp = sersoc.findByLogin("jo@blo.com");
		List<Reference> refs = sersoc.refsFor(ppp);
		Assert.assertEquals(100, refs.size());
		
	} 
	
}
