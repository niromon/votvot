package org.deguet.tests.lab;


public  class B extends A { 
	String b = "b";
	public B(){a="Bb";}
	@Override public String toString(){return "|"+b+" -- "+super.toString();}
}