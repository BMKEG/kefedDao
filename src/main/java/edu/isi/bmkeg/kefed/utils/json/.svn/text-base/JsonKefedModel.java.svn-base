package edu.isi.bmkeg.kefed.utils.json;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

public class JsonKefedModel {
	
	private String id;
	private String kefedVersion;
	private String diagramXML;
	private String type;
	private String _type;
	private String source;
	private String modelName;
	private String _uid;
	private String description;
	private String dateTime;
	
	private List<KefedObject> nodes = new ArrayList<KefedObject>();
	private List<KefedLink> edges = new ArrayList<KefedLink>();
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setKefedVersion(String kefedVersion) {
		this.kefedVersion = kefedVersion;
	}
	public String getKefedVersion() {
		return kefedVersion;
	}
	public void setDiagramXML(String diagramXML) {
		this.diagramXML = diagramXML;
	}
	public String getDiagramXML() {
		return diagramXML;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModelName() {
		return modelName;
	}
	public void set_uid(String _uid) {
		this._uid = _uid;
	}
	public String get_uid() {
		return _uid;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSource() {
		return source;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void set_type(String _type) {
		this._type = _type;
	}
	public String get_type() {
		return _type;
	}
	public void setNodes(List<KefedObject> nodes) {
		this.nodes = nodes;
	}
	public List<KefedObject> getNodes() {
		return nodes;
	}
	public void setEdges(List<KefedLink> edges) {
		this.edges = edges;
	}
	public List<KefedLink> getEdges() {
		return edges;
	}
	
	public KefedModel2 convertToKefedModel() throws Exception {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		KefedModel2 model = new KefedModel2();
		
		model.set_type(this._type);
		model.set_uid(this._uid);
		model.setDateTime(this.dateTime);
		model.setDescription(this.description);
		model.setDiagramXML(db.parse(new InputSource(new StringReader(this.diagramXML))));
		model.setId(this.id);
		model.setKefedVersion(this.kefedVersion);
		model.setModelName(this.modelName);
		model.setSource(source);
		model.setType(this.type);
				
		model.set_bEdges(this.edges);
		model.set_bNodes(this.nodes);
		
		return model;
		
	}
	

}
