package fr.insee.pearljam.api.dto.state;

import java.util.List;

public class StateCountDto {

	private String idDem;
	private Long nnsCount;
	private Long ansCount;
	private Long vicCount;
	private Long prcCount;
	private Long aocCount;
	private Long apsCount;
	private Long insCount;
	private Long wftCount;
	private Long wfsCount;
	private Long tbrCount;
	private Long finCount;
	private Long nviCount;
	private Long nvmCount;
	private Long total;

	public StateCountDto(Long nnsCount, Long ansCount, Long vicCount, Long prcCount, Long aocCount, Long apsCount,
			Long insCount, Long wftCount, Long wfsCount, Long tbrCount, Long finCount, Long nviCount, Long nvmCount,
			Long total) {
		super();
		this.nnsCount = nnsCount;
		this.ansCount = ansCount;
		this.vicCount = vicCount;
		this.prcCount = prcCount;
		this.aocCount = aocCount;
		this.apsCount = apsCount;
		this.insCount = insCount;
		this.wftCount = wftCount;
		this.wfsCount = wfsCount;
		this.tbrCount = tbrCount;
		this.finCount = finCount;
		this.nviCount = nviCount;
		this.nvmCount = nvmCount;
		this.total = total;
	}

	public StateCountDto(List<Object[]> obj) {
		super();
		if (obj != null && !obj.isEmpty() && obj.get(0).length > 13 && obj.get(0)[0] != null) {
			this.nnsCount = ((java.math.BigInteger) obj.get(0)[0]).longValue();
			this.ansCount = ((java.math.BigInteger) obj.get(0)[1]).longValue();
			this.vicCount = ((java.math.BigInteger) obj.get(0)[2]).longValue();
			this.prcCount = ((java.math.BigInteger) obj.get(0)[3]).longValue();
			this.aocCount = ((java.math.BigInteger) obj.get(0)[4]).longValue();
			this.apsCount = ((java.math.BigInteger) obj.get(0)[5]).longValue();
			this.insCount = ((java.math.BigInteger) obj.get(0)[6]).longValue();
			this.wftCount = ((java.math.BigInteger) obj.get(0)[7]).longValue();
			this.wfsCount = ((java.math.BigInteger) obj.get(0)[8]).longValue();
			this.tbrCount = ((java.math.BigInteger) obj.get(0)[9]).longValue();
			this.finCount = ((java.math.BigInteger) obj.get(0)[10]).longValue();
			this.nviCount = ((java.math.BigInteger) obj.get(0)[11]).longValue();
			this.nvmCount = ((java.math.BigInteger) obj.get(0)[12]).longValue();
			this.total = ((java.math.BigInteger) obj.get(0)[13]).longValue();
		}
	}

	public StateCountDto(String idDem, List<Object[]> obj) {
		super();
		if (obj != null && !obj.isEmpty() && obj.get(0).length > 13 && obj.get(0)[0] != null) {
			this.nnsCount = ((java.math.BigInteger) obj.get(0)[0]).longValue();
			this.ansCount = ((java.math.BigInteger) obj.get(0)[1]).longValue();
			this.vicCount = ((java.math.BigInteger) obj.get(0)[2]).longValue();
			this.prcCount = ((java.math.BigInteger) obj.get(0)[3]).longValue();
			this.aocCount = ((java.math.BigInteger) obj.get(0)[4]).longValue();
			this.apsCount = ((java.math.BigInteger) obj.get(0)[5]).longValue();
			this.insCount = ((java.math.BigInteger) obj.get(0)[6]).longValue();
			this.wftCount = ((java.math.BigInteger) obj.get(0)[7]).longValue();
			this.wfsCount = ((java.math.BigInteger) obj.get(0)[8]).longValue();
			this.tbrCount = ((java.math.BigInteger) obj.get(0)[9]).longValue();
			this.finCount = ((java.math.BigInteger) obj.get(0)[10]).longValue();
			this.nviCount = ((java.math.BigInteger) obj.get(0)[11]).longValue();
			this.nvmCount = ((java.math.BigInteger) obj.get(0)[12]).longValue();
			this.total = ((java.math.BigInteger) obj.get(0)[13]).longValue();
		}
		this.idDem = idDem;
	}

