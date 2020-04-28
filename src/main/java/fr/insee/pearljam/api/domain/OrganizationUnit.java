package fr.insee.pearljam.api.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table
public class OrganizationUnit {
	
	@Id
	public String id;

	public Type label;
	
	public Type type;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Type getLabel() {
		return label;
	}

	public void setLabel(Type label) {
		this.label = label;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
