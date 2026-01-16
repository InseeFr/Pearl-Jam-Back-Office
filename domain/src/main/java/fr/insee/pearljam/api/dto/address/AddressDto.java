package fr.insee.pearljam.api.dto.address;

import fr.insee.pearljam.api.domain.Address;
import fr.insee.pearljam.api.domain.InseeAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AddressDto {

	// address 7 lines
	private String l1;
	private String l2;
	private String l3;
	private String l4;
	private String l5;
	private String l6;
	private String l7;

	private Boolean elevator;
	private String building;
	private String floor;
	private String door;
	private String staircase;
	private Boolean cityPriorityDistrict;

	public AddressDto(Address address) {
		super();
		if (address != null) {
			this.l1 = ((InseeAddress) address).getL1();
			this.l2 = ((InseeAddress) address).getL2();
			this.l3 = ((InseeAddress) address).getL3();
			this.l4 = ((InseeAddress) address).getL4();
			this.l5 = ((InseeAddress) address).getL5();
			this.l6 = ((InseeAddress) address).getL6();
			this.l7 = ((InseeAddress) address).getL7();
			this.elevator = ((InseeAddress) address).getElevator();
			this.building = ((InseeAddress) address).getBuilding();
			this.floor = ((InseeAddress) address).getFloor();
			this.door = ((InseeAddress) address).getDoor();
			this.staircase = ((InseeAddress) address).getStaircase();
			this.cityPriorityDistrict = ((InseeAddress) address).getCityPriorityDistrict();
		}
	}

	public AddressDto() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((l1 == null) ? 0 : l1.hashCode());
		result = prime * result + ((l2 == null) ? 0 : l2.hashCode());
		result = prime * result + ((l3 == null) ? 0 : l3.hashCode());
		result = prime * result + ((l4 == null) ? 0 : l4.hashCode());
		result = prime * result + ((l5 == null) ? 0 : l5.hashCode());
		result = prime * result + ((l6 == null) ? 0 : l6.hashCode());
		result = prime * result + ((l7 == null) ? 0 : l7.hashCode());
		result = prime * result + Boolean.hashCode(elevator);
		result = prime * result + ((building == null) ? 0 : building.hashCode());
		result = prime * result + ((floor == null) ? 0 : floor.hashCode());
		result = prime * result + ((door == null) ? 0 : door.hashCode());
		result = prime * result + ((staircase == null) ? 0 : staircase.hashCode());
		result = prime * result + Boolean.hashCode(cityPriorityDistrict);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AddressDto other = (AddressDto) obj;
		if (l1 == null) {
			if (other.l1 != null)
				return false;
		} else if (!l1.equals(other.l1))
			return false;
		if (l2 == null) {
			if (other.l2 != null)
				return false;
		} else if (!l2.equals(other.l2))
			return false;
		if (l3 == null) {
			if (other.l3 != null)
				return false;
		} else if (!l3.equals(other.l3))
			return false;
		if (l4 == null) {
			if (other.l4 != null)
				return false;
		} else if (!l4.equals(other.l4))
			return false;
		if (l5 == null) {
			if (other.l5 != null)
				return false;
		} else if (!l5.equals(other.l5))
			return false;
		if (l6 == null) {
			if (other.l6 != null)
				return false;
		} else if (!l6.equals(other.l6))
			return false;
		if (l7 == null) {
			if (other.l7 != null)
				return false;
		} else if (!l7.equals(other.l7))
			return false;
		if (building == null) {
			if (other.building != null)
				return false;
		} else if (!building.equals(other.building))
			return false;
		if (floor == null) {
			if (other.floor != null)
				return false;
		} else if (!floor.equals(other.floor))
			return false;
		if (door == null) {
			if (other.door != null)
				return false;
		} else if (!door.equals(other.door))
			return false;
		if (staircase == null) {
			if (other.staircase != null)
				return false;
		} else if (!staircase.equals(other.staircase))
			return false;
		if (elevator != other.elevator)
			return false;
		if (cityPriorityDistrict != other.cityPriorityDistrict)
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "AddressDto [l1=" + l1 + ", l2=" + l2 + ", l3=" + l3 + ", l4=" + l4 + ", l5=" + l5 + ", l6=" + l6
				+ ", l7=" + l7 +
				", building=" + building + ", floor=" + floor + ", door=" + door + ", staircase=" + staircase
				+ ", elevator=" + elevator + ", cityPriorityDistrict=" + cityPriorityDistrict + "]";
	}

}
