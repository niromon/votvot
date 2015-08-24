package org.deguet.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.deguet.model.NQPosition;
import org.deguet.model.NQToken;
import org.deguet.model.civil.IDPicture;
import org.deguet.model.civil.NQAffiliation;
import org.deguet.model.civil.NQConfirmation;
import org.deguet.model.civil.NQGroup;
import org.deguet.model.civil.NQPerson;
import org.deguet.model.civil.NQPerson.Sex;
import org.deguet.model.civil.Reference;
import org.deguet.model.civil.SocialLink;
import org.deguet.model.civil.SocialLink.Type;
import org.deguet.model.transfer.C2SSignUpRequest;
import org.deguet.model.transfer.S2CIDCheck;
import org.deguet.repo.RepoPerson;
import org.deguet.utils.CRUD;
import org.deguet.utils.FileRepository;
import org.deguet.utils.JPARepository;
import org.joda.time.DateTime;

/**
 * une personne s'inscrit et on peut valider son existence
 * apres elle peut
 * - changer de photo ou d'email
 * - (mais pas de lieu ou de date de naissance)
 * - mourir
 * 
 * Quelqu'un doit pouvoir annoncer la mort
 * Chaque transition doit être validée par des gens qui connaissent (contacts):
 * - des gens avec des comptes validés
 * - reliés par Facebook, par recherche par emails
 * 
 * Il faut 3 personnes (qui ne connait pas trois personnes) mais on continue à en valider plus.
 * 
 * En validant, vérifier si on a des profils trop proches (même date de naissance, noms proches, lieux proches)
 * 
 * Il faut démarrer quelque part: Visa, Etat??
 * @author joris
 *
 */
public class ServiceSocial  {

	// Exceptions
	public static class BadEmail extends Exception{ public BadEmail(String s){super(s);}}
	public static class BadPassword extends Exception{}
	public static class BadCredentials extends Exception{}
	public static class BadBirth extends Exception{}
	public static class BadSex extends Exception{}
	public static class BadAddress extends Exception{}

	public class NoOneLogged extends Exception{}
	public class NoToken extends Exception{}

	// Repositories
	public final RepoPerson 				repoperson;
	public final CRUD<NQToken> 				repoToken ;
	public final CRUD<IDPicture> 			repoPicture ;
	public final CRUD<SocialLink> 			repolink;
	public final CRUD<Reference> 			reporef;
	public final CRUD<NQGroup> 				repogroup;
	public final CRUD<NQAffiliation> 		repoaffiliation;
	public final CRUD<NQConfirmation> 		repoconfirmation;

	public ServiceSocial(){
		this.repoperson = new RepoPerson();
		this.repoToken = new FileRepository<NQToken>(NQToken.class);
		this.repolink = new FileRepository<SocialLink>(SocialLink.class);
		this.reporef = new FileRepository<Reference>(Reference.class);
		this.repoPicture = new FileRepository<IDPicture>(IDPicture.class);
		this.repogroup = new JPARepository<NQGroup>(NQGroup.class);
		this.repoaffiliation = new JPARepository<NQAffiliation>(NQAffiliation.class);
		this.repoconfirmation = new JPARepository<NQConfirmation>(NQConfirmation.class);
	}

	public List<NQPerson> relationByType(Type type, NQPerson p) {
		List<NQPerson> res = new ArrayList<NQPerson>();
		for (SocialLink l : repolink.getAll()){
			if (l.type == type){
				if (l.userA.equals(p.getId()) )
					res.add(repoperson.get(l.userB));
				else if (l.userB.equals(p.getId()))
					res.add(repoperson.get(l.userA));
			}
		}
		return res;
	}


	public NQPerson signUp(C2SSignUpRequest r) throws BadEmail, BadBirth, BadSex, BadAddress, NoSuchAlgorithmException, UnsupportedEncodingException {
		if (r.birthDate == null || r.birthDate.isAfter(DateTime.now())) throw new BadBirth();
		if (r.sex == null) throw new BadSex();
		if (r.adress == null || r.birthPlace == null) throw new BadAddress();
		if (r.adress.getId() == null) r.adress.setId(UUID.randomUUID().toString());
		if (r.birthPlace.getId() == null) r.birthPlace.setId(UUID.randomUUID().toString());
		// check if exists
		NQPerson existing = repoperson.findByEmail(r.email);
		if (existing != null) throw new BadEmail("User email already exists");
		// build the person and hash the password
		NQPerson p = C2SSignUpRequest.convert(r);
		repoperson.save(p);
		return p;
	}

