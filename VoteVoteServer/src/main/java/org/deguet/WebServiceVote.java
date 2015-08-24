package org.deguet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.deguet.model.civil.NQPerson;
import org.deguet.model.transfer.C2SVoteRequest;
import org.deguet.model.transfer.S2CPreferentialResult;
import org.deguet.model.vote.NQAnswer;
import org.deguet.model.vote.NQAnswerReceipt;
import org.deguet.model.vote.NQQuestion;
import org.deguet.model.vote.NQResult;
import org.deguet.service.ServiceSocial.NoToken;
import org.deguet.service.ServiceVote.AlreadyVoted;
import org.deguet.service.ServiceVote.NoSuchPoll;
import org.deguet.utils.GenericWebService;
import org.joda.time.DateTime;

import com.deguet.gutils.vote.BallotBox;
import com.deguet.gutils.vote.InstantRunoffOnBallotBox;
import com.deguet.gutils.vote.RankedVote;
import com.deguet.gutils.vote.ShulzeOnBallotBox;
import com.deguet.gutils.vote.TidemanOnBallotBox;


@Path("/vote") 
public class WebServiceVote extends GenericWebService<NQAnswer> {

	public WebServiceVote(){
		super(NQAnswer.class);
	}

	// just used to deserialise the transit format coming from JSON with GSON
	private static class ListOfSetOfString extends ArrayList<Set<String>>{}
	
	@POST										@Path("/vote")
	public String vote(String json) 
	{
		// Todo take the vote intention and make two things

		System.out.println("About to register vote  " +json);
		C2SVoteRequest o = g.fromJson(json, C2SVoteRequest.class);
		// get the poll see the type and deserliase based on that
		NQQuestion p;
		try {
			p = Services.vote.pollById(UUID.fromString(o.questionId));
			switch(p.type){
			case Preferential: 
				System.out.println("VOTE PREF " +json);
				try {
					// got to interpret a list of set as a rankedvote.
					System.out.println("o.choice : " +o.choice);
					ListOfSetOfString lss = CustomGson.getIt().fromJson(o.choice, ListOfSetOfString.class);
					System.out.println("LSS " + lss);
					RankedVote<String> rv = RankedVote.fromListOfSet(lss);
					System.out.println("rv " + rv);
					o.choice = CustomGson.getIt().toJson(rv);
					Services.vote.registerVote(o);
				} catch (AlreadyVoted | NoToken e1) {
					return "AlreadyVoted";
				}
				return "Preferential TODO";
			case SingleChoice: 
				System.out.println("VOTE SINGLE " +json);
				NQAnswerReceipt res;
				try {
					res = Services.vote.registerVote(o);
					System.out.println("VOTE SINGLE DONE " +json);
					return g.toJson(res); 
				} catch (AlreadyVoted e) {
					System.out.println("VOTE SINGLE Arleardy voted " +json);
					return "AlreadyVoted";
				} catch (NoToken e) {
					System.out.println("No Token voted " +json);
					return "NoToken";
				}
			}
			return "TODO";
		} catch (NoSuchPoll e1) {
			return e1.getClass().getSimpleName();
		}
		
	}

	@GET										@Path("/results/{id}")
	public String results(@PathParam("id") String id) 
	{
		
		NQQuestion p;
		try {
			p = Services.vote.pollById(UUID.fromString(id));
		} catch (NoSuchPoll e) {
			return e.getClass().getSimpleName();
		}
		switch(p.type){
		case Preferential: 
		{
			System.out.println("RESULTS PREF ");
			S2CPreferentialResult result = new S2CPreferentialResult();
			// build a BallotBox
			NQResult res = Services.vote.resultsForPoll(id);
			BallotBox<String> bb = new BallotBox<String>();
			for (String vote : res.result.keySet()){
				RankedVote<String> ranked = CustomGson.getIt().fromJson(vote, RankedVote.class);
				bb.add(ranked,res.result.get(vote));
			}
			result.edges = bb.computePairwise().triplets();
			result.undisputedWinner = bb.undisputedWinners().size() > 0?bb.undisputedWinners().iterator().next():null;
			// apply shulze
			result.shulze = RankedVote.fromListOfSet(new ShulzeOnBallotBox<String>(bb).results());
			result.tideman = new TidemanOnBallotBox(bb).results();
			result.instant = new InstantRunoffOnBallotBox(bb).results();
			// apply Shulze method and get the result
			System.out.println("Tideman condensed   :::   "  + result.tideman.toCondense());
			System.out.println("Shulze  condensed   :::   "  + result.shulze.toCondense());
			String json = g.toJson(result);
			//System.out.println("\n\n\n\n\n\n\n\n"+json);
			return json;
		}
		case SingleChoice: 
			System.out.println("RESULTS SINGLE " );
			NQResult res = Services.vote.resultsForPoll(id);
			String json = g.toJson(res); 
			System.out.println("   >> "  + json);
			return json;
		default:
			return "TODO";
		}

	}
	
	@GET										@Path("/hasvoted/{pollId}/{userId}")
	public String hasVoted(@PathParam("pollId") String pollid, @PathParam("userId") String userid) 
	{
		try {
			NQQuestion poll = Services.vote.pollById(UUID.fromString(pollid));
			NQPerson person = Services.social.getByID(userid);
			if (person == null ) return "NoSuchPerson";
			for (NQAnswerReceipt has : Services.vote.particpationsForPoll(pollid)){
				if (has.voter.equals(userid)) return "true";
			}
			return "false";
		} catch (NoSuchPoll e1) {
			return e1.getClass().getSimpleName();
		}
	}


	@GET										@Path("/participation/{id}")
	public String participation(@PathParam("id") String id) 
	{
		List<NQAnswerReceipt> res = Services.vote.particpationsForPoll(id);
		return g.toJson(res);
	}

	@GET										@Path("/participation/daily/{id}")
	public String participationForGraph(@PathParam("id") String id) 
	{
		try {
		List<NQAnswerReceipt> res = Services.vote.particpationsForPoll(id);
		Collections.sort(res);
		Map<DateTime, Integer> result = new TreeMap<DateTime, Integer>();
		DateTime z = DateTime.now();
		UUID idid = UUID.fromString(id);
		NQQuestion p = Services.vote.pollById(idid);
		if (p == null) System.out.println("Particiaption Daily on null "+ id);
		DateTime a = p.acceptedDate;
		result.put(a, 0);

		// go through votes and count until
		int count = 0;
		a = a.plusDays(1);
		for (NQAnswerReceipt h : res){
			count++;
			if (h.timestamp.isAfter(a)){
				while(h.timestamp.isAfter(a)){
					a = a.plusDays(1);
				}
				result.put(a, count);
			}
		}
		result.put(z, res.size());
		return g.toJson(result);
		} catch (NoSuchPoll e) {
			return "NoSuchPoll";
		}
	}

	@GET										@Path("/votesby/{userid}")
	public String votesBy(@PathParam("userid") String id) {
		NQPerson p = Services.social.getByID(id);
		if (p == null) return "NoSuchPerson";
		List<NQAnswerReceipt> result = Services.vote.votesBy(p);
		return CustomGson.getIt().toJson(result);


	}

}
