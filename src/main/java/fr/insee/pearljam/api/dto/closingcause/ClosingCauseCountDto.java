package fr.insee.pearljam.api.dto.closingcause;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.constants.Constants;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClosingCauseCountDto {
	private Long npiCount;
	private Long npaCount;
	private Long rowCount;
	private Long total;
       
    public ClosingCauseCountDto() {
    	super();
    }
    
    public ClosingCauseCountDto(Map<String, BigInteger> obj) {
    	if (obj != null && !obj.isEmpty()) {
    		for(String str : Constants.CLOSING_CAUSE_FIELDS) {
				try {
					setLongField(str, obj.get(str)!=null?obj.get(str).longValue():0L);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				} 
			}
		}
    	
    }

	/**
	 * @return the npiCount
	 */
	public Long getNpiCount() {
		return npiCount;
	}

	/**
	 * @param npiCount the npiCount to set
	 */
	public void setNpiCount(Long npiCount) {
		this.npiCount = npiCount;
	}

	/**
	 * @return the npaCount
	 */
	public Long getNpaCount() {
		return npaCount;
	}

	/**
	 * @param npaCount the npaCount to set
	 */
	public void setNpaCount(Long npaCount) {
		this.npaCount = npaCount;
	}

	/**
	 * @return the rowCount
	 */
	public Long getRowCount() {
		return rowCount;
	}

	/**
	 * @param rowCount the rowCount to set
	 */
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
	
	public void setLongField(String fieldName, Long value)
	        throws NoSuchFieldException, IllegalAccessException {
	    Field field = getClass().getDeclaredField(fieldName);
	    field.set(this, value);
	}

}
