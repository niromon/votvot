package com.deguet.gutils.permutation;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class TestPrime {


	@Test(timeout =10000)
	public  void testCodec(){
		PrimeBase base = new PrimeBase();
		long n = 20;
		System.out.println("=========================================================   ");
		System.out.println("n    is    : "+n);
		int[] arr = base.to(n);
		long recov = base.from(arr);
		System.out.println("recov is   : "+recov);
		Assert.assertEquals(" Equality test  ",recov, n);
		int[] arr2 = base.to(recov);
		Assert.assertArrayEquals("Blop ", arr, arr2);
		for (long ll = 100 ; ll >=0; ll -= 1){
			System.out.println(ll+" ::: "+Arrays.toString(base.to(ll)));
			Assert.assertEquals("should always be equal ",ll, base.from(base.to(ll)));
		}
	}

//	@Test
//	public void testFirstPrime(){
//		PrimeBase b = new PrimeBase();
//		for (int i = 1 ; i < 100 ; i++){
//			int prime = b.nthPrime(i);
//			int i2 = b.primeIndex(prime);
//			Assert.assertEquals(i, i2);
//		}
//	}
	
	@Test
	public void testRankedVote(){
		PrimeBase b = new PrimeBase();
		int[] test = {3, 0, 0, 0, 0, 0, 0, 0, 0};
		System.out.println(b.from(test));
	}

}
