package org.deguet.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.deguet.CustomGson;
import org.deguet.WebServiceSocial;
import org.deguet.model.NQToken;
import org.deguet.model.civil.NQPerson;
import org.deguet.model.civil.SocialLink;
import org.deguet.model.transfer.C2SLoginPassword;
import org.deguet.model.transfer.C2SSignUpRequest;
import org.deguet.model.transfer.C2SVoteRequest;
import org.deguet.model.vote.NQQuestion;
import org.deguet.model.vote.NQResult;
import org.deguet.service.ServiceSocial;
import org.deguet.service.ServiceSocial.BadAddress;
import org.deguet.service.ServiceSocial.BadBirth;
import org.deguet.service.ServiceSocial.BadCredentials;
import org.deguet.service.ServiceSocial.BadEmail;
import org.deguet.service.ServiceSocial.BadSex;
import org.deguet.service.ServiceSocial.NoOneLogged;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WebClientVotVot {

	Current current = null;
	
	// some not so safe code to accept all certificates TODO TODO remove before production
	static {
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {}
		//for localhost testing only
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
				new javax.net.ssl.HostnameVerifier(){

					public boolean verify(String hostname,
							javax.net.ssl.SSLSession sslSession) {
						if (hostname.equals("localhost")) {
							return true;
						}
						if (hostname.startsWith("192.168.0.")) {
							return true;
						}
						if (hostname.equals("10.0.2.2")) {
							return true;
						}
						if (hostname.equals("5a5.di.college-em.info")) {
							return true;
						}
						return false;
					}
				});
	}

	private String baseURL;

	private Gson gson = CustomGson.getIt();

	private NQPerson user;

	public WebClientVotVot(String base) {
		this.baseURL = base;
	}

	public NQPerson signUp(C2SSignUpRequest p) throws BadEmail, BadAddress, BadBirth, BadSex, IOException {
		InputStream is = null;
		URL url = new URL(this.baseURL+"social/signup");
		String json = gson.toJson(p);
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("POST");
			conn.setInstanceFollowRedirects(false); 
			setCookie(conn);
			conn.setDoInput(true);
			conn.setDoOutput(true);


			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));

			writer.write(json);
			writer.flush();
			writer.close();
			os.close();

			// Starts the query
			conn.connect();

			is = conn.getInputStream();
			// Convert the InputStream into a string
			String contentAsString = IOUtils.toString(is, "UTF-8");
			//System.out.println("SIGNUP " +contentAsString);
			NQPerson serverP = gson.fromJson(contentAsString, NQPerson.class);
			return serverP;
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} 
		catch(IOException e){
			int response = conn.getResponseCode();
			System.out.println("Response code " + response);
			System.out.println("Response message " + conn.getResponseMessage());
			System.out.println("Response code " + conn.getResponseMessage());
			is = conn.getErrorStream();
			// Convert the InputStream into a string
			String exception = IOUtils.toString(is, "UTF-8");
			System.out.println("Response content >" + exception+"<");
			if(exception.equals("BadBirth")) 	throw new BadBirth();
			if(exception.equals("BadEmail")) 	throw new BadEmail(p.email);
			if(exception.equals("BadSex")) 		throw new BadSex();
		}
		finally {
			if (is != null) {
				is.close();
			} 
		}
		return null;
	}

	private void setCookie(HttpURLConnection conn) {
		if (current != null)
			conn.setRequestProperty("Cookie", WebServiceSocial.Cookie+"="+current.token);
	}
	
	public static class NotOnLocalhost extends Exception{}
	
	public void deleteAllFromLocalhost() throws IOException, NotOnLocalhost  {
		InputStream is = null;
		HttpURLConnection conn = null;
		URL url = new URL(this.baseURL+"flushfortests");
		conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(60000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		setCookie(conn);
		conn.setInstanceFollowRedirects(false); 
		// Starts the query
		conn.connect();
		is = conn.getInputStream();
		// Convert the InputStream into a string
		String contentAsString = IOUtils.toString(is, "UTF-8");
		if (!contentAsString.equals("ok")) throw new NotOnLocalhost();
		System.out.println("Flush For tests " +contentAsString);
	} 

	public List<NQPerson> allPeople() throws IOException {
		InputStream is = null;
		URL url = new URL(this.baseURL+"social/all");
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			setCookie(conn);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			System.out.println("Response code " + response);
			is = conn.getInputStream();
			// Convert the InputStream into a string
			String contentAsString = IOUtils.toString(is, "UTF-8");
			System.out.println(contentAsString);
			List<NQPerson> result = 
					gson.fromJson(contentAsString, new TypeToken<List<NQPerson>>(){}.getType());
			return result;
		} finally {
			if (is != null) {
				is.close();
			} 
		}
	}

	public void create(SocialLink link) {
		// TODO Auto-generated method stub

	}

	public NQToken signin(String login, String password) throws BadCredentials, IOException {
		C2SLoginPassword lp = new C2SLoginPassword();
		lp.email = login;
		lp.password = password;

		InputStream is = null;
		URL url = new URL(this.baseURL+"social/signin");
		String json = gson.toJson(lp);
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("POST");
			conn.setInstanceFollowRedirects(false); 
			setCookie(conn);
			conn.setDoInput(true);
			conn.setDoOutput(true);


			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));

			writer.write(json);
			writer.flush();
			writer.close();
			os.close();

			// Starts the query
			conn.connect();

			is = conn.getInputStream();
			// Convert the InputStream into a string
			String contentAsString = IOUtils.toString(is, "UTF-8");
			//System.out.println("SIGNUP " +contentAsString);
			NQToken serverP = gson.fromJson(contentAsString, NQToken.class);
			current = new Current();
			current.id = serverP.userID;
			current.name = login;
			current.token = serverP.getId();
			return serverP;
		} 
		catch(IOException e){
			current = null;
			int response = conn.getResponseCode();
			System.out.println("Response code " + response);
			System.out.println("Response message " + conn.getResponseMessage());
			System.out.println("Response code " + conn.getResponseMessage());
			is = conn.getErrorStream();
			// Convert the InputStream into a string
			String exception = IOUtils.toString(is, "UTF-8");
			System.out.println("Response content >" + exception+"<");
			if(exception.equals("BadCredentials")) 	throw new BadCredentials();
		}
		finally {
			if (is != null) {
				is.close();
			} 
		}
		return null;

	}

	public void logout() {
		// TODO 
		// call server to tell it
		
		// on success delete cookie
		current = null;
	}

	public String currentUserID() throws NoOneLogged {
		if (current == null ) return null;
		return current.id;
	}
	
	public String currentUserName() throws NoOneLogged {
		if (current == null ) return null;
		return current.name;
	}

	/**
	 * Decides locally if the password is alright.
	 * @param password
	 * @return
	 */
	public boolean isValidPassword(String password) {
		return ServiceSocial.isValidPassword(password);
	}

	public static byte[] hash(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return ServiceSocial.hash(s);
	}

	public void initialFromLocalHost() throws IOException {
		InputStream is = null;
		HttpURLConnection conn = null;
		URL url = new URL(this.baseURL+"initial");
		conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(90000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		setCookie(conn);
		conn.setInstanceFollowRedirects(false); 
		// Starts the query
		conn.connect();
		is = conn.getInputStream();
		// Convert the InputStream into a string
		String contentAsString = IOUtils.toString(is, "UTF-8");
		System.out.println("Flush For tests " +contentAsString);
	}

	public List<NQQuestion> allPolls() throws IOException {
		WebGeneric<NQQuestion> delegate = new WebGeneric<NQQuestion>(NQQuestion.class,this.baseURL);
		return delegate.getAll();
	}

	public NQResult resultForPoll(NQQuestion p) throws IOException {
		InputStream is = null;
		URL url = new URL(this.baseURL+"vote/results/"+p.getId());
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			setCookie(conn);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			System.out.println("Response code " + response);
			is = conn.getInputStream();
			// Convert the InputStream into a string
			String contentAsString = IOUtils.toString(is, "UTF-8");
			System.out.println(contentAsString);
			NQResult result = 
					gson.fromJson(contentAsString, NQResult.class);
			return result;
		} finally {
			if (is != null) {
				is.close();
			} 
		}
	}

	public void vote(C2SVoteRequest vote) throws IOException{
		InputStream is = null;
		URL url = new URL(this.baseURL+"vote/vote");
		String json = gson.toJson(vote);
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("POST");
			conn.setInstanceFollowRedirects(false); 
			setCookie(conn);
			conn.setDoInput(true);
			conn.setDoOutput(true);


			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));

			writer.write(json);
			writer.flush();
			writer.close();
			os.close();

			// Starts the query
			conn.connect();

			is = conn.getInputStream();
			// Convert the InputStream into a string
			String contentAsString = IOUtils.toString(is, "UTF-8");
			//System.out.println("SIGNUP " +contentAsString);
			//Poll serverP = gson.fromJson(contentAsString, Poll.class);
			return ;
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} 
		catch(IOException e){
			int response = conn.getResponseCode();
			is = conn.getErrorStream();
			// Convert the InputStream into a string
			String exception = IOUtils.toString(is, "UTF-8");
			System.out.println("Response content >" + exception+"<");
		}
		finally {
			if (is != null) {
				is.close();
			} 
		}
	}
	
	public NQQuestion propose(NQQuestion p) throws IOException {
		WebGeneric<NQQuestion> delegate = new WebGeneric<NQQuestion>(NQQuestion.class, this.baseURL);
		return delegate.save(p);
	}

	public NQQuestion getPoll(String id) throws IOException {
		WebGeneric<NQQuestion> delegate = new WebGeneric<NQQuestion>(NQQuestion.class, this.baseURL);
		return delegate.get(id);
	}
	
	public static class Current {
		public String name;
		public String id;
		public String token;
	}

}
