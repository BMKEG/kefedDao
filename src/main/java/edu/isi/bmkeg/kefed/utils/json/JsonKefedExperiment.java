package edu.isi.bmkeg.kefed.utils.json;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.xml.sax.InputSource;

public class JsonKefedExperiment extends JsonKefedModel {
			
	private Map<String,Map<String,String>> experimentData;

	public Map<String,Map<String,String>> getExperimentalData() {
		return experimentData;
	}

	public void setExperimentData(Map<String,Map<String,String>> experimentData) {
		this.experimentData = experimentData;
	}
	
	public JsonKefedExperiment() {
		super();
		this.experimentData = new HashMap<String,Map<String,String>>();
	}
	

}
