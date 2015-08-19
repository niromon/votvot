package org.deguet.service.calc;

import java.util.UUID;

public class ServiceMemonic {

	String[] consoles = {"B","K","S","D","F","J","L","M","N","O","P","R","T"};
	
	String[] vowels = {"A","E","O","I","U"};
	
	String[] digits = {"1","2","3","5","6","7","8","9"};
	
	// would make easy to remember 585 possibilities like TO-4, FA-1 etc.
	
	// two blocks TO5-NA6 would lead to 342225 possiblities
	
	public String memonic(UUID uuid){
		long least = uuid.getLeastSignificantBits();
		long most = uuid.getMostSignificantBits();
		return "";
	}
	
}
