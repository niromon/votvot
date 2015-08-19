package org.deguet.tests;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.deguet.model.civil.NQPerson;
import org.deguet.service.ServiceInitial;
import org.deguet.service.ServiceSocial;
import org.deguet.service.ServiceSocial.BadEmail;
import org.deguet.service.ServiceSocial.BadPassword;
import org.deguet.service.ServiceVote;
import org.junit.Before;
import org.junit.Test;



public class TestServiceInitial {

	ServiceInitial si = new ServiceInitial(new ServiceVote(), new ServiceSocial());
	
	@Before
	public void deleteAll(){
		si.clearAll();
	}
	
	@Test
	public void testInitialLoad(){
		si.createSampleAll();
		ServiceSocial social = si.getSsocial();
		System.out.println("All people : " + social.allPeople() );
	}
	
	@Test
	public void testInitialSimple() throws Exception{
		Random r = new Random(1243);
		si.createSamplesUsers(r);
		si.createSampleQuestions(r);
		si.createOneVote(r);
		ServiceVote sv = si.getSvote();
		System.out.println("All Votes : " + sv.allVotes() );
	}

	@Test
	public void testLoginJohnLennon() throws BadEmail, BadPassword, NoSuchAlgorithmException, UnsupportedEncodingException{

		ServiceInitial init = si;
		try{init.createSampleAll();} catch(Exception be){}
		ServiceSocial social = si.getSsocial();
		for (NQPerson p : social.allPeople())
			System.out.println("All people : " +  p);
		
		social.signin("joris@deguet.org", "password");
	}
	
	@Test
	public void testFindJohnLennon() throws BadEmail, BadPassword{

		ServiceInitial init = si;
		init.createSampleAll();
		ServiceSocial social = si.getSsocial();
		for (NQPerson p : social.allPeople())
			System.out.println("ONE : " +  p);
		
		NQPerson p = social.findByLogin("joris@deguet.org");
		System.out.println("p " + p);
	}

}
