package org.deguet;

import java.util.Random;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.deguet.service.ServiceSocial.BadEmail;
import org.deguet.service.ServiceSocial.NoToken;
import org.deguet.service.ServiceVote.NoSuchPoll;

/**
 * This web service is only used for development and tests purpose.
 * 
 * Methods only work when call from localhost
 * @author joris
 *
 */

@Path("/")
public class WebServiceDev {


	private boolean isLocalHost(UriInfo uriInfo){
		return uriInfo.getBaseUri().getHost().contains("localhost");
	} 

	@GET 
	@Path("includehalfpropositions")
	public String includeHalfPropositions(@Context UriInfo uriInfo){
		// do it only for request from localhost
		boolean isLocalHost = isLocalHost(uriInfo);
		System.out.println("Resquest to erase everything. On localhost ?" + isLocalHost +" "+uriInfo.getBaseUri().getHost());
		if (isLocalHost){
			try {
				Services.initial.includeHalfPropositions();
			} catch (NoSuchPoll e) {
				return "NoSuchPoll";
			}
			return "ok";
		}
		return "only available on localhost for tests";
	}
	
	@GET 
	@Path("flushfortests")
	public String flushForTests(@Context UriInfo uriInfo){
		// do it only for request from localhost
		boolean isLocalHost = isLocalHost(uriInfo);
		System.out.println("Resquest to erase everything. On localhost ?" + isLocalHost +" "+uriInfo.getBaseUri().getHost());
		if (isLocalHost){
			// do the flushing
			System.out.println("Deleting people");
			Services.social.deletePeople();
			System.out.println("Deleting all votes");
			Services.vote.deleteAll();
			return "ok";
		}
		return "only available on localhost for tests";
	}

	@GET 
	@Path("fakeinclude/{pollId}")
	public String fakeInclude(@Context UriInfo uriInfo, @PathParam("pollId") String pollId){
		// do it only for request from localhost
		boolean isLocalHost = isLocalHost(uriInfo);
		System.out.println("Resquest to fakeInclude. On localhost ?" + isLocalHost +" "+uriInfo.getBaseUri().getHost());
		System.out.println("PollId is " + pollId);
		if (isLocalHost){
			// do the flushing
			try {
				return Services.initial.fakeInclude(pollId);
			} catch (NoSuchPoll e) {
				return "NoSuchPoll";
			}
		}
		return "only available on localhost for tests";
	}

	@GET 
	@Path("fakevote/{pollId}")
	public String fakeVote(@Context UriInfo uriInfo, @PathParam("pollId") String pollId){
		// do it only for request from localhost
		System.out.println("Resquest to fakeVote. On localhost ?" + isLocalHost(uriInfo) +" "+uriInfo.getBaseUri().getHost());
		System.out.println("PollId is " + pollId);
		if (isLocalHost(uriInfo)){
			// do the flushing
			try {
				return Services.initial.fakeVote(pollId);
			} catch (NoSuchPoll e) {
				return "NoSuchPoll";
			}
		}
		return "only available on localhost for tests";
	}

	@GET 
	@Path("initial")
	public String initial(@Context UriInfo uriInfo){
		// do it only for request from localhost
		boolean isLocalHost = isLocalHost(uriInfo);
		System.out.println("Initial load. On localhost ?" + isLocalHost +" "+uriInfo.getBaseUri().getHost());
		if (isLocalHost){
			// do the flushing
			Services.initial.createSampleAll();
			return "ok";
		}
		return "only available on localhost for tests";
	}

	@GET 
	@Path("initial/people")
	public String initialPeople(@Context UriInfo uriInfo){
		// do it only for request from localhost
		boolean isLocalHost = isLocalHost(uriInfo);
		System.out.println("Initial load. On localhost ?" + isLocalHost +" "+uriInfo.getBaseUri().getHost());
		if (isLocalHost){
			// do the flushing
			try {
				Services.initial.createSamplesUsers(new Random(123));
				return "ok";
			} catch (Exception e) {
				e.printStackTrace();
				return e.getClass().getSimpleName();
			}
		}
		return "only available on localhost for tests";
	}
	
	@GET 
	@Path("initial/questions")
	public String initialQuestions(@Context UriInfo uriInfo){
		// do it only for request from localhost
		boolean isLocalHost = isLocalHost(uriInfo);
		System.out.println("Initial load. On localhost ?" + isLocalHost +" "+uriInfo.getBaseUri().getHost());
		if (isLocalHost){
			// do the flushing
			try {
				Services.initial.createSampleQuestions(new Random(123));
				return "ok";
			} catch (Exception e) {
				e.printStackTrace();
				return e.getClass().getSimpleName();
			}
		}
		return "only available on localhost for tests";
	}

}
