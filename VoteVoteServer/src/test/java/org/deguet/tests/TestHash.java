package org.deguet.tests;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.deguet.service.ServiceSocial;
import org.junit.Assert;
import org.junit.Test;

public class TestHash {

	@Test
	public void testHashAndString() throws NoSuchAlgorithmException, UnsupportedEncodingException{
		String pass  = "éléanor pouet pouet";
		byte[] bytes = ServiceSocial.hash(pass);
		String string = DatatypeConverter.printBase64Binary(bytes);
		System.out.println("chaine : " + string);
		byte[] bytes2 = DatatypeConverter.parseBase64Binary(string);
		Assert.assertArrayEquals(bytes, bytes2);
	}
	
}
