package fr.insee.pearljam.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
/**
* Entity InseeSampleIdentifier : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
public class InseeSampleIdentifier extends SampleIdentifier {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1044333540440142996L;
	@Column
	private Integer bs;
	@Column(length=1)
	private String ec;
	@Column
	private Integer le;
	@Column
	private Integer noi;
	@Column
	private Integer numfa;
	@Column
	private Integer rges; 
	@Column
	private Integer ssech; 
	@Column
	private Integer nolog; 
	@Column
	private Integer nole; 
	@Column(length=50)
	private String autre; 
	@Column(length=50)
	private String nograp;
	
	/**
	 * @return the bs
	 */
	public Integer getBs() {
		return bs;
	}
	/**
	 * @param bs the bs to set
	 */
	public void setBs(Integer bs) {
		this.bs = bs;
	}
	/**
	 * @return the ec
	 */
	public String getEc() {
		return ec;
	}
	/**
	 * @param ec the ec to set
	 */
	public void setEc(String ec) {
		this.ec = ec;
	}
	/**
	 * @return the le
	 */
	public Integer getLe() {
		return le;
	}
	/**
	 * @param le the le to set
	 */
	public void setLe(Integer le) {
		this.le = le;
	}
	/**
	 * @return the noi
	 */
	public Integer getNoi() {
		return noi;
	}
	/**
	 * @param noi the noi to set
	 */
	public void setNoi(Integer noi) {
		this.noi = noi;
	}
	/**
	 * @return the numfa
	 */
	public Integer getNumfa() {
		return numfa;
	}
	/**
	 * @param numfa the numfa to set
	 */
	public void setNumfa(Integer numfa) {
		this.numfa = numfa;
	}
	/**
	 * @return the rges
	 */
	public Integer getRges() {
		return rges;
	}
	/**
	 * @param rges the rges to set
	 */
	public void setRges(Integer rges) {
		this.rges = rges;
	}
	/**
	 * @return the ssech
	 */
	public Integer getSsech() {
		return ssech;
	}
	/**
	 * @param ssech the ssech to set
	 */
	public void setSsech(Integer ssech) {
		this.ssech = ssech;
	}
	/**
	 * @return the nolog
	 */
	public Integer getNolog() {
		return nolog;
	}
	/**
	 * @param nolog the nolog to set
	 */
	public void setNolog(Integer nolog) {
		this.nolog = nolog;
	}
	/**
	 * @return the nole
	 */
	public Integer getNole() {
		return nole;
	}
	/**
	 * @param nole the nole to set
	 */
	public void setNole(Integer nole) {
		this.nole = nole;
	}
	/**
	 * @return the autre
	 */
	public String getAutre() {
		return autre;
	}
	/**
	 * @param autre the autre to set
	 */
	public void setAutre(String autre) {
		this.autre = autre;
	}
	/**
	 * @return the nograp
	 */
	public String getNograp() {
		return nograp;
	}
	/**
	 * @param nograp the nograp to set
	 */
	public void setNograp(String nograp) {
		this.nograp = nograp;
	} 
}
