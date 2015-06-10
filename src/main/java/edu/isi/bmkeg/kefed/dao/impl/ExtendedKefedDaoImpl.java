package edu.isi.bmkeg.kefed.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.isi.bmkeg.kefed.dao.ExtendedKefedDao;
import edu.isi.bmkeg.kefed.model.design.KefedModel;
import edu.isi.bmkeg.kefed.model.design.KefedModelEdge;
import edu.isi.bmkeg.kefed.model.design.KefedModelElement;
import edu.isi.bmkeg.kefed.model.qo.design.KefedModelEdge_qo;
import edu.isi.bmkeg.kefed.model.qo.design.KefedModelElement_qo;
import edu.isi.bmkeg.kefed.model.qo.design.KefedModel_qo;
import edu.isi.bmkeg.ooevv.dao.ExtendedOoevvDao;
import edu.isi.bmkeg.ooevv.dao.ExtendedOoevvDaoImpl;
import edu.isi.bmkeg.ooevv.model.ExperimentalVariable;
import edu.isi.bmkeg.ooevv.model.OoevvElement;
import edu.isi.bmkeg.ooevv.model.OoevvEntity;
import edu.isi.bmkeg.ooevv.model.OoevvProcess;
import edu.isi.bmkeg.ooevv.model.scale.MeasurementScale;
import edu.isi.bmkeg.ooevv.model.value.MeasurementValue;
import edu.isi.bmkeg.vpdmf.dao.CoreDao;
import edu.isi.bmkeg.vpdmf.dao.CoreDaoImpl;
import edu.isi.bmkeg.vpdmf.model.definitions.VPDMf;
import edu.isi.bmkeg.vpdmf.model.definitions.ViewDefinition;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;
import edu.isi.bmkeg.vpdmf.model.instances.PrimitiveInstance;
import edu.isi.bmkeg.vpdmf.model.instances.ViewBasedObjectGraph;
import edu.isi.bmkeg.vpdmf.model.instances.ViewInstance;

@Repository
@Transactional
public class ExtendedKefedDaoImpl implements ExtendedKefedDao {

	private static Logger logger = Logger.getLogger(ExtendedKefedDaoImpl.class);

	@Autowired
	private CoreDao coreDao;
	
	private ExtendedOoevvDao ooevvDao;
	
	public ExtendedKefedDaoImpl() throws Exception {}

	public void init(String login, String password, String uri, String wd) throws Exception {
		
		if( coreDao == null ) {
			this.coreDao = new CoreDaoImpl();
		}
		
		this.coreDao.init(login, password, uri, wd);
		initializeOoevvDao();
		
	}
	
