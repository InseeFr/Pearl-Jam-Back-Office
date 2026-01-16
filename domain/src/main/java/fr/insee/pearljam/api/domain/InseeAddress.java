package fr.insee.pearljam.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.insee.pearljam.api.dto.address.AddressDto;

/**
 * Entity InseeAddress : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InseeAddress extends Address {
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = -4901950808854835782L;
	/**
	 * The line 1 of INSEE address
	 */
	@Column(length = 255)
	private String l1;
	/**
	 * The line 2 of INSEE address
	 */
	@Column(length = 255)
	private String l2;
	/**
	 * The line 3 of INSEE address
	 */
	@Column(length = 255)
	private String l3;
	/**
	 * The line 4 of INSEE address
	 */
	@Column(length = 255)
	private String l4;
	/**
	 * The line 5 of INSEE address
	 */
	@Column(length = 255)
	private String l5;
	/**
	 * The line 6 of INSEE address
	 */
	@Column(length = 255)
	private String l6;
	/**
	 * The line 7 of INSEE address
	 */
	@Column(length = 255)
	private String l7;

	/**
	 * The elevator presence of INSEE address
	 */
	@Column
	private Boolean elevator;

	/**
	 * The building info of INSEE address
	 */
	@Column(length = 255)
	private String building;

	/**
	 * The floor info of INSEE address
	 */
	@Column(length = 255)
	private String floor;
	/**
	 * The door info of INSEE address
	 */
	@Column(length = 255)
	private String door;
	/**
	 * The staircase info of INSEE address
	 */
	@Column(length = 255)
	private String staircase;

	/**
	 * The city priority district info of INSEE address
	 */
	@Column(name = "city_priority_district")
	private Boolean cityPriorityDistrict;

	/**
	 * Constructor for the entity
	 * 
	 * @param address
	 */
	public InseeAddress(AddressDto address) {
		this.l1 = address.getL1();
		this.l2 = address.getL2();
		this.l3 = address.getL3();
		this.l4 = address.getL4();
		this.l5 = address.getL5();
		this.l6 = address.getL6();
		this.l7 = address.getL7();
		this.elevator = address.getElevator();
		this.building = address.getBuilding();
		this.floor = address.getFloor();
		this.door = address.getDoor();
		this.staircase = address.getStaircase();
		this.cityPriorityDistrict = address.getCityPriorityDistrict();
	}

}
