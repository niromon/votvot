package org.deguet.tests;

import java.util.UUID;

import org.deguet.model.NQToken;
import org.deguet.model.civil.IDPicture;
import org.deguet.model.civil.Reference;
import org.deguet.model.civil.SocialLink;
import org.deguet.repo.RepoPerson;
import org.deguet.service.ServiceInitial;
import org.deguet.service.ServiceSocial;
import org.deguet.service.ServiceVote;
import org.deguet.utils.JPARepository;
import org.junit.Test;

public class TestServiceInitialHibernate extends TestServiceInitial{

	public TestServiceInitialHibernate(){
		System.out.println("service "+si.getSsocial().rlink.getClass());
		si = new ServiceInitial(

				new ServiceVote(),
				new ServiceSocial(
						new RepoPerson(),
						new JPARepository<NQToken>(NQToken.class),
						new JPARepository<SocialLink>(SocialLink.class),
						new JPARepository<Reference>(Reference.class),
						new JPARepository<IDPicture>(IDPicture.class)
						)
				);
		si.createSampleAll();
		System.out.println("service "+si.getSsocial().rlink.getClass());
	}

	@Test
	public void testHibernate(){
		ServiceSocial ss  = new ServiceSocial(
				new RepoPerson(),
				new JPARepository<NQToken>(NQToken.class),
				new JPARepository<SocialLink>(SocialLink.class),
				new JPARepository<Reference>(Reference.class),
				new JPARepository<IDPicture>(IDPicture.class)
				);
		SocialLink link = new SocialLink();
		link.setId(UUID.randomUUID().toString());
		ss.create(link);
	}

	@Test
	public void testHib(){
		si = new ServiceInitial(

				new ServiceVote(),
				new ServiceSocial(
						new RepoPerson(),
						new JPARepository<NQToken>(NQToken.class),
						new JPARepository<SocialLink>(SocialLink.class),
						new JPARepository<Reference>(Reference.class),
						new JPARepository<IDPicture>(IDPicture.class)
						)
				);
		si.createSampleAll();
	}

}