	public CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}
	
	@Override
	public void initializeOoevvDao() throws Exception {
		
		if( this.ooevvDao == null ) {
			this.ooevvDao = new ExtendedOoevvDaoImpl(this.coreDao);	
		}
		
	}

	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
	public List<LightViewInstance> listAllKefedModels() throws Exception {

		return coreDao.list(new KefedModel_qo(), "KefedModel");

	}

	public void insertKefedModel(KefedModel kefed) throws Exception {
		
		try {

			coreDao.getCe().connectToDB();
			coreDao.getCe().turnOffAutoCommit();

			VPDMf top = coreDao.getTop();
			ClassLoader cl = coreDao.getCl();
			
			// Make lists of all OoEVV Elements in the KEfED model. 
			Map<String, ExperimentalVariable> expVariables = 
					new HashMap<String, ExperimentalVariable>();
			Map<String, OoevvProcess> ooevvProcesses = 
					new HashMap<String, OoevvProcess>();
			Map<String, OoevvEntity> ooevvEntities = 
					new HashMap<String, OoevvEntity>();
			Map<String, MeasurementScale > scales = 
					new HashMap<String, MeasurementScale>();
			Map<String, MeasurementValue> values = 
					new HashMap<String, MeasurementValue>();
			
			for( KefedModelElement el : kefed.getElements() ) {
				if( el.getDefn() != null ) {
					if(el.getDefn() instanceof ExperimentalVariable) {
						ExperimentalVariable ev = (ExperimentalVariable) el.getDefn();
						expVariables.put(ev.getShortTermId(), ev);
						MeasurementScale ms = ev.getScale();
						scales.put(ms.getShortTermId(), ms);
						values.putAll( 
								ExtendedOoevvDaoImpl.extractValuesFromVariable(ev) 
								);
					} 
					else if(el.getDefn() instanceof OoevvProcess) {
						OoevvProcess op = (OoevvProcess) el.getDefn();
						ooevvProcesses.put(op.getShortTermId(), op);
					}
					else if(el.getDefn() instanceof OoevvEntity) {
						OoevvEntity oe = (OoevvEntity) el.getDefn();
						ooevvEntities.put(oe.getShortTermId(), oe);
					}
				}
			}
			
			this.ooevvDao.insertSetOfOoevvProcesses(
					new HashSet<OoevvProcess>(ooevvProcesses.values())
					);
			this.ooevvDao.insertSetOfOoevvEntities(
					new HashSet<OoevvEntity>(ooevvEntities.values())
					);			
			this.ooevvDao.insertSetOfMeasurementValues(
					new HashSet<MeasurementValue>(values.values())
					);
			this.ooevvDao.insertSetOfMeasurementScales(
					new HashSet<MeasurementScale>(scales.values())
					);
			this.ooevvDao.insertSetOfExperimentalVariables(
					new HashSet<ExperimentalVariable>(expVariables.values())
					);

			
			//
			// 1. insert the KefedModel 
			//
			coreDao.insertInTrans(kefed, "KefedModel");
			
			//
			// 1. insert each model element of the KefedModel as needed
			//
			for( KefedModelElement el : kefed.getElements() ) {
				coreDao.insertInTrans(el, el.getElementType());					
			}
			
			//
			// 2. insert each kefed model edge as needed
			//
			for( KefedModelEdge ed: kefed.getEdges() ) {
				coreDao.insertInTrans(ed, "KefedModelEdge");
			}

			
			coreDao.getCe().commitTransaction();

		} catch (Exception e) {

			coreDao.getCe().rollbackTransaction();
			e.printStackTrace();
			throw e;

		} finally {

			coreDao.getCe().closeDbConnection();

		}
		
	}
	
	@Override
	public KefedModel retrieveModelFromUuid(String uuid) throws Exception {
		KefedModel m = new KefedModel();
		
		try {

			coreDao.getCe().connectToDB();
			coreDao.getCe().turnOffAutoCommit();

			KefedModel_qo qo = new KefedModel_qo();
			qo.setUuid(uuid);
			List<LightViewInstance> l = coreDao.listInTrans(qo, "KefedModel");
			
			if( l.size() == 1 ) {
				m = reconstructKefedModelinTrans(m, l.get(0).getVpdmfId() );
			} else if( l.size() > 1 ){
				throw new Exception(uuid + " ambiguous");
			} else {
				throw new Exception(uuid + " not found");				
			}
			
		} catch (Exception e) {

			e.printStackTrace();
			throw e;

		} finally {

			coreDao.getCe().closeDbConnection();

		}
		
		return m;
		
	}

	
	public KefedModel retrieveModel(Long id) throws Exception {

		KefedModel m = new KefedModel();
		
		try {

			coreDao.getCe().connectToDB();
			coreDao.getCe().turnOffAutoCommit();

			m = reconstructKefedModelinTrans(m, id);
			
		} catch (Exception e) {

			e.printStackTrace();
			throw e;

		} finally {

			coreDao.getCe().closeDbConnection();

		}
		
		return m;

	}

	private KefedModel reconstructKefedModelinTrans(KefedModel m, long id) throws Exception {
		
		VPDMf top = coreDao.getTop();
		ClassLoader cl = coreDao.getCl();
		
		Map<String,KefedModelElement> elLookup = new HashMap<String,KefedModelElement>();
		List<KefedModelElement> elList = new ArrayList<KefedModelElement>();
		
		//
		// 1. retrieve the KefedModel as a view
		// (this should contain all nodes and edges but no 
		//  extra details of the nodes)
		//
		m = coreDao.findByIdInTrans(id, m, "KefedModel");
					
		//
		// 2. fill out each model element of the KefedModel as needed
		//
		KefedModelElement_qo elQ = new KefedModelElement_qo();
		KefedModel_qo mQ = new KefedModel_qo();
		elQ.setModel( mQ );
		mQ.setUuid( m.getUuid() );
		
		Map<String, KefedModelElement> lookup = new HashMap<String, KefedModelElement>();
		
		List<LightViewInstance> nodeList = coreDao.list(elQ, "KefedModelElement"); 
		for( LightViewInstance lvi : nodeList ) {

			Map<String,String> objMap = lvi.readIndexTupleMap(top);

			KefedModelElement el = new KefedModelElement();
			String type = lvi.getDefName();
			el.setElementType(type);
			
			String uuid = objMap.get("[" + type + "]KefedModelElement|KefedModelElement.uuid");
			el.setUuid(uuid);
			lookup.put(uuid, el);

			String name = null;
			if( type.equals("EntityInstance") ) {
				name = objMap.get("[EntityInstance]OoevvEntity|Term.termValue");
			} else if( type.equals("ProcessInstance") ) {
				name = objMap.get("[ProcessInstance]OoevvProcess|Term.termValue");
			} else {
				name = objMap.get("[" + type + "]ExperimentalVariable|Term.termValue");
			}
			OoevvElement defn = new OoevvElement();
			defn.setTermValue(name);
			el.setDefn(defn);
			
			String xx = objMap.get("[" + type + "]KefedModelElement|KefedModelElement.x");
			el.setX(new Float(xx));

			String yy = objMap.get("[" + type + "]KefedModelElement|KefedModelElement.y");
			el.setY(new Float(yy));

			String ww = objMap.get("[" + type + "]KefedModelElement|KefedModelElement.w");
			el.setW(new Float(ww));

			String hh = objMap.get("[" + type + "]KefedModelElement|KefedModelElement.h");
			el.setH(new Float(hh));

			m.getElements().add(el);
			el.setModel( m );
		
		}
		

		//
		// 3. switch out old elements from edges, but keep the edges.
		//
		KefedModelEdge_qo edQ = new KefedModelEdge_qo();
		mQ = new KefedModel_qo();
		mQ.setUuid( m.getUuid() );
		edQ.setModel( mQ );
		
		List<LightViewInstance> edgeList = coreDao.list(edQ, "KefedModelEdge"); 
		for( LightViewInstance lvi : edgeList ) {

			Map<String,String> objMap = lvi.readIndexTupleMap(top);

			KefedModelEdge ed = new KefedModelEdge();
			
			String edgeUuid = objMap.get("[KefedModelEdge]KefedModelEdge|KefedModelEdge.uuid");
			ed.setUuid(edgeUuid);
			
			String sourceUuid = objMap.get("[KefedModelEdge]KefedModelElementIn|KefedModelElement.uuid");
			KefedModelElement source = lookup.get(sourceUuid);
			ed.setSource(source);
			
			String targetUuid = objMap.get("[KefedModelEdge]KefedModelElementOut|KefedModelElement.uuid");
			KefedModelElement target = lookup.get(targetUuid);
			ed.setTarget(target);

			m.getEdges().add(ed);
			ed.setModel( m );
		
		}
		return m;
	}
	
	
	public void saveModel(KefedModel kefed, long vpdmfId) throws Exception {
		
		try {

			coreDao.getCe().connectToDB();
			coreDao.getCe().turnOffAutoCommit();
			
			ViewInstance vi = coreDao.getCe().executeUIDQuery("KefedModel", vpdmfId );
			coreDao.getCe().storeViewInstanceForUpdate(vi);
			
			ViewBasedObjectGraph vbog = new ViewBasedObjectGraph(coreDao.getTop(), coreDao.getCl(), "KefedModel");
			vbog.viewToObjectGraph(vi);
			vbog.viewToObjectGraph(vi);
			Map<String, Object> objMap = vbog.getObjMap();
			Iterator<String> keyIt = objMap.keySet().iterator();
			while (keyIt.hasNext()) {
				String key = keyIt.next();
				PrimitiveInstance pi = (PrimitiveInstance) vi.getSubGraph()
						.getNodes().get(key);
				Object o = objMap.get(key);
				vbog.primitiveToObject(pi, o, true);
			}
			KefedModel exist = (KefedModel) vbog.readPrimaryObject();
			
			Set<Long> elToRemove = new HashSet<Long>();
			Set<Long> elToAdd = new HashSet<Long>();
			Set<Long> edToRemove = new HashSet<Long>();
			Set<Long> edToAdd = new HashSet<Long>();
			
			Map<Long,KefedModelElement> elKefed = this.createElementBmkegIdSet(kefed.getElements());
			Map<Long,KefedModelElement> elExist = this.createElementBmkegIdSet(exist.getElements());
			Map<Long,KefedModelEdge> edKefed = this.createEdgeBmkegIdSet(kefed.getEdges());
			Map<Long,KefedModelEdge> edExist = this.createEdgeBmkegIdSet(exist.getEdges());
			
			Iterator<KefedModelElement> elIt = kefed.getElements().iterator();
			while( elIt.hasNext() ) {
				KefedModelElement el = elIt.next();
				if( !elExist.keySet().contains(el.getVpdmfId()) ) {
					elToAdd.add(el.getVpdmfId());
				}
			}
			
			elIt = exist.getElements().iterator();
			while( elIt.hasNext() ) {
				KefedModelElement el = elIt.next();
				if( !elKefed.keySet().contains(el.getVpdmfId()) ) {
					elToRemove.add(el.getVpdmfId());
				}
			}

			Iterator<KefedModelEdge> edIt = kefed.getEdges().iterator();
			while( edIt.hasNext() ) {
				KefedModelEdge ed = edIt.next();
				if( !edExist.keySet().contains(ed.getVpdmfId()) ) {
					edToAdd.add(ed.getVpdmfId());
				}
			}

			edIt = exist.getEdges().iterator();
			while( edIt.hasNext() ) {
				KefedModelEdge ed = edIt.next();
				if( !edKefed.keySet().contains(ed.getVpdmfId()) ) {
					edToRemove.add(ed.getVpdmfId());
				}
			}
			
			//
			// 1. remove unused edges from database
			//
			Iterator<Long> it = edToRemove.iterator();
			while( it.hasNext() ) {
				long i = it.next();
				coreDao.getCe().executeDeleteQuery("KefedModelEdge", i);
				logger.debug("removing edge: " + i);
			}

			//
			// 2. remove unused elements from database
			//
			it = elToRemove.iterator();
			while( it.hasNext() ) {
				long i = it.next();
				KefedModelElement el = elExist.get(i);
				coreDao.getCe().executeDeleteQuery(el.getElementType(), i);
				logger.debug("removing node: " + i);				
			}
			
			//
			// 1. insert each model element of the KefedModel as needed
			//
			it = elToAdd.iterator();
			while( it.hasNext() ) {
				long i = it.next();
				KefedModelElement el = elKefed.get(i);
				
				vbog = new ViewBasedObjectGraph(coreDao.getTop(), coreDao.getCl(), el.getElementType());

				vi = vbog.objectGraphToView(el);
				objMap = vbog.getObjMap();

				coreDao.getCe().executeInsertQuery(vi);
				
				keyIt = objMap.keySet().iterator();
				while (keyIt.hasNext()) {
					String key = keyIt.next();
					PrimitiveInstance pi = (PrimitiveInstance) vi.getSubGraph()
							.getNodes().get(key);
					Object o = objMap.get(key);
					vbog.primitiveToObject(pi, o, true);
				}
				
			}

			//
			// 3. insert each kefed model edge as needed
			//
			it = edToAdd.iterator();
			while( it.hasNext() ) {
				long i = it.next();
				KefedModelEdge ed = edKefed.get(i);
				
				vbog = new ViewBasedObjectGraph(coreDao.getTop(), coreDao.getCl(), "KefedModelEdge");

				vi = vbog.objectGraphToView(ed);
				
				objMap = vbog.getObjMap();

				coreDao.getCe().executeInsertQuery(vi);

				keyIt = objMap.keySet().iterator();
				while (keyIt.hasNext()) {
					String key = keyIt.next();
					PrimitiveInstance pi = (PrimitiveInstance) vi.getSubGraph()
							.getNodes().get(key);
					Object o = objMap.get(key);
					vbog.primitiveToObject(pi, o, true);
				}
				
			}
			
			//
			// 5. update the KefedModel as a view
			//
			vbog = new ViewBasedObjectGraph(coreDao.getTop(), coreDao.getCl(), "KefedModel");
			vi = vbog.objectGraphToView(kefed);
			objMap = vbog.getObjMap();

			coreDao.getCe().executeUpdateQuery(vi);

			keyIt = objMap.keySet().iterator();
			while (keyIt.hasNext()) {
				String key = keyIt.next();
				PrimitiveInstance pi = (PrimitiveInstance) vi.getSubGraph()
						.getNodes().get(key);
				Object o = objMap.get(key);
				vbog.primitiveToObject(pi, o, true);
			}

			coreDao.getCe().commitTransaction();

		} catch (Exception e) {

			coreDao.getCe().rollbackTransaction();
			e.printStackTrace();
			throw e;

		} finally {

			coreDao.getCe().closeDbConnection();

		}
		
	}

	private Map<Long,KefedModelElement> createElementBmkegIdSet(List<KefedModelElement> elList) throws Exception {
		Map<Long,KefedModelElement> bmkegIds = new HashMap<Long,KefedModelElement>();
		Iterator<KefedModelElement> elIt = elList.iterator();
		while( elIt.hasNext() ) {
			KefedModelElement el = elIt.next();
			bmkegIds.put(el.getVpdmfId(),el);
		}
		return bmkegIds;
	}

	private Map<Long,KefedModelEdge> createEdgeBmkegIdSet(List<KefedModelEdge> edList) throws Exception {
		Map<Long,KefedModelEdge> bmkegIds = new HashMap<Long,KefedModelEdge>();
		Iterator<KefedModelEdge> edIt = edList.iterator();
		while( edIt.hasNext() ) {
			KefedModelEdge ed = edIt.next();
			bmkegIds.put(ed.getVpdmfId(),ed);
		}
		return bmkegIds;
		
	}
	
	public boolean deleteModel(Long uuid) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public <T> List<T> listObjectGraphsFromDb(T t) throws Exception {

		String viewName = t.getClass().getName();
		ViewBasedObjectGraph vbog = new ViewBasedObjectGraph(
				coreDao.getTop(), coreDao.getCl(), viewName
				);
		ViewDefinition vd = coreDao.getTop().getViews().get(viewName);
		ViewInstance qVi = new ViewInstance(vd);
		List<T> l = new ArrayList<T>();
		List<ViewInstance> viewList = coreDao.getCe().executeFullQuery(qVi);
		for (Iterator<ViewInstance> iterator = viewList.iterator(); iterator.hasNext();) {
			ViewInstance viewInstance = (ViewInstance) iterator.next();
			vbog.viewToObjectGraph(viewInstance);
			T oa = (T) vbog.readPrimaryObject();
		    l.add(oa);
		}
		return l;

	}

}
