package org.deguet.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import jersey.repackaged.com.google.common.collect.Lists;

import org.deguet.CustomGson;
import org.deguet.Services;
import org.deguet.model.NQPosition;
import org.deguet.model.civil.IDPicture;
import org.deguet.model.civil.NQPerson;
import org.deguet.model.civil.NQPerson.Sex;
import org.deguet.model.transfer.C2SSignUpRequest;
import org.deguet.model.transfer.C2SVoteRequest;
import org.deguet.model.vote.NQQuestion;
import org.deguet.model.vote.NQQuestion.Type;
import org.deguet.model.vote.NQQuestionInOrOut;
import org.deguet.model.vote.NQQuestionInOrOut.Opinion;
import org.deguet.service.ServiceSocial.NoToken;
import org.deguet.service.ServiceVote.AlreadyAcceptedPoll;
import org.deguet.service.ServiceVote.AlreadyExpressedOpinion;
import org.deguet.service.ServiceVote.AlreadyVoted;
import org.deguet.service.ServiceVote.NoSuchPoll;
import org.joda.time.DateTime;

import com.deguet.gutils.vote.RankedVote;
import com.sun.jersey.core.util.Base64;


public class ServiceInitial {

	private final ServiceVote svote;
	private final ServiceSocial ssocial;

	public ServiceVote getSvote() {
		return svote;
	}

	public ServiceSocial getSsocial() {
		return ssocial;
	}

	public ServiceInitial(ServiceVote sv, ServiceSocial ss){
		this.svote 		= sv;
		this.ssocial 	= ss;
	}

	public void clearAll(){
		svote.deleteAll();
		ssocial.deletePeople();
	}


