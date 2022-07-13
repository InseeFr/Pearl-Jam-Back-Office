package fr.insee.pearljam.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import fr.insee.pearljam.api.dto.address.AddressDto;

/**
 * Entity InseeAddress : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity
public class InseeAddress extends Address {
	/**
	 * 
	 */
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
	 * Defaut constructor for the entity
	 */
	public InseeAddress() {

	}

	/**
	 * Constructor for the entity
	 * 
	 * @param l1
	 * @param l2
	 * @param l3
	 * @param l4
	 * @param l5
	 * @param l6
	 * @param l7
	 * @param elevator
	 * @param building
	 * @param floor
	 * @param door
	 * @param staircase
	 * @param cityPriorityDistrict
	 */
	public InseeAddress(String l1, String l2, String l3, String l4, String l5, String l6, String l7, boolean elevator,
			String building, String floor, String door, String staircase, boolean cityPriorityDistrict) {
		this.l1 = l1;
		this.l2 = l2;
		this.l3 = l3;
		this.l4 = l4;
		this.l5 = l5;
		this.l6 = l6;
		this.l7 = l7;
		this.elevator = elevator;
		this.building = building;
		this.floor = floor;
		this.door = door;
		this.staircase = staircase;
		this.cityPriorityDistrict = cityPriorityDistrict;
	}

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
	}

	/**
	 * @return the line 1
	 */
	public String getL1() {
		return l1;
	}

	/**
	 * @param l1 the line 1 to set
	 */
	public void setL1(String l1) {
		this.l1 = l1;
	}

	/**
	 * @return the line 2
	 */
	public String getL2() {
		return l2;
	}

	/**
	 * @param l2 the line 2 to set
	 */
	public void setL2(String l2) {
		this.l2 = l2;
	}

	/**
	 * @return the line 3
	 */
	public String getL3() {
		return l3;
	}

	/**
	 * @param l3 the line 3 to set
	 */
	public void setL3(String l3) {
		this.l3 = l3;
	}

	/**
	 * @return the line 4
	 */
	public String getL4() {
		return l4;
	}

	/**
	 * @param l4 the line 4 to set
	 */
	public void setL4(String l4) {
		this.l4 = l4;
	}

	/**
	 * @return the line 5
	 */
	public String getL5() {
		return l5;
	}

	/**
	 * @param l5 the line 5 to set
	 */
	public void setL5(String l5) {
		this.l5 = l5;
	}

	/**
	 * @return the line 6
	 */
	public String getL6() {
		return l6;
	}

	/**
	 * @param l6 the line 6 to set
	 */
	public void setL6(String l6) {
		this.l6 = l6;
	}

	/**
	 * @return the line 7
	 */
	public String getL7() {
		return l7;
	}

	/**
	 * @param l7 the line 7 to set
	 */
	public void setL7(String l7) {
		this.l7 = l7;
	}

	public boolean isElevator() {
		return this.elevator;
	}

	public void setElevator(Boolean elevator) {
		this.elevator = elevator != null ? elevator : false;
	}

	public String getBuilding() {
		return this.building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getFloor() {
		return this.floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getDoor() {
		return this.door;
	}

	public void setDoor(String door) {
		this.door = door;
	}

	public String getStaircase() {
		return this.staircase;
	}

	public void setStaircase(String staircase) {
		this.staircase = staircase;
	}

	public boolean isCityPriorityDistrict() {
		return this.cityPriorityDistrict;
	}

	public void setCityPriorityDistrict(Boolean cityPriorityDistrict) {
		this.cityPriorityDistrict = cityPriorityDistrict != null ? cityPriorityDistrict : false;
	}

}
