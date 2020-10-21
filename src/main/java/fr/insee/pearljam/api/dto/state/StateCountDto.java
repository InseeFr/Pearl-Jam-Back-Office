package fr.insee.pearljam.api.dto.state;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StateCountDto {

	private String idDem;
    private String labelDem;
    private InterviewerDto interviewer;
    private CampaignDto campaign;
	private Long nvmCount;
	private Long nnsCount;
	private Long anvCount;
	private Long vinCount;
	private Long vicCount;
	private Long prcCount;
	private Long aocCount;
	private Long apsCount;
	private Long insCount;
	private Long wftCount;
	private Long wfsCount;
	private Long tbrCount;
	private Long finCount;
	private Long qnaCount;
	private Long qnaFinCount;
	private Long nvaCount;
	private Long total;

	

	public StateCountDto(List<Object[]> obj) {
		super();
		if (obj != null && !obj.isEmpty() && obj.get(0).length > 13 && obj.get(0)[0] != null) {
			this.nvmCount = ((java.math.BigInteger) obj.get(0)[0]).longValue();
			this.nnsCount = ((java.math.BigInteger) obj.get(0)[1]).longValue();
			this.anvCount = ((java.math.BigInteger) obj.get(0)[2]).longValue();
			this.vinCount = ((java.math.BigInteger) obj.get(0)[3]).longValue();
			this.vicCount = ((java.math.BigInteger) obj.get(0)[4]).longValue();
			this.prcCount = ((java.math.BigInteger) obj.get(0)[5]).longValue();
			this.aocCount = ((java.math.BigInteger) obj.get(0)[6]).longValue();
			this.apsCount = ((java.math.BigInteger) obj.get(0)[7]).longValue();
			this.insCount = ((java.math.BigInteger) obj.get(0)[8]).longValue();
			this.wftCount = ((java.math.BigInteger) obj.get(0)[9]).longValue();
			this.wfsCount = ((java.math.BigInteger) obj.get(0)[10]).longValue();
			this.tbrCount = ((java.math.BigInteger) obj.get(0)[11]).longValue();
			this.finCount = ((java.math.BigInteger) obj.get(0)[12]).longValue();
			this.qnaCount = ((java.math.BigInteger) obj.get(0)[13]).longValue();
			this.qnaFinCount = ((java.math.BigInteger) obj.get(0)[14]).longValue();
			this.nvaCount = ((java.math.BigInteger) obj.get(0)[15]).longValue();
			this.total = ((java.math.BigInteger) obj.get(0)[16]).longValue();
		}
	}

	public StateCountDto(String idDem, String labelDem, List<Object[]> obj) {
		super();
		if (obj != null && !obj.isEmpty() && obj.get(0).length > 13 && obj.get(0)[0] != null) {
			this.nvmCount = ((java.math.BigInteger) obj.get(0)[0]).longValue();
			this.nnsCount = ((java.math.BigInteger) obj.get(0)[1]).longValue();
			this.anvCount = ((java.math.BigInteger) obj.get(0)[2]).longValue();
			this.vinCount = ((java.math.BigInteger) obj.get(0)[3]).longValue();
			this.vicCount = ((java.math.BigInteger) obj.get(0)[4]).longValue();
			this.prcCount = ((java.math.BigInteger) obj.get(0)[5]).longValue();
			this.aocCount = ((java.math.BigInteger) obj.get(0)[6]).longValue();
			this.apsCount = ((java.math.BigInteger) obj.get(0)[7]).longValue();
			this.insCount = ((java.math.BigInteger) obj.get(0)[8]).longValue();
			this.wftCount = ((java.math.BigInteger) obj.get(0)[9]).longValue();
			this.wfsCount = ((java.math.BigInteger) obj.get(0)[10]).longValue();
			this.tbrCount = ((java.math.BigInteger) obj.get(0)[11]).longValue();
			this.finCount = ((java.math.BigInteger) obj.get(0)[12]).longValue();
			this.qnaCount = ((java.math.BigInteger) obj.get(0)[13]).longValue();
			this.qnaFinCount = ((java.math.BigInteger) obj.get(0)[14]).longValue();
			this.nvaCount = ((java.math.BigInteger) obj.get(0)[15]).longValue();
			this.total = ((java.math.BigInteger) obj.get(0)[16]).longValue();
		}
		this.idDem = idDem;
		this.setLabelDem(labelDem);
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
	 * @return the nnsCount
	 */
	public Long getNnsCount() {
		return nnsCount;
	}

	/**
	 * @param nnsCount the nnsCount to set
	 */
	public void setNnsCount(Long nnsCount) {
		this.nnsCount = nnsCount;
	}

	/**
	 * @return the anvCount
	 */
	public Long getAnvCount() {
		return anvCount;
	}

	/**
	 * @param anvCount the anvCount to set
	 */
	public void setAnvCount(Long anvCount) {
		this.anvCount = anvCount;
	}

	/**
	 * @return the vinCount
	 */
	public Long getVinCount() {
		return vinCount;
	}

	/**
	 * @param vinCount the vinCount to set
	 */
	public void setVinCount(Long vinCount) {
		this.vinCount = vinCount;
	}

	/**
	 * @return the vicCount
	 */
	public Long getVicCount() {
		return vicCount;
	}

	/**
	 * @param vicCount the vicCount to set
	 */
	public void setVicCount(Long vicCount) {
		this.vicCount = vicCount;
	}

	/**
	 * @return the prcCount
	 */
	public Long getPrcCount() {
		return prcCount;
	}

	/**
	 * @param prcCount the prcCount to set
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
	 * @return the qnaCount
	 */
	public Long getQnaCount() {
		return qnaCount;
	}

	/**
	 * @param qnaCount the qnaCount to set
	 */
	public void setQnaCount(Long qnaCount) {
		this.qnaCount = qnaCount;
	}
	
	/**
	 * @return the qnaFinCount
	 */
	public Long getQnaFinCount() {
		return qnaFinCount;
	}

	/**
	 * @param qnaCount the qnaCount to set
	 */
	public void setQnaFinCount(Long qnaFinCount) {
		this.qnaFinCount = qnaFinCount;
	}

	/**
	 * @return the nvaCount
	 */
	public Long getNvaCount() {
		return nvaCount;
	}

	/**
	 * @param nvaCount the nvaCount to set
	 */
	public void setNvaCount(Long nvaCount) {
		this.nvaCount = nvaCount;
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
		return "StateCountDto [ansCount=" + anvCount + ", prcCount=" + prcCount + "]";
	}

	public String getLabelDem() {
		return labelDem;
	}

	public void setLabelDem(String labelDem) {
		this.labelDem = labelDem;
	}

	public CampaignDto getCampaign() {
		return campaign;
	}

	public void setCampaign(CampaignDto campaign) {
		this.campaign = campaign;
	}

	public InterviewerDto getInterviewer() {
		return interviewer;
	}

	public void setInterviewer(InterviewerDto interviewer) {
		this.interviewer = interviewer;
	}

}
