package org.deguet.utils;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.deguet.CustomGson;
import org.deguet.model.Identifiable;

import com.google.gson.Gson;

public abstract class GenericWebService<T extends Identifiable> {

	CRUD<T> repo;
	protected Gson g = CustomGson.getIt();
	final Class<T> myClass;

	public GenericWebService(Class<T> c){
		this.myClass = c;
		this.repo = new FileRepository<T>(c);
	}

	@GET					@Path("/{id}")
	@Produces("text/json")
	public String getById(@PathParam("id") String id){ 
		//UUID uuid = UUID.fromString(id);
		T res = repo.get(id.trim());
		if (res == null){
			return "nosuchelement" ; 
		}
		else{
			return g.toJson(res);
		}
	}

	@POST										@Path("/create")
//	@Consumes(MediaType.TEXT_PLAIN)				@Produces(MediaType.TEXT_PLAIN)
	public String create(String json) 
	{
		System.out.println("About to create  " +json);
		T o = g.fromJson(json, myClass);
		String t = create(o);
		System.out.println("Has Created  " +t);
		return t; 
	}

	protected String create(T o) 
	{
		repo.save(o);
		System.out.println("CREATE " + o + " >>LIST IS  " + repo.getAll());
		return g.toJson(o); 
	}

	@GET			@Path("/all")			@Produces("text/json")
	public String getAll(){ 
		return g.toJson(repo.getAll()) ; 
	}

	@GET			@Path("/deleteall")			@Produces("text/json")
	public String deleteAll(){ 
		repo.deleteAll();
		System.out.println("DELETE ALL " + repo.getAll());
		return g.toJson(repo.getAll()) ; 
	}

	@DELETE			@Path("/{id}")			@Produces("text/json")
	public String deleteById(@PathParam("id") String id){
		//UUID uuid = UUID.fromString(id);
		T t = repo.get(id);
		if (t != null ) {
			System.out.println("DELETE One bef " + repo.getAll().size());
			repo.delete(t);
			System.out.println("DELETE One after " + repo.getAll().size());
			return g.toJson(repo.getAll()) ; 
		}
		else{
			return "nosuchelement" ; 
		}
	}
	
	@GET		@Path("/delete/{id}")			@Produces("text/json")
	public String deleteByIdViaGet(@PathParam("id") String id){ 
		return this.deleteById(id) ; 
	}

}
