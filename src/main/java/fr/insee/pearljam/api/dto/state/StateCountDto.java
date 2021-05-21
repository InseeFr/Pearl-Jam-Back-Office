package fr.insee.pearljam.api.dto.state;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.constants.Constants;
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
	private Long npaCount;
	private Long npiCount;
	private Long rowCount;
	private Long total;

	public StateCountDto(Map<String, BigInteger> obj) {
		super();
		if (obj != null && !obj.isEmpty()) {
			for(String str : Constants.STATE_COUNT_FIELDS) {
				try {
					setLongField(str, obj.get(str)!=null ? obj.get(str).longValue() : 0L);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				} 
			}
		}
	}

	public StateCountDto(String idDem, String labelDem, Map<String, BigInteger> obj) {
		this(obj);
		this.idDem = idDem;
		this.setLabelDem(labelDem);
	}
	


	public StateCountDto() {
		super();
	}
	
	public void addClosingCauseCount(Map<String, BigInteger> obj) {
		if (obj != null && !obj.isEmpty()) {
			for(String str : Constants.STATECOUNT_CLOSED_CLOSING_CAUSE_FIELDS) {
				try {
					setLongField(str, obj.get(str) != null ? obj.get(str).longValue() : 0L);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				} 
			}
		}
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
	
	public Long getNpaCount() {
		return npaCount;
	}

	public void setNpaCount(Long npaCount) {
		this.npaCount = npaCount;
	}

	public Long getNpiCount() {
		return npiCount;
	}

	public void setNpiCount(Long npiCount) {
		this.npiCount = npiCount;
	}

	public Long getRowCount() {
		return rowCount;
	}

	public void setRowCount(Long rowCount) {
		this.rowCount = rowCount;
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
	
	public void setLongField(String fieldName, Long value)
	        throws NoSuchFieldException, IllegalAccessException {
	    Field field = getClass().getDeclaredField(fieldName);
	    field.set(this, value);
	}

}
