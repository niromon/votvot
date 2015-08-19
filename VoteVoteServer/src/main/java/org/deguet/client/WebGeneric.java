package org.deguet.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.deguet.WebServiceSocial;
import org.deguet.model.Identifiable;

import com.google.gson.Gson;

public class WebGeneric<T extends Identifiable>  {

	private Gson gson = new Gson();

	private Class<T> theClass;

	class ListOfSomething<TT> implements ParameterizedType {
		Class<?> classe;
		public ListOfSomething(Class<TT> cl){ this.classe = cl;}
		@Override public Type[] getActualTypeArguments() { return new Type[] {classe}; }
		@Override public Type getRawType() {return List.class; }
		@Override public Type getOwnerType() {return null;}
	}

	String base;

	public WebGeneric(Class<T> c, String b){
		this.theClass = c;
		this.base = b;
	}

	private static <E> List<E> fromJson(String input, Class<E> c){
		Gson g = new Gson();
		E[] array = g.fromJson(input, new ArrayOfSomething<E>(c));
		return Arrays.asList(array);
	}
	
	private void setCookie(HttpURLConnection conn) {
		//if (cookie != null)
			conn.setRequestProperty("Cookie", WebServiceSocial.Cookie+"=pipo");
	}

	private String baseURL(){
		return base+this.theClass.getSimpleName().toLowerCase();
	}

	public List<T> getAll() throws IOException {
		InputStream is = null;
		URL url = new URL(baseURL()+"/all");
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
			List<T> elements = fromJson(contentAsString, theClass);
			return elements;
		} finally {
			if (is != null) {
				is.close();
			} 
		}
	}

	public void deleteAll() throws IOException  {
		InputStream is = null;

		HttpURLConnection conn = null;
		URL url = new URL(baseURL()+"/deleteall");
		conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		setCookie(conn);
		conn.setInstanceFollowRedirects(false); 

		// Starts the query
		conn.connect();

		is = conn.getInputStream();
		// Convert the InputStream into a string
		String contentAsString = IOUtils.toString(is, "UTF-8");
		System.out.println("DELETE ALL " +contentAsString);
		List<T> elements = fromJson(contentAsString, theClass);
		return ;
	}

	public T get(String id) throws IOException {
		InputStream is = null;
		URL url = new URL(baseURL()+"/"+id);
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
			T result = 
					gson.fromJson(contentAsString, theClass);
			return result;
		} finally {
			if (is != null) {
				is.close();
			} 
		}
	}

	public T save(T o) throws IOException {
		InputStream is = null;
		URL url = new URL(baseURL()+"/create");
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("POST");
			setCookie(conn);
			conn.setDoInput(true);
			conn.setDoOutput(true);

			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));

			String json = gson.toJson(o);
			writer.write(json);
			writer.flush();
			writer.close();
			os.close();

			
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			System.out.println("Response code " + response);
			is = conn.getInputStream();
			// Convert the InputStream into a string
			String contentAsString = IOUtils.toString(is, "UTF-8");
			System.out.println(contentAsString);
			T result = 
					gson.fromJson(contentAsString, theClass);
			return result;
		} finally {
			if (is != null) {
				is.close();
			} 
		}

	}

	public void delete(T o) throws IOException {
		InputStream is = null;
		URL url = new URL(baseURL()+"/delete/"+o.getId());
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));

			String json = gson.toJson(o);
			writer.write(json);
			writer.flush();
			writer.close();
			os.close();

			setCookie(conn);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			System.out.println("Response code " + response);
			is = conn.getInputStream();
			// Convert the InputStream into a string
			String contentAsString = IOUtils.toString(is, "UTF-8");
			System.out.println(contentAsString);
			return;
		} finally {
			if (is != null) {
				is.close();
			} 
		}
	}

}

