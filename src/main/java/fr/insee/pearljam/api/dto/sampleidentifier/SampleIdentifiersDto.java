package fr.insee.pearljam.api.dto.sampleidentifier;

import fr.insee.pearljam.api.domain.InseeSampleIdentifier;

public class SampleIdentifiersDto {
	private Integer bs;
	private String ec;
	private Integer le;
	private Integer noi;
	private Integer numfa;
	private Integer rges;
	private Integer ssech;
	private Integer nolog;
	private Integer nole;
	private String autre;
	private String nograp;

	public SampleIdentifiersDto(Integer bs, String ec, Integer le, Integer noi, Integer numfa, Integer rges,
			Integer ssech, Integer nolog, Integer nole, String autre, String nograp) {
		super();
		this.bs = bs;
		this.ec = ec;
		this.le = le;
		this.noi = noi;
		this.numfa = numfa;
		this.rges = rges;
		this.ssech = ssech;
		this.nolog = nolog;
		this.nole = nole;
		this.autre = autre;
		this.nograp = nograp;
	}
	
	public SampleIdentifiersDto(InseeSampleIdentifier si) {
		super();
		this.bs = si.getBs();
		this.ec = si.getEc();
		this.le = si.getLe();
		this.noi = si.getNoi();
		this.numfa = si.getNumfa();
		this.rges = si.getRges();
		this.ssech = si.getSsech();
		this.nolog = si.getNolog();
		this.nole = si.getNole();
		this.autre = si.getAutre();
		this.nograp = si.getNograp();
	}



	/**
	 * 
	 */
	public SampleIdentifiersDto() {
		super();
	}



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

	@Override
	public String toString() {
		return "SampleIdentifiersDto [bs=" + bs + ", ec=" + ec + ", le=" + le + ", noi=" + noi + ", numfa=" + numfa
				+ ", rges=" + rges + ", ssech=" + ssech + ", nolog=" + nolog + ", nole=" + nole + ", autre=" + autre
				+ ", nograp=" + nograp + "]";
	}

}
