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
    
    private Long iniCount;
    
    private Long alaCount;
    
    private Long wamCount;
    
    private Long oosCount;
    
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
	 * @return the campaignDto
	 */
	public CampaignDto getCampaign() {
		return campaign;
	}

	/**
	 * @param campaignDto the campaignDto to set
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
	 * @return the refCount
	 */
	public Long getRefCount() {
		return refCount;
	}

	/**
	 * @return the impCount
	 */
	public Long getImpCount() {
		return impCount;
	}

	/**
	 * @return the iniCount
	 */
	public Long getIniCount() {
		return iniCount;
	}

	/**
	 * @return the alaCount
	 */
	public Long getAlaCount() {
		return alaCount;
	}

	/**
	 * @return the wamCount
	 */
	public Long getWamCount() {
		return wamCount;
	}

	/**
	 * @return the oosCount
	 */
	public Long getOosCount() {
		return oosCount;
	}

	/**
	 * @param inaCount the inaCount to set
	 */
	public void setInaCount(Long inaCount) {
		this.inaCount = inaCount;
	}

	/**
	 * @param refCount the refCount to set
	 */
	public void setRefCount(Long refCount) {
		this.refCount = refCount;
	}

	/**
	 * @param impCount the impCount to set
	 */
	public void setImpCount(Long impCount) {
		this.impCount = impCount;
	}

	/**
	 * @param iniCount the iniCount to set
	 */
	public void setIniCount(Long iniCount) {
		this.iniCount = iniCount;
	}

	/**
	 * @param alaCount the alaCount to set
	 */
	public void setAlaCount(Long alaCount) {
		this.alaCount = alaCount;
	}

	/**
	 * @param wamCount the wamCount to set
	 */
	public void setWamCount(Long wamCount) {
		this.wamCount = wamCount;
	}

	/**
	 * @param oosCount the oosCount to set
	 */
	public void setOosCount(Long oosCount) {
		this.oosCount = oosCount;
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
