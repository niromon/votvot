package org.deguet.tests.client;

import java.io.IOException;

import org.deguet.client.WebClientVotVot;
import org.deguet.client.WebClientVotVot.NotOnLocalhost;
import org.deguet.model.NQPosition;
import org.deguet.model.NQToken;
import org.deguet.model.civil.NQPerson;
import org.deguet.model.civil.NQPerson.Sex;
import org.deguet.model.transfer.C2SSignUpRequest;
import org.deguet.service.ServiceSocial.BadCredentials;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if flush and initial only works on localhost
 * @author joris
 *
 */

public class TestFailOnDeploy {

	WebClientVotVot wcs = new WebClientVotVot(org.deguet.tests.client.Base.di5a5);

	@Test
	public void testAllPeople() throws IOException, BadCredentials {
		// signup and signin as admin to have the right
		C2SSignUpRequest admin = new C2SSignUpRequest();
		NQPerson adminSaved = null;
		try{
			admin.email = "admin@votvot.com";
			admin.birthDate = DateTime.now().minusYears(34);
			admin.password = "admin";
			admin.adress = new NQPosition("",10,10);
			admin.birthPlace = new NQPosition("",10,10);
			admin.sex = Sex.Female;
			adminSaved = wcs.signUp(admin);
		}catch(Exception e){e.printStackTrace();}
		System.out.println("Admin log attempt " +adminSaved);
		NQToken token = wcs.signin(admin.email, admin.password);
		System.out.println("Admin logged as   " +adminSaved);
		System.out.println("Admin token is    " +token);
	}

	@Test( expected=NotOnLocalhost.class )
	public void testFlush() throws IOException, NotOnLocalhost{
		wcs.deleteAllFromLocalhost();
		Assert.assertEquals(wcs.allPeople().size(), 0);
	}

}
