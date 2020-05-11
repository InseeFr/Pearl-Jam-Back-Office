package fr.insee.pearljam.api.dto.address;

public class AddressDto {

	/**
	 * First line of the AdressDto
	 */
	private String l1;
	
	/**
	 * First line of the AdressDto
	 */
	private String l2;
	
	/**
	 * First line of the AdressDto
	 */
	private String l3;
	
	/**
	 * First line of the AdressDto
	 */
	private String l4;
	
	/**
	 * First line of the AdressDto
	 */
	private String l5;

	/**
	 * First line of the AdressDto
	 */
	private String l6;
	
	/**
	 * First line of the AdressDto
	 */
	private String l7;
	
	public AddressDto(String l1, String l2, String l3, String l4, String l5, String l6, String l7) {
		super();
		this.l1 = l1;
		this.l2 = l2;
		this.l3 = l3;
		this.l4 = l4;
		this.l5 = l5;
		this.l6 = l6;
		this.l7 = l7;
	}
	/**
	 * @return the l1
	 */
	public String getL1() {
		return l1;
	}
	/**
	 * @param l1 the l1 to set
	 */
	public void setL1(String l1) {
		this.l1 = l1;
	}
	/**
	 * @return the l2
	 */
	public String getL2() {
		return l2;
	}
	/**
	 * @param l2 the l2 to set
	 */
	public void setL2(String l2) {
		this.l2 = l2;
	}
	/**
	 * @return the l3
	 */
	public String getL3() {
		return l3;
	}
	/**
	 * @param l3 the l3 to set
	 */
	public void setL3(String l3) {
		this.l3 = l3;
	}
	/**
	 * @return the l4
	 */
	public String getL4() {
		return l4;
	}
	/**
	 * @param l4 the l4 to set
	 */
	public void setL4(String l4) {
		this.l4 = l4;
	}
	/**
	 * @return the l5
	 */
	public String getL5() {
		return l5;
	}
	/**
	 * @param l5 the l5 to set
	 */
	public void setL5(String l5) {
		this.l5 = l5;
	}
	/**
	 * @return the l6
	 */
	public String getL6() {
		return l6;
	}
	/**
	 * @param l6 the l6 to set
	 */
	public void setL6(String l6) {
		this.l6 = l6;
	}
	/**
	 * @return the l7
	 */
	public String getL7() {
		return l7;
	}
	/**
	 * @param l7 the l7 to set
	 */
	public void setL7(String l7) {
		this.l7 = l7;
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
		return true;
	}
}
