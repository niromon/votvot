package com.deguet.gutils.file;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;


public class TestFile {

	
	//@Test
	public void testFileString() throws IOException{
		File test = new File("test/test");
		String content = "test unitaire de FileString";
		FileString.toFile(test, content);
		String recov = FileString.fromFile(test.getAbsolutePath());
		System.out.println(content.length());
		System.out.println(recov.length());
		Assert.assertEquals(content, recov);
	}
	
	//@Test
	public void testFileString2() throws IOException{
		File test = new File("test/test2");
		String content = "test unitaire de FileString\nbliblo\n";
		FileString.toFile(test, content);
		String recov = FileString.fromFile(test.getAbsolutePath());
		System.out.println(content.length());
		System.out.println(recov.length());
		
		System.out.println("==========");
		System.out.println("======="+content+"======");
		System.out.println("======="+recov+"=====");
		Assert.assertEquals(content, recov);
	}

	
}
