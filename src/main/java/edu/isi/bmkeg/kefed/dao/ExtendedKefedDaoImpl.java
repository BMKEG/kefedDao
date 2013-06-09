package edu.isi.bmkeg.kefed.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import edu.isi.bmkeg.kefed.model.design.KefedModel;
import edu.isi.bmkeg.kefed.model.design.KefedModelEdge;
import edu.isi.bmkeg.kefed.model.design.KefedModelElement;
import edu.isi.bmkeg.vpdmf.dao.CoreDao;
import edu.isi.bmkeg.vpdmf.model.definitions.VPDMf;
import edu.isi.bmkeg.vpdmf.model.definitions.ViewDefinition;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;
import edu.isi.bmkeg.vpdmf.model.instances.PrimitiveInstance;
import edu.isi.bmkeg.vpdmf.model.instances.ViewBasedObjectGraph;
import edu.isi.bmkeg.vpdmf.model.instances.ViewInstance;

public class ExtendedKefedDaoImpl implements ExtendedKefedDao {

	private static Logger logger = Logger.getLogger(ExtendedKefedDaoImpl.class);

	@Autowired
	private CoreDao coreDao;
	
	public ExtendedKefedDaoImpl() throws Exception {

		super();
	
	}
	
	public List<LightViewInstance> listAllKefedModels() throws Exception {

		return coreDao.listAllViews("KefedModel");

	}

	public void insertKefedModel(KefedModel kefed) throws Exception {
		
		try {

			coreDao.getCe().connectToDB();
			coreDao.getCe().turnOffAutoCommit();

			VPDMf top = coreDao.getTop();
			ClassLoader cl = coreDao.getCl();
			
			//
			// 1. insert each model element of the KefedModel as needed
			//
			Iterator<KefedModelElement> elIt = kefed.getElements().iterator();
			while( elIt.hasNext() ) {
				KefedModelElement el = elIt.next();
				
				ViewBasedObjectGraph vbog = new ViewBasedObjectGraph(top, cl, el.getElementType());

				ViewInstance vi = vbog.objectGraphToView(el);
				Map<String, Object> objMap = vbog.getObjMap();

				coreDao.getCe().executeInsertQuery(vi);

				Iterator<String> keyIt = objMap.keySet().iterator();
				while (keyIt.hasNext()) {
					String key = keyIt.next();
					PrimitiveInstance pi = (PrimitiveInstance) vi.getSubGraph()
							.getNodes().get(key);
					Object o = objMap.get(key);
					vbog.primitiveToObject(pi, o, true);
				}
				
			}
			
			//
			// 2. insert each kefed model edge as needed
			//
			Iterator<KefedModelEdge> edIt = kefed.getEdges().iterator();
			while( edIt.hasNext() ) {
				KefedModelEdge ed = edIt.next();
				
				ViewBasedObjectGraph vbog = new ViewBasedObjectGraph(top, cl, "KefedModelEdge");

				ViewInstance vi = vi = vbog.objectGraphToView(ed);
				Map<String, Object> objMap = vbog.getObjMap();

				coreDao.getCe().executeInsertQuery(vi);

				Iterator<String> keyIt = objMap.keySet().iterator();
				while (keyIt.hasNext()) {
					String key = keyIt.next();
					PrimitiveInstance pi = (PrimitiveInstance) vi.getSubGraph()
							.getNodes().get(key);
					Object o = objMap.get(key);
					vbog.primitiveToObject(pi, o, true);
				}
				
			}

			//
			// 3. insert the KefedModel as a view
			//
			ViewBasedObjectGraph vbog = new ViewBasedObjectGraph(top, cl, "KefedModel");
			ViewInstance vi = vbog.objectGraphToView(kefed);
			Map<String, Object> objMap = vbog.getObjMap();

			coreDao.getCe().executeInsertQuery(vi);

			Iterator<String> keyIt = objMap.keySet().iterator();
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
	
	public KefedModel retrieveModel(Long uid) throws Exception {

		ViewInstance vi = coreDao.getCe().executeUIDQuery("KefedModel", uid);

		ViewBasedObjectGraph vbog = new ViewBasedObjectGraph(coreDao.getTop(), coreDao.getCl(), "KefedModel");
		
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

		KefedModel kefed = (KefedModel) vbog.readPrimaryObject();

		return kefed;

	}

	public void saveModel(KefedModel kefed) throws Exception {
		
		try {

			coreDao.getCe().connectToDB();
			coreDao.getCe().turnOffAutoCommit();
			
			ViewInstance vi = coreDao.getCe().executeUIDQuery("KefedModel", kefed.getVpdmfId() );
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
				coreDao.getCe().deleteView("KefedModelEdge", i);
				logger.debug("removing edge: " + i);
			}

			//
			// 2. remove unused elements from database
			//
			it = elToRemove.iterator();
			while( it.hasNext() ) {
				long i = it.next();
				KefedModelElement el = elExist.get(i);
				coreDao.getCe().deleteView(el.getElementType(), i);
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
