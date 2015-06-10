package edu.isi.bmkeg.kefed.utils.json;

public class KefedFieldValueTemplate {

	private String nameValue;

	private KefedFullValueTemplate valueType;

	private String uid;

	public KefedFullValueTemplate getValueType() {
		return valueType;
	}

	public void setValueType(KefedFullValueTemplate valueType) {
		this.valueType = valueType;
	}

	public String getNameValue() {
		return nameValue;
	}

	public void setNameValue(String nameValue) {
		this.nameValue = nameValue;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
}
