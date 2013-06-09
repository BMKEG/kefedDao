package edu.isi.bmkeg.kefed.utils.json;

import java.util.ArrayList;
import java.util.List;

public class KefedFullValueTemplate {

	private String valueTypeName;
	private List<String> allowedValues = new ArrayList<String>();
	private List<KefedFieldValueTemplate> multipleSlotFields = new ArrayList<KefedFieldValueTemplate>();

	public String getValueTypeName() {
		return valueTypeName;
	}
	public void setValueTypeName(String valueTypeName) {
		this.valueTypeName = valueTypeName;
	}
	public List<String> getAllowedValues() {
		return allowedValues;
	}
	public void setAllowedValues(List<String> allowedValues) {
		this.allowedValues = allowedValues;
	}
	public List<KefedFieldValueTemplate> getMultipleSlotFields() {
		return multipleSlotFields;
	}
	public void setMultipleSlotFields(List<KefedFieldValueTemplate> multipleSlotFields) {
		this.multipleSlotFields = multipleSlotFields;
	}
	
	
	
}
