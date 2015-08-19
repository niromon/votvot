package org.deguet.tests;

import org.junit.Test;

public class RREGGOPP {

	@Test
	public void calculReegop(){
		double amount = 5000;
		for (int i = 2011 ; i <2041 ; i++){
			amount *= 1.09;
			amount += 4000;
			System.out.println(i+"   : "+amount);
		}
		System.out.println(amount/15);
	}
	
}
