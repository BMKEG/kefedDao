package edu.isi.bmkeg.kefed.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import edu.isi.bmkeg.kefed.model.design.ConstantInstance;
import edu.isi.bmkeg.kefed.model.design.EntityInstance;
import edu.isi.bmkeg.kefed.model.design.KefedModel;
import edu.isi.bmkeg.kefed.model.design.KefedModelEdge;
import edu.isi.bmkeg.kefed.model.design.KefedModelElement;
import edu.isi.bmkeg.kefed.model.design.MeasurementInstance;
import edu.isi.bmkeg.kefed.model.design.ParameterInstance;
import edu.isi.bmkeg.kefed.model.design.ProcessInstance;
import edu.isi.bmkeg.kefed.model.design.ProcessStartPoint;
import edu.isi.bmkeg.ooevv.model.ExperimentalVariable;
import edu.isi.bmkeg.ooevv.model.OoevvElementSet;
import edu.isi.bmkeg.ooevv.model.OoevvEntity;
import edu.isi.bmkeg.ooevv.model.OoevvProcess;

public class KefedModelFactory {

	public static KefedModel newKefedModelInstance(String id,
			OoevvElementSet oes) {

		KefedModel m = new KefedModel();
		m.setExptId(id);
		
		ProcessStartPoint psp = new ProcessStartPoint();
		psp.setElementType("ProcessStartPoint");
		psp.setUuid(UUID.randomUUID().toString());
		
		psp.setModelStartFlag(true);
		m.getElements().add(psp);
		psp.setModel(m);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		m.setDate(dateFormat.format(cal.getTime()));

		m.setOoevv(oes);

		return m;

	}

	public static ProcessInstance newProcessInstance(KefedModel m,
			OoevvProcess p) {

		ProcessInstance pi = new ProcessInstance();
		pi.setElementType("ProcessInstance");
		pi.setUuid(UUID.randomUUID().toString());
		pi.setModel(m);
		m.getElements().add(pi);
		pi.setDefn(p);

		return pi;

	}

	public static EntityInstance newEntityInstance(KefedModel m, OoevvEntity e) {

		EntityInstance ei = new EntityInstance();
		ei.setElementType("EntityInstance");
		ei.setUuid(UUID.randomUUID().toString());
		ei.setModel(m);
		m.getElements().add(ei);
		ei.setDefn(e);

		return ei;

	}

	public static MeasurementInstance newMeasurementInstance(KefedModel m,
			ExperimentalVariable v) {

		MeasurementInstance mi = new MeasurementInstance();
		mi.setElementType("MeasurementInstance");
		mi.setUuid(UUID.randomUUID().toString());
		mi.setModel(m);
		m.getElements().add(mi);
		mi.setDefn(v);

		return mi;

	}

	public static ParameterInstance newParameterInstance(KefedModel m,
			ExperimentalVariable v) {

		ParameterInstance pi = new ParameterInstance();
		pi.setElementType("ParameterInstance");
		pi.setUuid(UUID.randomUUID().toString());
		pi.setModel(m);
		m.getElements().add(pi);
		pi.setDefn(v);

		return pi;

	}

	public static ConstantInstance newConstantInstance(KefedModel m,
			ExperimentalVariable v) {

		ConstantInstance ci = new ConstantInstance();
		ci.setElementType("ConstantInstance");
		ci.setUuid(UUID.randomUUID().toString());
		ci.setModel(m);
		m.getElements().add(ci);
		ci.setDefn(v);

		return ci;

	}

	public static boolean addEdge(KefedModel m, String edgeType,
			String fromUuid, String toUuid) throws Exception {

		//
		// Check to make sure that both inNode & outNode are valid nodes in the
		// Graph
		//
		KefedModelElement fromEl = null;
		KefedModelElement toEl = null;
		Iterator<KefedModelElement> elIt = m.getElements().iterator();
		while (elIt.hasNext()) {
			KefedModelElement el = elIt.next();

			if (el.getUuid().equals(fromUuid)) {
				fromEl = el;
			} else if (el.getUuid().equals(toUuid)) {
				toEl = el;
			}

			if (fromEl != null && toEl != null)
				break;
		}

		if (fromEl == null || toEl == null) {
			throw new Exception("Can't add an edge to the graph, "
					+ "none or both of the nodes don't exist");
		}

		if (checkExistEdge(m, fromEl, toEl, edgeType))
			return false;

		addEdge(m, fromEl, toEl, edgeType);

		return true;

	}

	public static boolean addEdge(KefedModel m, 
			KefedModelElement fromEl, KefedModelElement toEl, String edgeType) throws Exception {

		KefedModelEdge edge = new KefedModelEdge();
		edge.setUuid(UUID.randomUUID().toString());
		edge.setEdgeType(edgeType);
		
		edge.setModel(m);
		m.getEdges().add(edge);

		fromEl.getOutgoing().add(edge);
		toEl.getIncoming().add(edge);

		edge.setSource(fromEl);
		edge.setTarget(toEl);

		return true;

	}