	public void createSampleAll(){
		Random r = new Random(1234);
		try {
			// base objects
			System.out.println("Creating all ");
			System.out.println("Creating people ");
			this.createSamplesUsers(r);
			System.out.println("Creating polls ");
			//System.out.println("After "+ssocial.allPeople());
			this.createSampleQuestions(r);
			System.out.println("Creating votes ");
			createSamplesVotes(r);
			System.out.println("All votes "+svote.allVotes().size());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void createSamplesVotes(Random r){
		System.out.println(" >>> Getting all people");
		List<NQPerson> all =  ssocial.allPeople();
		Collections.sort(all);
		System.out.println(" >>> Getting all polls");
		List<NQQuestion> polls = svote.homePolls();
		Collections.sort(polls);
		System.out.println(" >>> Start the votes");
		for (NQPerson p : all.subList(0, Math.min(all.size(), 100))){
			for (NQQuestion poll : polls){

				// create vote
				C2SVoteRequest v = 	new C2SVoteRequest();
				v.voterId  = 		p.getId();
				v.questionId = 		poll.getId();
				if (poll.type.equals(Type.SingleChoice)){
					v.choice = 			poll.choices.get(r.nextInt(poll.choices.size()));
				}
				else{
					RankedVote<String> ranked = 
							r.nextFloat() > 0.8?
									RankedVote.atRandom(r, poll.choices.toArray(new String[ poll.choices.size()])):
										RankedVote.fromCondense(poll.choices.get(0));
									//System.out.println("Ranked "+ ranked);
									String json = CustomGson.getIt().toJson(ranked);
									v.choice = json;
				}
				DateTime forcedDate = DateTime.now().minusDays(r.nextInt(365)).minusMinutes(r.nextInt(60));
				try {
					svote.registerVote(v, forcedDate);
				} 
				catch (IllegalArgumentException e) {e.printStackTrace();} 
				catch (AlreadyVoted e) {} 
				catch (NoToken e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void createOneVote(Random r){
		System.out.println(" >>> Getting all people");
		List<NQPerson> all =  ssocial.allPeople();
		Collections.sort(all);
		System.out.println(" >>> Getting all polls");
		List<NQQuestion> polls = svote.homePolls();
		Collections.sort(polls);
		System.out.println(" >>> Start the votes");
		NQPerson p = all.get(0);
		NQQuestion poll = polls.get(0);
		try {
			createOneVote(p,poll,r);
		} 
		catch (IllegalArgumentException e) {e.printStackTrace();} 
		catch (AlreadyVoted e) {} 
		catch (NoToken e) {
			e.printStackTrace();
		}
	}

	public void createOneVote(NQPerson person, NQQuestion question, Random r) throws AlreadyVoted, NoToken{
		// create vote
		C2SVoteRequest v = 	new C2SVoteRequest();
		v.voterId  = 		person.getId();
		v.questionId = 		question.getId();
		if (question.type.equals(Type.SingleChoice)){
			v.choice = r.nextFloat() > 0.4?
					question.choices.get(0):		// most of the time the first choice
					question.choices.get(r.nextInt(question.choices.size()));	// otherwise random
		}
		else{
			RankedVote<String> ranked = RankedVote.atRandom(r, question.choices.toArray(new String[ question.choices.size()]));
			System.out.println("Ranked "+ ranked);
			String json = CustomGson.getIt().toJson(ranked);
			v.choice = json;
		}
		DateTime forcedDate = DateTime.now().minusDays(r.nextInt(360)).minusMinutes(r.nextInt(60));
		svote.registerVote(v, forcedDate);
	}

	public void createSamplesUsers(Random r) throws Exception{
		String[] first = {"arnaud", "joris","malcolm","évariste","alexandre"
				,"paul","ringo","george","leandre","andrée","madeleine","camille","isaac","clément","norbert","mick",
				"marie-claude","jeanine","emilia"
		};
		String[] last = {"deguet","morin","dupont"
				,"lennon","mccartney","star","harrison","leduc","levasseur","valtrid","delury","richards","jeiger"
		};
		for (String f : first){
			System.out.println(f+" ");
			for (String l : last){
				for (int i = 0 ; i < 3 ; i++){
					C2SSignUpRequest p = new C2SSignUpRequest();
					p.email = f+ (i!=0?i+"":"") +"@"+l+".org";
					p.firstName = f;
					p.lastName = l;
					p.password = "password";
					p.sex = r.nextBoolean()?Sex.Male:Sex.Female;
					p.birthDate = DateTime.now().minusYears(r.nextInt(77)).minusDays(r.nextInt(365));
					p.adress = new NQPosition("ici",45.0,45.0);
					p.birthPlace = new NQPosition("ici",45.0,45.0);
					NQPerson person = ssocial.signUp(p);
					//					IDPicture pic = new IDPicture();
					//					pic.personID = person.getId();
					//					pic.content = Base64.decode(
					//							"/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAUDBAQEAwUEBAQFBQUGBwwIBwcHBw8KCwkMEQ8SEhEPERATFhwXExQaFRARGCEYGhwdHx8f" +
					//									"ExciJCIeJBweHx7/2wBDAQUFBQcGBw4ICA4eFBEUHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4e" +
					//									"Hh7/wAARCADIAMgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQID" +
					//									"AAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlq" +
					//									"c3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3" +
					//									"+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEI" +
					//									"FEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImK" +
					//									"kpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3" +
					//									"UR8D5gSeuOlMkj4OVb2p0SyN8j3DknsoC4/Kq72q5b55uT03kVwvRHo2LCbggBUk+uKbJC+dwQH60x7RdqjfOQR18w8frQLfBAM85B7e" +
					//									"YaW4h6rJ5mCqDGMe1TIjl87tvPaozBNG6lHYj/bkb8RU0JlZvmWMc9dx4/SmlYokMS8EzScn6UyOIB8rPJgDu1SSLc7SoMQUHryagPmk" +
					//									"7fMiB6cLmtVvYRZWAOeHJx23daFtlzgpyTnJJ4/zimSiYQhvtCqR22AV5t8SfGt3oukz3X9sJZRqjCMllDSMAeFHU9PYfWnOqqfQVz0H" +
					//									"U302xDS3s8FsMgBnk2gn8eDXlfjL4zeFNDv5dOFvczzxHDFen4c18x+JfF2v61qklzfazdXnJCmRyQB7KeB+VYjyq8xe6lZ36c881hOb" +
					//									"lsWoJs+mIfj54YR4vP0qQxN98qc7foDjP+etbVr8c/h1IP3r3cLAf8+zEfpXyz/aVikW2O2DOeGklwePZelOR7GdR5sTIx6sEUVlzNDd" +
					//									"JH1LH8a/B11dmO33xQYyZZwEJPsoya6bS/GvhzUwqWmp2hd8YQyqpPpwTXxjdWKbRJa3Sk94+QVqO1jvlnBUPuHoc4/KkpvdsTpH3bLG" +
					//									"soQGNfm4LYzkVVlsIo/3pWVCpyPLYr7DhTXhfwk8eNp/k6frl+8docKhkYkRk9wew9q+hbR47q1Dw3AkTAKvG25TnvinG0zOUOTcqr5K" +
					//									"hWElwuechif608zRgYLrIuc5K8ineVJAclt4PXAAokitpU+aLJOO3etVorGY2WFGyVkI4yQMCqE0Q8z5bgow6kNj86u/ZbAKw+yRkjrh" +
					//									"BxSfZ7NzhrcEVnOmpBcp21xcqdsVyJBnHUZpxuCXKzRMG/vAY21NLpOlSqC1pExB7jJqsdC0yUjbZJweCOo9x3BpckrBc0/D6K/iLTnH" +
					//									"a8i5K4/jHT/61FO8NWMcGuacRLcj/S4jhpmYH5x6k0V2YaPKmY1dzXQBQCxKnPHoaZIdxPPXr7U8pt7BuPypm9VwAwFRJWSOtD32MFIV" +
					//									"g3qOlRGMBshmbAzgmrXyvD1HSoc7ZCSB/s+lDG0SLGvyuNvuMZzTJl/ebs4I7BasQ7DGCNpBPHNDIQTlfyFWo3G2NySrZBwTnNU764gs" +
					//									"4Wmnk2rGM/Xj0q+RlSSeuCK8g/aRv7vS/DhuE1J7VGUrsXhpORwD/wDWpVXyxuhHMfF74xSWMr6XpMhMg+/t/gPPU+vTpivnrXdX1DWr" +
					//									"sT3lzNM5PG5s4+npWdcXLzXDyueWbPXNNWRge2fWuSzerNYpFpLLZHuUmRumMZGf6VSktpIztYEMeTWha3lztRIY8hQeOvXvV+1tmdi0" +
					//									"wLMx656VHPbc0VNvY54RkAk9asRK5GQwHfGa6A6HKX2RxM244UVBLol4JfLSIq68EjvS9tHuUqMuxjxsyAEnOT1PWrEVwpIym3H8QNad" +
					//									"rol0043xlR3BFXr3w6sUSNGcSZAaPr+NKVWHcpUp9iHS7tJUEEyiRFzs7MPof8a7b4ceLdb8Jaqht3kudLcgzWsh4RfUZ6flj1rjP7Iu" +
					//									"LWUeU24deeK3dLu451jjudiOowH6ED61i6lneJfsVJe8fVfhjxDpviPTvtdlIu5eHjYEOh9CD0q9MpUhhtII4wOlfKmi+KtW8L+JINSi" +
					//									"keSMELKmflljHY/h0r6a8O63ba5pMV7bEGOTkDOT9D+ddlOpzx1POqw5XZGgqsAxyoOPWoHWUEtgHParSqOpXC/SlOeSuGHrW6jfQysU" +
					//									"jMIQAysuT6VBJeASgojcnqK1HQvgMin+lUZIYw4+Ur1rOV09AsizoEsz+INLXY2PtcfXsN4oqbQ2jGvaaq53G7i7f7YoroodTGruakQQ" +
					//									"xgqxPqCpFRXABUFQc5yOcYpE8wxE+YyMD2UGoHEpDb7uQBR0GBUzl7qOst2+5oQCGzx0p4iZtxxj+VUZLfG0GSZyR3kPFWEtrckl4+Qe" +
					//									"oyalO6HcsxLiI8J+YP6ZpBCM9cZ7ZNNiNouQWCZ6biRmpUNsST5yE+xzzWsUwBoXWMEjI718qftf+JftOv2fh6A4W0TzJcY+83QH8BX0" +
					//									"/r+p2ej+H7zVrieLyrWF5WG8cgD/ABxX5++Ndam8ReKdQ1q45e6mL9eg6AfkKzrWvYcdWZlshZckdKt2ll5smX4XPaksVB4PSte1jw6g" +
					//									"evNck6nKdFON2bei6G88AWGJtpxnAFdPa+GQk0JeIhDjkjvWh8Nik1yiNwVA47Hn+detpocF4w5QbMYrwsTiZKVj3KFCPKmcroHhqCeS" +
					//									"1XyUO6Rg3yjIwP8AECuuX4eaXLDG/keXNgZOO9dFoWkJa5kUD5SDwO/tXSKsZi3FQcHv2rlpxnN3uXVqxi7RR5Wvw6i80pJGJUHXA5qL" +
					//									"WfhLFOUmtJmSVRz8uO3f1r2Wxt4mfKjkir0sMezp3711U8FUcebmOWrjUnax8uX3w6udNvozclprY53BeMe+PyrG8a+D7my0xr21jSVY" +
					//									"jn5V5249a+pdQsopc5jVgPXmuY1zTrd7WaJkUBlxjGK551KtKa5nodUJ06sbWPkuKZJrElA44weeAc+54+lesfs3+KJ5PtPhq5EZMC+b" +
					//									"bMy/NtyMrk9R3rz7WdNi0jxPe6YyjyLkEKpH3X6gfpgYo+GVwmn+NrG8trgq0cvlzRkkZRuMg/iK9qhUWjPDxMHZ+R9Wu9wqNm3BPGDu" +
					//									"4/SqT3U8bHNq4z33Vo2seQWGOnQnJzTjOFYiRk6ddvNeootq55pn/bpSNyQJ15yxJpJbrcVLyRxgnsmSKvlbaUffQOe+MVWvoLfIwVxj" +
					//									"HI9TVSi7AWtAjxrenP5ryZuoiN3pvFFWdESFdZ08owz9pjHXP8YorWirJmFTcc8jqi5K9e1V5ZWIIaNtp6nA4rQCMAMoCc8dqqOpFwfL" +
					//									"Axu6HnHrXJVVjuSBUEjKW4I46/0qeNFRmUgDPAGOhpQrNIN2MqcdPapsbQoKuRjPQnB/CtqaugasJEhxgArkgkihFTcSFHGTnHNSGWIA" +
					//									"ErPnj5RE2f5UwSqZMLbzMSeuAv8AM10cisI4T9oRZG+EmsiH5mWHOQOcbuefxr4ZK4JzweK+/wD4qrNdfDzXrePT5nLWMgA3Kf4c5618" +
					//									"Cbd0hBHX+n/665qytLQqBashtYN/k10NlFuII71z9u23avfPFdboEAkHzSAcjvXDWudlK2zOx8EQzWtzE6ITuIzXvWjQN9mjK8sygkep" +
					//									"rx/wmNsg+UMVwcV7H4dv4ngRG27x29K+exKblqe1QdoaGvY3DKgRlKnPANakUhSDlck9RWTdlFkiKnljwBWhaZYqHccjj1qaTd7EVIp6" +
					//									"mzpjNtXDDOOlXS5YkMBle3aqemCNFy0nJ5xVxwDkqRkHk5r26UZKmeTVXvlG5IwWGOnFc14hTfbSFhyVPSukum4PBx6HrWBqjb4pApyQ" +
					//									"ucYryMb2O/Ddz5n+JlqP7cEith1IdcDB454/KuHvZ5rPXEuLAnfDcKOO/Iwa9A+KOoWtt4nRQcMiFWHXqMV5vaXOPEEDYDI86KyHkEbu" +
					//									"n8/zr1MGn7NHLi3Zs+2/Ds32rS7a5ZQrPChYd8kdKsXKxITucLnnng1HpEIWyjQOWUIuNhxxj2qaSGIEMqAj1xmvfivdR4zRQIg88lZY" +
					//									"CSo5GM9arXltBkS7iSxVOuen41sjypmKbAX28jPPtWfIQxVGlEbbhjdgD6cipmrCNDw9FENXsXicsPtEeeefvDtRS6J/yGrDcMH7RHgG" +
					//									"Pn7worWnsYVNy6ftGOJUyP8AYz/WqLmcsRvTPXDRf/XrXMQKnbyD2K1Q2lbkpICpGegrmrQtZnfcji87z9rSIOB91cHNX4o8jezNIGGc" +
					//									"E1VXHmszEYJxnOOlW4iNm1G3dhg5qqC1B7E+CBgHG3+HHBquwHmbvmCe3rUynvuxn1qOQ5JVtuCMZx2rtIK+q2r3dhNCHK742Uc8cjGD" +
					//									"ntX5565ZPp/iO+sJl2vDcSRsB0BDEf0r6t/au8Y6h4b8NabpWkX0tpdX8jNI8TFWESjpkcjJIr5LklnudQ8+5keWVz80jnLN7k9zXFiJ" +
					//									"Jy0NoxsrkU25GIBI57VNa391bkFJD+VDxhpTnIwaakYlbaqE9cZrkaNdjotG8b6lp5UrMcAcg969G8G/E8T3scN0yplshhwPpXiqWTyF" +
					//									"gEVW2ghckFssBx275+gNJdWt1pt60MyvFJGeVPGK5auFp1EXSxE4yPr6w8VR6v4tsLK2cSL5OWHvXQeKvFFvoNxiXOVUHB4Brxr9laFr" +
					//									"/XmvZ33GLjJNevfGPwjLrdu09rEC8cZJHdvSvHqUPZy07nsQqqdkzzrXfjxNY6iotLZJYs8jJBq9oHxwa8kA37GbjY3+NeLar4WvC1zc" +
					//									"NEWET4wx2jJOABWp4A0K11ORI206W6uTM0XkW8rJMu0Fiw3DaeAe/pXowpxcbpnFVk4ysz3m0+JrXF2I2iXyjxyec11ml6jFqtoZot2G" +
					//									"yfmHI/GvJ/BmjWX9rx2scrSIx+RriLa6ODykg7N+hr2LTdLSwtQisME+mAa8rEQ59uh1wtBK58s/tFWMlh4x37cJMgYbfqa4nTYpYdSs" +
					//									"riLYSJlcDryCOv5V65+1Lbf8TSwuAP4NmffPSsr4W/C/WtVng1q5hMdjBiZELfNNg5wPQcdTXrYWqoUItnBXpurUcUfVGiZk0mGQ8MUV" +
					//									"sj3Gf6mrTANHtxz6YxWD4J1salo5ea2+ySRTGB43kHBXjGRxjpW+xZjlE3DHVSG/UV72Gqxq01I8yvRlRk4y3RVjQicttwx75HT0rN1H" +
					//									"ygyu+Dkgdzg54rYRwSxRBkDnnNVLmGJjvRWHI681pUjokYD9DVRrNiq4OLmPv0G4UVJpCbdXsXOCftMfT/eFFENNDGruaWY2X5l/XFUZ" +
					//									"VRZ8iRwjYI+Y1YUFcElQMd8Uy8AOw/aIhg4OWAqK1nE7EMj2b9wlkIHGNualMyRlS8cp/wBpFBP6VCZURwDdwqQOmRTxIjDIkZ8ggYBI" +
					//									"rGlK0insS/arXGHacfVDQ95aZGPMIxnlDVeVedu6SPJBGIv/ANdRCO3YYa7w3PDHaa6+eTM9T5Y/bAjvv+FiWt1LvNjJZKLbPQYJ3D65" +
					//									"Of8AgQrxiAZuowegFfXX7WPh2G8+GQ1aJMzafcIwYHJ2scEfqD+FfI1oy/aEYkctg/SuCtFxmzpjK8LFu2jEkhVj1NaNlZXVjdbxA0sb" +
					//									"cgrzVWyX/Sz7N0r0bQ7yJbNYzApbbyccmuOtUlE6KVPmMK3a1jj8xdNPm9csgwK5bxIxubsthVbqx6n867nVt8rMYQQvSuA1VsXjpkZ5" +
					//									"zzU0ZczLqx5Ue1/spGQapcxx8AAEnFfUE0LXCsgJ6YyD2r51/ZHtlWa4lkwuWxzX0lFIqTt0A3H6VyypqUpN7XN6s2lHlPJfFfgfW01J" +
					//									"7yzhiurd+WQEK3HsRg1p+BNIhs5dz6AILrkGTygjHPXkV6oohkUEMNw9KjSHEhLISvUYHSrWCd1yMTxzlG0kc/Z+GbTzpJZbaFCxDIUH" +
					//									"zfjV+6hSKExgADtx0rYfaqEgc+tZGruFhZ+eB1qcVh40IabmVKpKrPU8A/aIt4zqWkzTAtE0uCPXByR+tey+B7/RL7QLe409grSR7Sh6" +
					//									"r7YrzvXtAfx94ytdLIP9n6cxlvJAenog9z/StrT9CufDHiBNPtZfNhBDIcYO33rihOVOkp2uj0Y0oVJSV7NanReGbeW2GoIUUI127hv7" +
					//									"2fWt3y43XlTDJxhlbikUbF+QD7xJGR1qXbEYyrlx3yRkfpX1WCo8lFHg42p7Ss2VWDAsskm7tlgM8e9RNGjsCpOemQc/pVt4AUBRwQM9" +
					//									"DkfjVd7crJvUc+o6V0SicYaWSmuaem/j7RGPr8woqC0t0fxfpdywO+KZUCjocuOT+VFTDdmNXcsCKEHBhDHHoCKc1tbsMmCJWz1CAUiZ" +
					//									"B6hsD+GnqcDkZBP5VG52ETwRpsO1CVJ/hHFWY9gAIJ3EY69KilIKMu3kjg5qOF/lHY4xWUvdkMuswHf+tOBDqQcHNRB16MTx3FKuBwD1" +
					//									"P410p9SfQzPHWhQeIvBWqaKyj/SrZ0U91bBx+tfn/fWsljdTWtxE0c0LlHVuCGBPFfoypwoBJU/p9a878c/Bjwd4u1j+1byKaC6c5nNt" +
					//									"IEEvu3B59xg1nXpuWqLhKzPjSOULOsq/xfzrrtKvAUDjJ4xjNYvj/TYtF8ZavpUSFIrW7dIweoUZ2/pUWk3pG1C2DnFeZVjdXOyjNI6n" +
					//									"UbxbexaRyMkYrz+WUSTPOSMM3f0rY8VXTMywZIGMkDtXP7TyAfwqKMLI0qy1Pob4DeMNA0rTI4p2C3UknJ7Ee1e0J8RPCH9o/ZZNWt4p" +
					//									"nbAjLZOfwr5f+FOkaSsQ1DUUm3wOGAX7pFfQ1jJ4N1m3thPbWzurDy5MKG3DoN1efVlyTaid0FGrFc252k9y8Z+0xu3ksAdwrodLvo7i" +
					//									"3U5z/WsWz1DS7m2a23QNEgEZXcCPpUen2rWEj/ZJjJAeQCeVq6FaWHlzJ3TMa0FUi1JWaOku5lCZAU5HI6Vynia7MVnJ0YhSfyrRu7sm" +
					//									"IsCvHt1rkfFt04sZmweQFBx6n9Kwx+J9q7IWDw6i7st/D+FLfQGEOEnuXaWeT+Ikng/l+VdFPaxi6MuFLsBmVuuPQelct4X027sxbyW9" +
					//									"yo+0w7nWRcqccZB7Hpx9a6s8jEjBiOD2H4V34PBTlFKa0MsTXhFtxeo2Xcg5Xr6jNLFNblgsignPNJKiMNok2/jURs5mwdyMfUGvoIRc" +
					//									"djyG23dl8W1m4UqSpJ6qTUM1rhtqysR1+brVOb7ZDgGIFfZqIr192WVhj3rVyjs0TcuaZC39s2fmMTi4jIwPcUVLpFyr6parkEmdOc/7" +
					//									"QorJJLYxrbozgw6liaUMCxbBGPSo2Y9SXDdemOKWJyfukH+tc0d9TqHCSMrjpjr9KYTkZDsPRev86c2fmAQg+opjq5O8KcjrmplqwuKs" +
					//									"8qEZjJUdCFxu/WrK6jYmTE8vkSHtICoP4moArf3e3IqVYmf5iO3T1rWMmkK5ft41kTdbypKvqrZGKk8mXZtYMO44rOFnAuG8oD3AwakV" +
					//									"CuNjOvPUOf6VtfQE2fHP7Tlj9g+LmosBxcxxzZxjOVA/pXmlvJskEnXac17r+2Pp1wmv6RqhDtHLbtC0mc8q2QCf+BV4EnDbR/FXnzST" +
					//									"aOiEtC1PO1zNvZuT7U+O2cvkg7c8nFJHb7Y/MDD3FSwRSPID54CnHvisvI3S5mj1j4e6b/bWhSaZZ6naQzruJSRvnIxxj+X412/hbwdP" +
					//									"ZwR2s2q2gnHLAS9j7+teW+EfBHiOfUIrqzurTC4YFmI/lXs+k+FNVurWGS91mC1A4cW0Qdie3LV5dWDcvdZ7+HoRULz0NNfDWtWlgsVt" +
					//									"dRXGMZAbJYZ457Yq/wCHNX1ez1lLSdAIyBkP8rY6fzq9png23lVZBqWoowHDCTYW/ADGK57WhfaV4ljmlV3t2ZY2cnPGetcVaMqdiJcs" +
					//									"rpO56JqkgiiLHn0GcVxvi+VhotymcDaWBINbH9p/a9WlgIYwoi4bHHPasbxckN/dWukRpvluJVR1zxs3DP8AKuWEHOpFGfwRZ33haa2h" +
					//									"8PWMJOSIEySc9q0j9lcH7uRWbH4cijiSNHIRQAAGb/Go28NKXJ8z/wAePP51+gU1OMUrdD5icnJtmoBDkAMCo6j0qWKO3cD96PwPNYU/" +
					//									"hxN3J3cdzzTP+EbUciFmUjICyEVopzv8Jm2dFLZy7SUn3DsCAaoywMGBMan8Ky10q7tzut5tSix2FxuH5Nn9KfHLqJQrK87lTw2wc/iB" +
					//									"SnNdUNM1NJCDVrTK7G89Bj8RRVTRC761ZtJJID9oThlHPzCipTuYVXdmQsLIG8qZ4zngg9PT2p8f2tV+aaGQf7cZB/Q/0qFxG3MdwXGA" +
					//									"RjIqM/fwoKvg8lQa4OY6SwbiTdhXBI4KkEY/E0K+8Dcz89c8ioSlwwI80KQeoQHmmvHcp8y3eFB5/djp+VHNrcNS2yBkPKkg/wB2lSNc" +
					//									"hgB0/CuZ13xZoeg5fWPE1taKOzOgY/8AAQM14z8SPj2Sr2Hgx7k9jfTYHH+whH6n8qrnEk2fQ+ta3pOgaa95rF/DZQRrkvJJjP0HUn6C" +
					//									"vMbv9o7wXbak0EVpqtxADjzkVQD7gFga+VtZ1zWNauDcarqd1eyc/wCulZvwHaswsUbGOn60OTNoxPqj4veOvAXjv4UXF1FfxwTJLttk" +
					//									"n+WbzFGcbBk4569Oe1fMCOAc9cdKpljzzVmNt0aMB25qHvdlR0NmzlEluccsP4faiLeDviHTnArMtpWikyvX0rqvD4tJZW+0HZ8uR9a5" +
					//									"qi5dTpp6s67wj4uvNNtUhtzhxjblQQPXNey/DjxNq+opGjWRKMc7gu0AV5J4MtNKubhVeFpSXAQAY/E19BeBm02ysxCIxAyngFh83uea" +
					//									"8au05WWh7FKc1DXU6m3naO3V5wFzgEhulcb47eNoTIzjYfmRmJGWHY9vQ/hXS6zqNswS237WYAqBzkV5h4z1+1ubgWySFkDYKngqRxXJ" +
					//									"O8mkmVTjb3mb1jqFppmhJdXk3mMykySY6Z7fyqX4bWEmq60devFuYYZAVthJwdvPzY6j2+tc7pVhN4tubaBxKmi2zBsfxXBHv6Va/aA8" +
					//									"QXnhPwdZXGjzfZ7hLyIQkdOMkg+xAxitcHFQrRvvcwxMuaDsezDT4Ov2iYA/9Nm/xp5sYwCQ0rgcf69v8awfht4lsvGHgyx1y38sNMu2" +
					//									"dAPuSgfMPzrqIUChtuxhnJ2npX38EpK6PnShPZQnIPmYA5AmY/1qEWCZJSW4CAYyJ24P51oylcc5BpnlkJ8hxkZJAzT5YtiauUGspCuB" +
					//									"eXIx/wBNTVdrR13YuZwfqRWugYAhioLcjNQSox3EDI9vWpnTRKRS0dGGuWX75n/0iMndyfvCip9MULrNkM4IuEx3J+YUVikzCruYiKzK" +
					//									"W2nIHHeqqBlnADMh5zk5FPQN5QcSKB2NcV4s+JnhTw7K8OoXqT3A6w237xh7emfqa8/S1zq1Oo8Q6na6Dot3qt+4SC2jMjMD94en48Cv" +
					//									"kbx58T/Evia/mc6lcWdluxHbQyFQB2zjkn612fxW+MVj4n8NXehafpUywToh82ZtrIwYHovUcCvEQRIuQAO3+fzpXuWkOkk3ku8jOxPJ" +
					//									"JyT+dRghR1puChOKbyOuKrQ0SHhup71GeTnJpD1oFMLWADJxVuEBUC56VWUfNntU0X3u/NRIEOcYPy1e065YN1GB3zVbbuGKSOMhsrye" +
					//									"5qGrotXTO40DVZbfDRSlHGDnNegeH/F01np3lLIfNYlid27n6V5Ho9pd3ShIzjAOK6vw34Yv9QuUgW8MJBzke1efXpU+p30qso7HoFx4" +
					//									"5lliSd5wGYdCemMdBTvDGh6p4o1T7XNbMunbs5b5TLzng+mSOa6TwZ8LtDg8u7uUe7lIBzIDgH1xXquk6ZDY26LEgXA6DpXmtx2po6ed" +
					//									"vWQ/QtOg0+zWKNUQKAuFGBivFP2wJceGdIjU4LX2cfRG/wDrV7jLIkEUk0m1VQZJP0r46+O3jSTxb40kjhkLafYkxwDPBP8AE38q0wdN" +
					//									"yrLyMKzSg3fc7D9l/wAeJ4a8T/2FqVxt0rUyqjf92KbPyn2B+7+VfXrK2AVCngZJHGK/OGFmCqy8EcjHWvq/4J/G7QbrQLHRfFOofYtT" +
					//									"gQQiefISYD7pLdjjAOa+wwtZRXLI8WS1ue0lWfClwPoMUxYh1O88/wB9hx+dSxTQ3US3FtKk0LjKPG24MPYjg077qnewPPOTxXXF32FY" +
					//									"gktxuBjeZT14kJ/nUTLc8skyuM/xxjP6VbiLEA7hnHSoGGNwZgRyfcUNCZV00zvrtlmOH5Zk3YJUj5hzg0VLpjlNctkkAyZlwQMfxCiu" +
					//									"fQ5qu5+f+reNfFOqxg3+uX06xnKx+aQM9jgVzrSu8rMXZs8hi2Sfxq3q1s9nqlzaMMeVI0Z/A4rPlUg7gOM9q861juSuSLIVYOMHHbr+" +
					//									"FQ3CrFc/u/8AVPhk+n+c/lTxhgCOBio7n7qn0otqWkLKuV3AVA33RgHmrdsQ8eKilTn0xTEmRqpZCcc1GBzirVsMvtPGagYBbhhz1pjb" +
					//									"Bc46VLEDn8aVoyV3KtWrO2dlywNZt23CN3sEKbuKstA6Y+Xg9KW3j2XCgrxmvS9F8HjWNLWaJc5GQAM1z1Kqp6s6YQbMTwhYu/72NypX" +
					//									"k55r0bwJsbWRHMy846D3qn4f8PDTIpRPbyOTwcYzV7wxDFZ+IlwwVDIMZGe9ebWmp3sdVOLTPeNIWNbVCBgADH5CtNnIj3HAFZekSxG2" +
					//									"GMYAGDXKfE/x/pPh3TJYmv4BclTsiDgsT9B2H+c1yU4yatHc3lZO7OS/aL+IP9k6O+iadKPtlyNrEdVWvlxCS2ScseST6mtDxTrFzr2r" +
					//									"z6jdSsxckjPOKzUIjh8x846cjrXu4Sh7KHmeZiavtHZbGmn3QCMYqTkjBGaq2t1DMNgb5vQ1aBJAPb611NHIjpfB/jzxX4QlD6Jq88MX" +
					//									"8UEh8yJvqh4/SvbfBX7StpKkdt4r0eSGXcFa5s/mT6lCcj8M1828Y5Oce2aqXTJuMY5bufSqhVlB6FH6E+GvF3hnX1B0fWrG7JXcVjlG" +
					//									"5c46qeR1rWlO5jwMHuB1r84LaZ4JFlhleJkOVZDgj8a7bw18U/HmhMn2TX7qWNf+Wc7eapHpg11LFuyTJ5T7fsiRrdmUyAblAQe3zCiv" +
					//									"CPhP8fptb8ZaFomu6KiT3l9Bbpc2z8b3kVQWU9sn1/CirjNTu0cldWaPnv4iIF8davGFAH2yQ4x0OT/j/KueaPLFWAB6c11/xghe18f6" +
					//									"tHt2n7QDjOSdwz/hXLsomUOPvd64pKzsdUdkUxFtlMefcUk8WVIY8djipbjGFlHBRsEe1WRD5sbEEdM80jRsy7FgGOatzRZIKjiqcq+V" +
					//									"ckfl71pWoMy4xkAZ47igVyoiFSz4I285FRXsZ3LL2fvW1Jb5SSIDHy5Wstf31u8LffXkUhXuXfDqwS6jBFcbRE7hXJ7A8Z/WvU7nwGYo" +
					//									"MJx+H+fevG7GUpMOK+kvhtr9t4g8NQJJIsl3bqI5h7gcN+Irz8dKcffidmF5ZPlZ5ze+CL5YhcRRllB44r2n4M6c0GmxxXUXzAYAxyTW" +
					//									"/pOixvA4dAyZ5AA4OKu6TbvYXoWJVBzg8ZrzKmIlONmejCjyO6Kvi7T4EtmeKMDjPbivGrm8lsvEaxsgiO4E+mM17v4pS0SwuLu6cBgM" +
					//									"nJyMY6Yr5Q+IviJZdWkWyc4ztDDrWuGpObsiK0uRXPQviJ8WnsdPOk6FMBdMuJZ15CcdvU14Re3FzqF6000zySuSzOxLE/iajlZmJyxL" +
					//									"n+dWbGAqGdvl7mvYoYeNFeZ5lWvKoRsRCh+UsR2x1qncvLI2XOR2HYVrPEp+ZuTUElquOK22MrmYpwQRkEdCK2LC7WVAJThu5xwaoyWT" +
					//									"jlQam02J1nXcpx7+lO9xNWNN3CIWPUDpVBImnieQkgk54qS6nCtshxjoQemanhGIugGecChaEszLaUpIYn5Gcda0FjwAdxwegrLb/j5P" +
					//									"HetZclUPAoKeh1Pwc3D4v+DcfMP7esef+3hKKd8HCP8Ahbvg4+uvWIGP+vhKK6aGzOPEO7R13xi8BeOdU8c3V7Y+BvEk0EqRNvi0yaQZ" +
					//									"8tRjIXkg1xEHw2+JUVwVPw/8WFfUaNcY/wDQKKKmUU2aRqOyJrn4Y/ENlJHgDxUcjBH9j3HP/jlJZfDT4jGDbJ4A8WKRxk6PcD/2Siip" +
					//									"5EHtGV9V+FnxGyrR+AfFkhzg40e4PH/fFS2Hw1+I8V4mfh94sCumCTo1xgH3+SiijkQ/aOxqt8OPiESGHgTxR6Ef2RP/APEViS/DD4kJ" +
					//									"qDbPh/4sMZbqNGuCMfXZRRS9mhe0ZFcfCz4lQu+zwB4rbd0K6PcHg9f4OK1/BfhL4p+GdVi1C2+HvjFjuxNF/Y1xtkTHT7mc9qKKUqUZ" +
					//									"KzLhWkmmj6q8OWetPp0LHw9rMBlG5llsJEZfqCMg+1P1zTNasw09tomrzlhnEdi7HP0AoorxHgqfPuz1Vi522R4f8Sv+Fr6ostnY+BvF" +
					//									"hhYlRs0i4Ix9dleWN8NPiY0plf4feLiQc86NcEn/AMcoor16OHhCOh51bETnLUng+GfxGY5Pw+8WBieraLcD/wBkq8vw3+IYQR/8IB4q" +
					//									"AHf+x7jn/wAcoorX2aMfaMafhv8AEYn/AJEHxVxwB/Y9x/8AEUz/AIVr8RQv/IgeKjn/AKg9x/8AEUUUezQ/aMVPhn8RM/8AIheKh/3B" +
					//									"7j/4ikuvhr8RggSHwB4qy3LEaRP/APEUUUezQvaMrQ/DT4jrLtb4d+LMH+L+x7j/AOIq0nw3+I2DnwB4r6f9Ae4/+Ioop8iD2jMuT4Zf" +
					//									"Er7UWHw78W4z1Gi3H/xFaX/CtviOI0x4A8V57/8AEnuP/iKKKORD9ozpfhT8PvHtn8UfCV7eeCPE1tbW+t2cs0sulTokaLOhZmYrgAAE" +
					//							"knpRRRW1KNkc9aXM0f/Z");
					//					Services.social.save(pic);
				}
			}
		}
		//		for (Person a : ssocial.allPeople()){
		//			for (Person b : ssocial.allPeople()){
		//				if (r.nextFloat() > 0.9){
		//					SocialLink link = new SocialLink();
		//					link.type = 
		//							SocialLink.Type.values()[r.nextInt(SocialLink.Type.values().length)];
		//					link.userA = a.getId();
		//					link.userB = b.getId();
		//					ssocial.create(link);
		//				}
		//			}
		//		}
	}

	public void createSampleQuestions(Random r) throws Exception{
		svote.create(new NQQuestion(Type.SingleChoice,
				"Pour ou contre l'interdiction de chauffer un lieu public a plus de 22 et de climatiser a moins de 25?",
				"Pour","Contre"));
		svote.create(new NQQuestion(Type.Preferential,
				"Quel est l'enjeu le plus important pour l'humanité actuellement",
				"La paix dans le monde", "La croissance économique", "Le réchauffement climatique", "la pauvreté"));
		svote.create(new NQQuestion(Type.Preferential,
				"Quel est le droit de l'homme le plus essentiel?",
				"Le droit à un traitement équitable, sans distinction d'origine ou autre", 
				"Le droit à l'eau et la nourriture nécessaires à sa subsistance", 
				"Le droit à un logement décent", 
				"Le droit à la propriété privée",
				"Le droit à la présomption d'innocence",
				"Le droit d'exprimer, d'imprimer, d'énoncer ses pensées et opinions",
				"Le droit de pratiquer la religion de son choix voire aucune",
				"Le droit à un traitement équitable par la justice"));
		svote.create(new NQQuestion(Type.SingleChoice,
				"Devrions nous avoir un gouvernement mondial?",
				"Pour","Contre"));
		svote.create(new NQQuestion(Type.SingleChoice,
				"Devrions nous avoir une taxe de 1% sur toutes les transactions financières?",
				"Pour","Contre"));
		svote.create(new NQQuestion(Type.SingleChoice,
				"Devrions nous avoir un revenu minimal sans condition pour chaque personne?",
				"Non",
				"Oui de 400$",
				"Oui de 600$",
				"Oui de 800$",
				"Oui de 1000$"
				));
		svote.create(new NQQuestion(Type.Preferential,
				"Qui est le plus grand joueur de tennis de tous les temps?",
				"Roger Federer","Pete Sampras","Bjorn Borg", "Rafael Nadal", "André Agassi"));
		svote.create(new NQQuestion(Type.Preferential,
				"Qui est le plus grand joueur de football?",
				"Lionel Messi","Pelé","Zinédine Zidane"));
		svote.create(new NQQuestion(Type.Preferential,
				"Si un gouvernement mondial existait, quelle devrait être ses priorités?",
				"La lutte contre l'évasion fiscale", "La croissance économique", "Le réchauffement climatique", 
				"La lutte contre la pauvreté", "La lutte contre la faim", "la lutte contre le terrorisme"));
		svote.create(new NQQuestion(Type.SingleChoice,
				"Devrions nous mettre en place des villes expérimentales pour définir de nouveaux modes de vie?",
				"Pour","Contre"));
		svote.create(new NQQuestion(Type.SingleChoice,
				"Pour ou contre la suppression d'une partie de la dette publique grecque?",
				"Pour","Contre"));
		svote.create(new NQQuestion(Type.SingleChoice,
				"Doit-on limiter la puissance, le poids et la consommation des voitures?",
				"Pour","Contre"));
		svote.create(new NQQuestion(Type.SingleChoice,
				"Devons-nous définir un socle de ressources (nourriture, eau, argent) attribué à chaque humain sans conditions?",
				"Pour","Contre"));
	}

	public String fakeInclude(String pollId) throws NoSuchPoll {
		// add enough support so that the proposition becomes an include
		List<NQPerson> allPeople = this.ssocial.allPeople();
		NQQuestion poll = svote.pollById(UUID.fromString(pollId));

		for (NQPerson p : allPeople){
			try{
				NQQuestionInOrOut o = new NQQuestionInOrOut();
				o.questionId = poll.getId();
				o.opinion = Opinion.INCLUDE;
				o.voterId = p.getId();
				svote.addPropositionOpinion(o);

			}
			catch (AlreadyExpressedOpinion e) {} 
			catch (AlreadyAcceptedPoll e) {
				return "ok";
			}
		}
		return "Cannot";
	}

	public String fakeVote(String pollId) throws NoSuchPoll {
		// add enough support so that the proposition becomes an include
		List<NQPerson> allPeople = this.ssocial.allPeople();
		NQQuestion poll = svote.pollById(UUID.fromString(pollId));
		Random r = new Random();
		for (NQPerson p : allPeople){
			try{
				createOneVote(p,poll,r);
			}
			catch (AlreadyVoted e) {} 
			catch (NoToken e) {}
		}
		return "Cannot";
	}

	public void includeHalfPropositions() throws NoSuchPoll {
		boolean include = true;
		for (NQQuestion p : this.svote.propositions()){
			if (include){
				this.fakeInclude(p.getId());
			}
			include = !include;
		}
	}

}
