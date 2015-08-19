package org.deguet;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.mail.MessagingException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.deguet.model.NQToken;
import org.deguet.model.civil.NQPerson;
import org.deguet.model.transfer.C2SLoginPassword;
import org.deguet.model.transfer.C2SSignUpRequest;
import org.deguet.model.transfer.S2CIDCheck;
import org.deguet.service.ServiceEmail;
import org.deguet.service.ServiceSocial.BadEmail;
import org.deguet.service.ServiceSocial.BadPassword;
import org.deguet.service.ServiceSocial.NoToken;

import com.google.gson.Gson;

//http://en.wikipedia.org/wiki/Hash-based_message_authentication_code

@Path("/social") 
public class WebServiceSocial {

	public final static String Cookie = "votvot-id";

	private Gson gson;

	public WebServiceSocial() throws BadEmail{			
		gson = CustomGson.getIt();
	}

	@POST					@Path("/signin")
	@Produces("text/json")
	public Response signin(String json){ 
		System.out.println("SIGNIN " + json);
		C2SLoginPassword lp = gson.fromJson(json, C2SLoginPassword.class);
		if (lp == null){return Response.status(Response.Status.BAD_REQUEST).entity("BadFormat").build();}
		NQToken t;
		try {
			System.out.println("SIGNIN " + lp.email +"  ::::: " +lp.password);
			t = Services.social.signin(lp.email, lp.password);
			System.out.println("SIGNIN Success" + t.toString());
			return Response.ok(gson.toJson(t),MediaType.APPLICATION_JSON)
					.cookie(new NewCookie(Cookie, t.getId()))
					.build();
		}  catch (NoSuchAlgorithmException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getClass().getSimpleName().toString()).build();
		} catch (UnsupportedEncodingException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getClass().getSimpleName().toString()).build();
		} catch (BadEmail | BadPassword e) {
			System.out.println("SIGNIN Eror " + e);
			return Response.status(Response.Status.BAD_REQUEST).entity("BadCredentials").build();
		}
	}



	// expose the method as a get possibility to test in browser
	// http://localhost:8080/rest/social/signin/joris.deguet@gmail.com/password
	@GET					@Path("/signin/{email}/{password}")
	@Produces("text/json")
	public Response signinGet(@PathParam("email") String login, @PathParam("password") String password){ 
		System.out.println("SIGN IN THROUGH GET");
		C2SLoginPassword lp = new C2SLoginPassword();
		lp.email = login;
		lp.password = password;
		return signin(gson.toJson(lp));
	}

	@GET					@Path("/resetpassword/{email}")
	@Produces("text/json")
	public Response resetPassword(@PathParam("email") String login){
		try {
			// send email.
			ServiceEmail se = new ServiceEmail();
			se.sendEmail("5142491501@vmobile.ca", "Test Votvot");
			System.out.println("Password reset request");
			return Response.ok("Sent").build();
		} catch (MessagingException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getClass().getSimpleName().toString()).build();
		}
	}

	// simple example of a straight get access
	@GET					@Path("/all")
	@Produces("text/json")
	public Response all(@CookieParam(Cookie) String cookie){ 
		try {
			System.out.println("GET ALL cookie " + cookie );
			NQPerson p = Services.social.returnWithToken(cookie);
			if (cookie != null ) System.out.println("  from  " + p);
			return Response.ok(gson.toJson( Services.social.allPeople()),MediaType.APPLICATION_JSON).build();
		} catch (NoToken e) {
			// Should return unauthorized and rethink client tests : deleteall and then all the user does not exist anymore
			return Response.ok(gson.toJson( Services.social.allPeople()),MediaType.APPLICATION_JSON).build();
			//return Response.status(Response.Status.UNAUTHORIZED).entity("No auth token").build();
		}

	}

	@GET					@Path("/deleteall")
	@Produces("text/json")
	public Response deleteAll(@CookieParam(Cookie) String cookie) throws NoToken{
		// TODO handle not authorized
		System.out.println("GET DELETE ALL cookie " + cookie );
		if (cookie != null) System.out.println("  from  " + Services.social.returnWithToken(cookie));
		Services.social.deletePeople();
		return Response.ok(gson.toJson( Services.social.allPeople()),MediaType.APPLICATION_JSON).build();
	}



	// http://localhost:8080/rest/social/returnin/80bbdd51-a02c-4281-8630-d2012113473a
	@GET					@Path("/returnin/{token}")
	@Produces("text/json")
	public Response returninGet(@PathParam("token") String token){ 
		try {
			System.out.println("Return In " + token );
			NQPerson p = Services.social.returnWithToken(token);
			return Response.ok(gson.toJson(p),MediaType.APPLICATION_JSON).build();
		} catch (NoToken e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getClass().getSimpleName().toString()).build();
		} 
	}

	// exemple de construction de la reponse avec la possibilite de manipuler la reponse HTTP directement
	@POST					@Path("/signup")
	@Produces("text/json")
	public Response signup(String person) throws BadEmail{ 
		// Get the person that is candidate
		C2SSignUpRequest p = gson.fromJson(person, C2SSignUpRequest.class);
		System.out.println("SIGNUP REQUEST " + p);
		try {
			NQPerson persisted = Services.social.signUp(p);
			return Response.ok(gson.toJson(persisted),MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getClass().getSimpleName().toString()).build();
		}
	}

	// exemple de construction de la reponse avec la possibilite de manipuler la reponse HTTP directement
	@POST					@Path("/signout")
	@Produces("text/json")
	public Response signout(@CookieParam(Cookie) Cookie cookie, String token) throws BadEmail{ 
		// todo the service signout by erasing the token
		System.out.println("SIGNOUT REQUEST " + token);
		// erase the cookie
		if (cookie == null) return Response.ok("No cookie",MediaType.TEXT_PLAIN).build();
		NewCookie toDelete = new NewCookie(cookie, "to delete", 0 , false);
		Response res = Response.ok("TODO",MediaType.TEXT_PLAIN)
				.cookie(toDelete)
				.build();
		return res;
	}

	// exemple de construction de la reponse avec la possibilite de manipuler la reponse HTTP directement
	@GET					@Path("/idchecks")
	@Produces("text/json")
	public Response getAllIDCheck(@CookieParam(Cookie) Cookie cookie) throws BadEmail{ 
		// todo the service signout by erasing the token
		System.out.println("All IDChecks " + cookie);
		List<S2CIDCheck> checks = Services.social.allIDChecks();
		String json  = gson.toJson(checks);
		Response res = Response.ok(json,MediaType.APPLICATION_JSON).build();
		return res;
	}

	// exemple de construction de la reponse avec la possibilite de manipuler la reponse HTTP directement
	@GET					@Path("/person/{personId}")
	@Produces("text/json")
	public Response getPerson(@CookieParam(Cookie) Cookie cookie, @PathParam("personId") String personId) throws BadEmail{ 
		// todo the service signout by erasing the token
		System.out.println("Get a person by ID " + personId);
		NQPerson p = Services.social.getByID(personId);
		String json  = gson.toJson(p);
		Response res = Response.ok(json,MediaType.APPLICATION_JSON).build();
		return res;
	}

}