	public static boolean checkExistEdge(KefedModel m,
			KefedModelElement fromEl, KefedModelElement toEl, String edgeType) {

		boolean isExist = false;

		Iterator<KefedModelEdge> it = m.getEdges().iterator();
		while (it.hasNext()) {
			KefedModelEdge edge = it.next();

			if (edge.getSource() == fromEl && edge.getTarget() == toEl
					&& edge.getEdgeType().equals(edgeType)) {
				isExist = true;
				break;
			}

		}

		return isExist;

	}

	public static void deleteEdgeSetFromGraph(KefedModel m,
			Set<KefedModelEdge> edgeSet) throws Exception {

		Iterator<KefedModelEdge> it = edgeSet.iterator();
		while (it.hasNext()) {
			KefedModelEdge edge = it.next();
				
			KefedModelElement outEdgeNode = edge.getTarget();
			KefedModelElement inEdgeNode = edge.getSource();

			outEdgeNode.getIncoming().remove(edge);
			inEdgeNode.getOutgoing().remove(edge);

			m.getEdges().remove(edge);

		}

	}

	public static void deleteNodeFromGraph(KefedModel m, 
			KefedModelElement node) throws Exception {
		//
		// Make a Vector of all the edges involving this node.
		//
		Set<KefedModelEdge> edgeSet = new HashSet<KefedModelEdge>();
		edgeSet.addAll(node.getIncoming());
		edgeSet.addAll(node.getOutgoing());

		deleteEdgeSetFromGraph(m, edgeSet);
		m.getElements().remove(node);

	}

	/**
	 * Note that we are using a very old version of JGraphT to do this. There
	 * may well be a better library.
	 * 
	 * @return
	 */
	public static UndirectedGraph<KefedModelElement, DefaultEdge> dumpToJGraphT(
			KefedModel m) {

		UndirectedGraph<KefedModelElement, DefaultEdge> g = new SimpleGraph<KefedModelElement, DefaultEdge>(
				DefaultEdge.class);

		Iterator<KefedModelElement> nIt = m.getElements().iterator();
		while (nIt.hasNext()) {
			KefedModelElement n = nIt.next();
			g.addVertex(n);
		}

		Iterator<KefedModelEdge> eIt = m.getEdges().iterator();
		while (eIt.hasNext()) {
			KefedModelEdge e = eIt.next();

			g.addEdge(e.getSource(), e.getTarget());
		}

		return g;

	}
	
	public static ProcessStartPoint getProcessStartPoint(KefedModel m) {
		
		Iterator<KefedModelElement> it = m.getElements().iterator();
		while(it.hasNext()) {
			KefedModelElement el = it.next();
			
			if( el instanceof ProcessStartPoint ) {
				ProcessStartPoint psp = (ProcessStartPoint) el;
				if( psp.getModelStartFlag() )
					return psp;
			}
			
		}
		
		return null;
	
	}
	
/*	public static KefedModel generateRandomKefedModel(String name, OoevvElementSet oes, ArticleCitationPlaceholder cite) throws Exception {
		
		KefedModel m = KefedModelFactory.newKefedModelInstance(name, oes, cite);
		ProcessStartPoint psp = KefedModelFactory.getProcessStartPoint(m);
		
		Random r = new Random();
		
		OoevvElement e = oes.getElements().get(r.nextInt(oes.getElements().size())); 
		while( !(e instanceof OoevvEntity) ) {			
			OoevvElement e = oes.getElements().get(r.nextInt(oes.getElements().size())); 
			
			
		}
		
		EntityInstance ei = KefedModelFactory.newEntityInstance(m, e);
		KefedModelFactory.addEdge(m, psp, ei, "start");
		
		KefedModelElement last = ei;
				
		int n = r.nextInt(8) + 2;
		for( int i=0; i<n; i++) {
					
			OoevvProcess p = oes.getProcesses().get(r.nextInt(oes.getProcesses().size())); 
			ProcessInstance pi = KefedModelFactory.newProcessInstance(m, p);
		
			KefedModelFactory.addEdge(m, last, pi, "temp");
			
			ExperimentalVariable vp = oes.getExptVbs().get(r.nextInt(oes.getExptVbs().size())); 
			ParameterInstance ppi = KefedModelFactory.newParameterInstance(m, vp);			
			KefedModelFactory.addEdge(m, ppi, pi, "parameterizes");

			MeasurementInstance mi = KefedModelFactory.newMeasurementInstance(m, vp);
			KefedModelFactory.addEdge(m, pi, mi, "contains");
			
			e = oes.getEntities().get(r.nextInt(oes.getEntities().size())); 
			ei = KefedModelFactory.newEntityInstance(m, e);
			last = ei;
		
		}
		
		return m;		
				
	}*/

}
