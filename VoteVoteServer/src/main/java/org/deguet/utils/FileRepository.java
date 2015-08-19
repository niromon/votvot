package org.deguet.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.deguet.CustomGson;
import org.deguet.model.Identifiable;

import com.google.gson.Gson;

public class FileRepository<T extends Identifiable> implements CRUD<T> {

	static Gson gson = CustomGson.getIt();
	Class<T> classe;
	Long next = null;

	File base;
	
	public FileRepository(Class<T> c){
		this.classe = c;
		base =  new File("./data/"+c.getSimpleName());
		base.mkdirs();
	}

	public T get(String id) {
		synchronized (classe) {
			String content;
			try {
				File f = new File(base,id+"."+classe.getSimpleName());
				if (!f.exists()) return null;
				content = FileUtils.readFileToString(new File(base,id+"."+classe.getSimpleName()));
				T a = gson.fromJson(content, classe);
				return a;
			} catch (IOException e) {
				return null;
			}
		}
	}

	public List<T> getAll() {
		synchronized (classe) {
			List<T> achats = new ArrayList<T>();
			for (File f : base.listFiles()){
				if (f.getName().endsWith("."+classe.getSimpleName())){
					try{
						//System.out.println("File is "+f.getName());
						String content = FileUtils.readFileToString(f);
						T a = gson.fromJson(content, classe);
						achats.add(a);
					}
					catch(Exception e){	e.printStackTrace(); }
				}
			}
			return achats;
		}
	}

	public void save(T a) {
		synchronized (classe) {
			// set the id
			if (a.getId() == null) a.setId(this.nextAvailableId());
			String serialise = gson.toJson(a);
			try {
				FileUtils.writeStringToFile(new File(base, a.getId()+"."+classe.getSimpleName()), serialise);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void delete(T a) {
		synchronized (classe) {
			File f = new File(base, a.getId()+"."+classe.getSimpleName());
			f.delete();
		}
	}

	private String nextAvailableId(){
		synchronized (classe) {
			return UUID.randomUUID().toString();
		}
	}

	public void deleteAll() {
		for (T t : getAll())  {  delete(t);  }
	}

}
