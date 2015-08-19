package org.deguet.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.deguet.model.vote.NQQuestion;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class TestData {

	public class ListPoll {
		List<NQQuestion> polls = new ArrayList<NQQuestion>();
	}
	
	@Test
	public void testImportData() throws JsonSyntaxException, JsonIOException, FileNotFoundException, JsonProcessingException{
		File dir = new File("data");
		for (File file : dir.listFiles()){
			if (file.getName().startsWith(".")) continue;
			if (file.isDirectory()) continue;
			System.out.println("File  " + file.getAbsolutePath());
			Gson gson = new Gson();
			
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			List<NQQuestion> polls = gson.fromJson(new FileReader(file), new TypeToken<List<NQQuestion>>(){}.getType());
			for (NQQuestion poll : polls){
				System.out.println("Poll extracted " + poll);
				String yaml = mapper.writeValueAsString(poll);
				System.out.println(yaml);
			}
		}
	}
	
}
