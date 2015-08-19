package org.deguet;

import org.deguet.service.ServiceInitial;
import org.deguet.service.ServiceSocial;
import org.deguet.service.ServiceVote;

public class Services {

	public static final ServiceSocial social = 		new ServiceSocial();
	public static final ServiceVote vote = 			new ServiceVote();
	public static final ServiceInitial initial = 	new ServiceInitial(vote,social);
	
}
