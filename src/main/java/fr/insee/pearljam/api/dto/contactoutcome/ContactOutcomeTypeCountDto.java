package fr.insee.pearljam.api.dto.contactoutcome;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactOutcomeTypeCountDto {
	
	private String idDem;
	
    private String labelDem;
    
	private CampaignDto campaign;
	
	private Long inaCount;
	
    private Long refCount;
    
    private Long impCount;
    
    private Long ucdCount;
    
    private Long utrCount;
    
    private Long alaCount;
    
    private Long acpCount;

    private Long dcdCount;

    private Long nuhCount;

    private Long nerCount;
    
    private Long total;
       
    public ContactOutcomeTypeCountDto() {
    	super();
    }
    
    public ContactOutcomeTypeCountDto(Map<String, BigInteger> obj) {
    	if (obj != null && !obj.isEmpty()) {
    		for(String str : Constants.CONTACT_OUTCOME_FIELDS) {
				try {
					setLongField(str, obj.get(str)!=null?obj.get(str).longValue():0L);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				} 
			}
		}
    	
    }
    
    public ContactOutcomeTypeCountDto(Map<String, BigInteger> obj, CampaignDto campaign) {
    	this.campaign = campaign;
    	if (obj != null && !obj.isEmpty()) {
    		for(String str : Constants.CONTACT_OUTCOME_FIELDS) {
				try {
					setLongField(str, obj.get(str)!=null?obj.get(str).longValue():0L);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				} 
			}
		}
    	
    }
    
    public ContactOutcomeTypeCountDto(String idDem, String labelDem, Map<String, BigInteger> obj) {
		this(obj);
		this.idDem = idDem;
		this.setLabelDem(labelDem);
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
	 * @return the labelDem
	 */
	public String getLabelDem() {
		return labelDem;
	}

	/**
	 * @param labelDem the labelDem to set
	 */
	public void setLabelDem(String labelDem) {
		this.labelDem = labelDem;
	}

	/**
	 * @return the campaign
	 */
	public CampaignDto getCampaign() {
		return campaign;
	}

	/**
	 * @param campaign the campaign to set
	 */
	public void setCampaign(CampaignDto campaign) {
		this.campaign = campaign;
	}

	/**
	 * @return the inaCount
	 */
	public Long getInaCount() {
		return inaCount;
	}

	/**
	 * @param inaCount the inaCount to set
	 */
	public void setInaCount(Long inaCount) {
		this.inaCount = inaCount;
	}

	/**
	 * @return the refCount
	 */
	public Long getRefCount() {
		return refCount;
	}

	/**
	 * @param refCount the refCount to set
	 */
	public void setRefCount(Long refCount) {
		this.refCount = refCount;
	}

	/**
	 * @return the impCount
	 */
	public Long getImpCount() {
		return impCount;
	}

	/**
	 * @param impCount the impCount to set
	 */
	public void setImpCount(Long impCount) {
		this.impCount = impCount;
	}

	/**
	 * @return the ucdCount
	 */
	public Long getUcdCount() {
		return ucdCount;
	}

	/**
	 * @param ucdCount the ucdCount to set
	 */
	public void setUcdCount(Long ucdCount) {
		this.ucdCount = ucdCount;
	}

	/**
	 * @return the utrCount
	 */
	public Long getUtrCount() {
		return utrCount;
	}

	/**
	 * @param utrCount the utrCount to set
	 */
	public void setUtrCount(Long utrCount) {
		this.utrCount = utrCount;
	}

	/**
	 * @return the alaCount
	 */
	public Long getAlaCount() {
		return alaCount;
	}

	/**
	 * @param alaCount the alaCount to set
	 */
	public void setAlaCount(Long alaCount) {
		this.alaCount = alaCount;
	}

	/**
	 * @return the acpCount
	 */
	public Long getAcpCount() {
		return acpCount;
	}

	/**
	 * @param acpCount the acpCount to set
	 */
	public void setAcpCount(Long acpCount) {
		this.acpCount = acpCount;
	}

	/**
	 * @return the dcdCount
	 */
	public Long getDcdCount() {
		return dcdCount;
	}

	/**
	 * @param dcdCount the dcdCount to set
	 */
	public void setDcdCount(Long dcdCount) {
		this.dcdCount = dcdCount;
	}

	/**
	 * @return the nuhCount
	 */
	public Long getNuhCount() {
		return nuhCount;
	}

	/**
	 * @param nuhCount the nuhCount to set
	 */
	public void setNuhCount(Long nuhCount) {
		this.nuhCount = nuhCount;
	}

	/**
	 * @return the nerCount
	 */
	public Long getNerCount() {
		return nerCount;
	}

	/**
	 * @param nerCount the nerCount to set
	 */
	public void setNerCount(Long nerCount) {
		this.nerCount = nerCount;
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

	public void setLongField(String fieldName, Long value)
	        throws NoSuchFieldException, IllegalAccessException {
	    Field field = getClass().getDeclaredField(fieldName);
	    field.set(this, value);
	}

}
