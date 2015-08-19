package org.deguet.model;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Entity;

@Entity
public class NQPosition extends Identifiable{

	public NQPosition(){}
	
	public NQPosition(String desc, double la, double lg) {
		setId(UUID.randomUUID().toString());
		this.description = desc;
		this.lat = la;
		this.lng = lg;
	}

	@Override
	public String toString() {
		return "Position [lat=" + lat + ", lng=" + lng + ", description="
				+ description + "]";
	}

	@Basic
	public Double lat,lng;
	
	@Basic
	public String description;
	
}
