package org.deguet.model.civil;

import java.util.Base64;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.deguet.model.Identifiable;
import org.deguet.model.NQPosition;
import org.joda.time.DateTime;

@Entity
@Table(
        indexes = @Index(columnList = "email")
)
public class NQPerson extends Identifiable implements Comparable<NQPerson>{

	public enum Sex {Male,Female}
	@Column(unique = true) public String email;
	@Basic public String firstName;
	@Basic public String lastName;
	

	@Lob public byte[] password;
	@OneToOne(cascade = CascadeType.ALL) public NQPosition adress;
	@OneToOne(cascade = CascadeType.ALL) public NQPosition birthPlace;
	
	@Lob public DateTime birthDate;				//as millisec from Epoch
	@Basic public Sex sex;
	
	@Override
	public String toString() {
		return "Person [email=" + email + ", password="
				+ Base64.getEncoder().encodeToString(password) + ", adress=" + adress
				+ ", birthPlace=" + birthPlace + ", birthDate=" + birthDate
				+ ", sex=" + sex + "]";
	}

	@Override
	public int compareTo(NQPerson o) {
		return this.email.compareTo(o.email);
	}
}
