package org.deguet.tests.lab;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class TestLabSerial {

	@Test
	public void testDeserPoly(){
		List<A> list = new ArrayList<A>();
		list.add(new A());
		list.add(new B());

		Gson g = new GsonBuilder().setPrettyPrinting().create();
		String json = g.toJson(list);
		System.out.println(json);

		List<A> recov = new Gson().fromJson(json, List.class);
		System.out.println("recov " +recov);
		for (A a : recov){
			System.out.println(a.toString());
		}
	}

	@Test
	public void testDeserPoly2(){
		Collection collection = new ArrayList();  
		collection.add("hello");  
		collection.add(5);  
		collection.add(new int[]{3,4,5});  

		Gson g = new GsonBuilder().setPrettyPrinting().create();
		String json = g.toJson(collection);
		System.out.println(json);  
		Collection things = g.fromJson(json, Collection.class);  
		System.out.println(things);  
	}

}