	public List<NQPerson> allPeople() { return repoperson.getAll();}

	public NQToken signin(String login, String password) throws BadEmail, BadPassword, NoSuchAlgorithmException, UnsupportedEncodingException {
		// if credentials are good
		byte[] hashed = hash(password);
		NQPerson p = this.findByLogin(login);
		if (p != null && Arrays.equals(p.password, hashed) ){
			// get token associated with this login and delete it
			NQToken t;
			try {
				t = getTokenFor(p.getId());
				repoToken.delete(t);
			} catch (NoToken e) {} 
			// produce a new one
			NQToken newT = NQToken.forUser(p, 15);
			repoToken.save(newT);
			// return it serialized
			return newT;
		}
		// otherwise
		else{
			throw new BadPassword();
		}	
	}

	public NQPerson returnWithToken(String id) throws NoToken{
		//UUID uuid = UUID.fromString(id);
		NQToken t = repoToken.get(id);
		if (t == null){
			throw new NoToken();
		}
		NQPerson p = repoperson.get(t.userID);
		if (p == null)
			throw new NoToken();
		return p;
	}

	public NQPerson findByLogin(String login) throws BadEmail {
		for (NQPerson p : repoperson.getAll()){
			if (p.email.toLowerCase().trim().equals(login.toLowerCase().trim())){
				return p;
			}
		}
		throw new BadEmail(login);
	}

	/**
	 * Returns all references to date for this person (people guaranteeing that the person exists)
	 * @param p
	 * @return
	 */
	public List<Reference> refsFor(NQPerson p){
		List<Reference> res = new  ArrayList<Reference>();
		for (Reference r : reporef.getAll()){
			if (r.validated.getId().equals(p.getId())) res.add(r);
		}
		return res;
	}

	public List<NQPerson> similarProfiles(NQPerson p){
		List<NQPerson> result = new ArrayList<NQPerson>();
		for (NQPerson other : this.repoperson.getAll()){
			if (other.getId().equals(p.getId())) continue;					// do not count itself
			if (Math.abs(other.adress.lat - p.adress.lat) > 1) continue;	// should be close in space
			if (Math.abs(other.adress.lng - p.adress.lng) > 1) continue;
			if (Math.abs(other.birthDate.getMillis() - p.birthDate.getMillis()) > 1000000000) continue;
			if (distance(other.firstName, p.firstName) > 0.5) continue;		// should have similar name
			if (distance(other.lastName, p.lastName) > 0.5) continue;		
			result.add(other);
		}
		return result;
	}
	
	public static double distance(String a, String b){
		// TODO go get a fast similarity distance
		return 1.0;
	}
	
	public static boolean isValidPassword(String password) {return password.length() >= 4;}

	public NQToken getTokenFor(String id) throws NoToken {
		for (NQToken t : repoToken.getAll()){
			if (t.userID.equals(id)) return t;
		}
		throw new NoToken();
	}

	public void deletePeople() {
		repolink.deleteAll();
		repoperson.deleteAll();
	}

	public void add(Reference ref){
		// validation
		// add the reference
		// compute if we consider it a real person threshold
		reporef.save(ref);
	}

	public void create(SocialLink link) {
		// access permissions
		repolink.save(link);
	}


	public static byte[] hash(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-256");
		md.update(s.getBytes("UTF-8")); 
		byte[] digest = md.digest();
		return digest;
	}

	public List<S2CIDCheck> allIDChecks() {
		List<S2CIDCheck> res = new ArrayList<S2CIDCheck>();
		int count = 0;
		for (NQPerson p : repoperson.getAll()){
			S2CIDCheck check = new S2CIDCheck();
			check.person = p;
			for (IDPicture pic : repoPicture.getAll()){
				if (pic.personID.equals(p.getId())){
					check.picture = pic;
				}
			}
			res.add(check);
			count++;
			if (count > 6) return res;
		}
		return res;
	}

	public void save(IDPicture pic) {
		repoPicture.save(pic);
	}

	public NQPerson getByID(String id) {
		return repoperson.get(id);
	}

	public int countPeople() {
		return repoperson.count();
	}

}
