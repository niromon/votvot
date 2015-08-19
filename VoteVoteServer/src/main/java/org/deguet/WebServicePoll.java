package org.deguet;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.deguet.model.civil.NQPerson;
import org.deguet.model.vote.NQAnswerReceipt;
import org.deguet.model.vote.NQQuestion;
import org.deguet.model.vote.NQQuestion.Status;
import org.deguet.model.vote.NQQuestion.Type;
import org.deguet.model.vote.NQQuestionInOrOut;
import org.deguet.service.ServiceVote.AlreadyAcceptedPoll;
import org.deguet.service.ServiceVote.AlreadyExpressedOpinion;
import org.deguet.service.ServiceVote.NoSuchPoll;
import org.deguet.utils.GenericWebService;
import org.joda.time.DateTime;


@Path("/poll") 
public class WebServicePoll extends GenericWebService<NQQuestion> {
	
	public WebServicePoll(){
		super(NQQuestion.class);
	}
	
	
	@GET		@Path("/delete/{id}")			@Produces("text/json")
	public String deleteByIdViaGet(@PathParam("id") String id){ 
		System.out.println("Super classe sur Poll");
		return super.deleteById(id) ; 
	}
	
	public static class S2CNumbers {
		public int numberOfPeople, numberOfQuestions, numberOfPropositions, numberOfAnswers;
		
	}
	
	@GET		@Path("/globalnumbers")			@Produces("text/json")
	public String globalNumbers(){ 
		S2CNumbers numbers = new S2CNumbers();
		numbers.numberOfPeople = Services.social.countPeople();
		numbers.numberOfQuestions = Services.vote.homePolls().size();
		numbers.numberOfAnswers = Services.vote.allVotes().size();
		numbers.numberOfPropositions = Services.vote.numberOfPropositions();
		return CustomGson.getIt().toJson(numbers); 
	}
	
	@POST		@Path("/propose")			@Produces("text/json")
	public String propose(String json){ 
		System.out.println("Propose Poll " + json);
		C2SProposal proposal = CustomGson.getIt().fromJson(json, C2SProposal.class);
		// should go into the service layer
		NQQuestion poll = new NQQuestion();
		poll.status = Status.Proposed;
		poll.type = proposal.type;
		poll.question = proposal.question;
		poll.choices = proposal.choices;
		poll.proposedDate = DateTime.now();
		Services.vote.create(poll);
		return CustomGson.getIt().toJson(poll); 
	}
	
	@GET		@Path("/propositions")			@Produces("text/json")
	public String propositions(){ 
		return CustomGson.getIt().toJson(Services.vote.propositions()); 
	}
	
	@GET		@Path("/questions")			@Produces("text/json")
	public String questions(){ 
		return CustomGson.getIt().toJson(Services.vote.homePolls()); 
	}
	
	@POST		@Path("/support")			@Produces("text/json")
	public String support(String json){ 
		System.out.println("Support for proposed Poll " + json);
		NQQuestionInOrOut opinion = CustomGson.getIt().fromJson(json, NQQuestionInOrOut.class);
		// should go into the service layer
		try {
			Services.vote.addPropositionOpinion(opinion);
			return CustomGson.getIt().toJson(opinion);
		} catch (AlreadyExpressedOpinion e) {
			return e.getClass().getSimpleName();
		} catch (AlreadyAcceptedPoll e) {
			return e.getClass().getSimpleName();
		} catch (NoSuchPoll e) {
			return e.getClass().getSimpleName();
		}
		 
	}
	
	@GET										@Path("/supportsby/{userid}")
	public String supportsBy(@PathParam("userid") String id) {
		System.out.println("Supports for " + id);
		NQPerson p = Services.social.getByID(id);
		if (p == null) return "NoSuchPerson";
		List<NQQuestionInOrOut> result = Services.vote.supportsBy(p);
		return CustomGson.getIt().toJson(result);


	}
	
	public static class C2SProposal {
		public String question; 
		public Type type;
		public List<String> choices;
	}
	
}