	public StateCountDto() {
		super();
	}

	/**
	 * @return the idDem
	 */
	public String getIdDem() {
		return idDem;
	}

	/**
	 * @param idDem the idDem to set
	 */
	public void setIdDem(String idDem) {
		this.idDem = idDem;
	}

	/**
	 * @return the id
	 */
	public Long getNnsCount() {
		return nnsCount;
	}

	/**
	 * @param nnsCount the id to set
	 */
	public void setNnsCount(Long nnsCount) {
		this.nnsCount = nnsCount;
	}

	/**
	 * @return the id
	 */
	public Long getAnsCount() {
		return ansCount;
	}

	/**
	 * @param ansCount the id to set
	 */
	public void setAnsCount(Long ansCount) {
		this.ansCount = ansCount;
	}

	/**
	 * @return the id
	 */
	public Long getVicCount() {
		return vicCount;
	}

	/**
	 * @param vicCount the id to set
	 */
	public void setVicCount(Long vicCount) {
		this.vicCount = vicCount;
	}

	/**
	 * @return the date
	 */
	public Long getPrcCount() {
		return prcCount;
	}

	/**
	 * @param prcCount the date to set
	 */
	public void setPrcCount(Long prcCount) {
		this.prcCount = prcCount;
	}

	/**
	 * @return the aocCount
	 */
	public Long getAocCount() {
		return aocCount;
	}

	/**
	 * @param aocCount the aocCount to set
	 */
	public void setAocCount(Long aocCount) {
		this.aocCount = aocCount;
	}

	/**
	 * @return the apsCount
	 */
	public Long getApsCount() {
		return apsCount;
	}

	/**
	 * @param apsCount the apsCount to set
	 */
	public void setApsCount(Long apsCount) {
		this.apsCount = apsCount;
	}

	/**
	 * @return the insCount
	 */
	public Long getInsCount() {
		return insCount;
	}

	/**
	 * @param insCount the insCount to set
	 */
	public void setInsCount(Long insCount) {
		this.insCount = insCount;
	}

	/**
	 * @return the wftCount
	 */
	public Long getWftCount() {
		return wftCount;
	}

	/**
	 * @param wftCount the wftCount to set
	 */
	public void setWftCount(Long wftCount) {
		this.wftCount = wftCount;
	}

	/**
	 * @return the wfsCount
	 */
	public Long getWfsCount() {
		return wfsCount;
	}

	/**
	 * @param wfsCount the wfsCount to set
	 */
	public void setWfsCount(Long wfsCount) {
		this.wfsCount = wfsCount;
	}

	/**
	 * @return the tbrCount
	 */
	public Long getTbrCount() {
		return tbrCount;
	}

	/**
	 * @param tbrCount the tbrCount to set
	 */
	public void setTbrCount(Long tbrCount) {
		this.tbrCount = tbrCount;
	}

	/**
	 * @return the finCount
	 */
	public Long getFinCount() {
		return finCount;
	}

	/**
	 * @param finCount the finCount to set
	 */
	public void setFinCount(Long finCount) {
		this.finCount = finCount;
	}

	/**
	 * @return the nviCount
	 */
	public Long getNviCount() {
		return nviCount;
	}

	/**
	 * @param nviCount the nviCount to set
	 */
	public void setNviCount(Long nviCount) {
		this.nviCount = nviCount;
	}

	/**
	 * @return the nvmCount
	 */
	public Long getNvmCount() {
		return nvmCount;
	}

	/**
	 * @param nvmCount the nvmCount to set
	 */
	public void setNvmCount(Long nvmCount) {
		this.nvmCount = nvmCount;
	}

	/**
	 * @return the total
	 */
	public Long getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Long total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "StateCountDto [ansCount=" + ansCount + ", prcCount=" + prcCount + "]";
	}

}
