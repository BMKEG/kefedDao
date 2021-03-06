package edu.isi.bmkeg.kefed.services.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.isi.bmkeg.bioscholar.model.KefedModelCuration;
import edu.isi.bmkeg.ftd.model.FTDFragment;
import edu.isi.bmkeg.kefed.dao.ExtendedKefedDao;
import edu.isi.bmkeg.kefed.model.design.KefedModel;
import edu.isi.bmkeg.kefed.model.design.KefedModelEdge;
import edu.isi.bmkeg.kefed.model.design.KefedModelElement;
import edu.isi.bmkeg.kefed.model.qo.design.KefedModel_qo;
import edu.isi.bmkeg.kefed.services.ExtendedKefedService;
import edu.isi.bmkeg.vpdmf.dao.CoreDao;
import edu.isi.bmkeg.vpdmf.model.instances.AttributeInstance;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;
import edu.isi.bmkeg.vpdmf.model.instances.ViewInstance;

@RemotingDestination
@Transactional
@Service
public class ExtendedKefedServiceImpl implements ExtendedKefedService {

	private static final Logger logger = Logger.getLogger(ExtendedKefedServiceImpl.class);

	@Autowired
	private ExtendedKefedDao extKefedDao;

	public void setExtKefedDao(ExtendedKefedDao kefedDao) {
		this.extKefedDao = kefedDao;
	}
	
	public boolean saveCompleteKefedModel(KefedModel kefedModel) throws Exception {
	
		this.extKefedDao.initializeOoevvDao();
		
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		
		KefedModel_qo kefedQo = new KefedModel_qo();
		kefedQo.setExptId( kefedModel.getExptId() );
		List<LightViewInstance> lviList = coreDao.list(kefedQo, "KefedModel");
		if( lviList.size() == 0 ) {
			this.extKefedDao.insertKefedModel(kefedModel);
		} else if( lviList.size() == 1) {
			this.extKefedDao.saveModel(kefedModel, lviList.get(0).getVpdmfId() );
		} else {
			return false;
		}
		
		return true;
				
	}

	@Override
	public KefedModel retrieveCompleteKefedModel(long vpdmfId) throws Exception {

		this.extKefedDao.initializeOoevvDao();
		
		return this.extKefedDao.retrieveModel(vpdmfId);
		
	}
	
	@Override
	public KefedModel retrieveCompleteKefedModelFromUuid(String uuid) throws Exception {

		this.extKefedDao.initializeOoevvDao();
		
		return this.extKefedDao.retrieveModelFromUuid(uuid);
		
	}
	
	@Override
	public Document retrieveKefedModelTree(long vpdmfId) 
			throws Exception {
		
		this.extKefedDao.initializeOoevvDao();
		
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		coreDao.connectToDb();
		
		String sql = "select j.vpdmfId, j.abbr, " + 
				" lc.vpdmfId, lc.pubYear, ac.volume, lc.pages, vt.vpdmfLabel, " +
				" frg.vpdmfId, frg.frgOrder, b.vpdmfId, b.vpdmfOrder, b.text, k.exptId, k.vpdmfId " +
				"from " + 
				"FTDFragment as frg, " +
				"FTDFragmentBlock as b, " +
				"FTD as ftd, " +
				"Journal as j, " + 
				"ViewTable as vt, " +
				"LiteratureCitation as lc, " +
				"ArticleCitation as ac, " +
				"KefedModelCuration as kc, " +
				"KefedModel as k " +
				"where " +
//				"b.vpdmfOrder = 0 AND " +
				"b.fragment_Id = frg.vpdmfId AND " +
				"frg.ftd_id = ftd.vpdmfId AND " +
				"lc.fullText_id = ftd.vpdmfId AND " +
				"lc.vpdmfId = " + vpdmfId + " AND " +
				"lc.vpdmfId = vt.vpdmfId AND " +
				"lc.vpdmfId = ac.vpdmfId AND " +
				"ac.journal_id = j.vpdmfId AND " +
				"kc.doc_id = frg.vpdmfId AND " +
				"k.curation_id = kc.vpdmfId " +
				"ORDER BY j.abbr, lc.pubYear, ac.volume, lc.pages, frg.frgOrder, k.exptId";
		
		ResultSet rs = coreDao.getCe().executeRawSqlQuery(sql);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.newDocument();
		
		// create the root element node
		Element root = doc.createElement("root");
		doc.appendChild(root);
				
		Map<String, Element> jLookup = new HashMap<String, Element>(); 
		Map<String, Element> yLookup = new HashMap<String, Element>(); 
		Map<String, Element> vLookup = new HashMap<String, Element>(); 
		Map<String, Element> pLookup = new HashMap<String, Element>(); 
		Map<String, Element> fLookup = new HashMap<String, Element>(); 
		Map<String, Element> kLookup = new HashMap<String, Element>(); 
		
		while( rs.next() ) {

			String j = rs.getString("j.abbr");
			Long jId = rs.getLong("j.vpdmfId");
			String y = rs.getString("lc.pubYear");
			String v = rs.getString("ac.volume");
			String pg = rs.getString("lc.pages");
			
			String cit = rs.getString("vt.vpdmfLabel");
			int commaLen = cit.indexOf(",");
			int bracketLen = cit.indexOf("(");
			if( commaLen > bracketLen )
				cit = cit.substring(0, bracketLen); 
			else 
				cit = cit.substring(0, commaLen) + " et al."; 
			cit += " p: " + pg; 
			
			Long citId = rs.getLong("lc.vpdmfId");
			String f = rs.getString("frg.frgOrder");
			Long frgId = rs.getLong("frg.vpdmfId");
			String b = rs.getString("b.text");
			Long bId = rs.getLong("b.vpdmfId");
			String t = rs.getString("b.text");
			String k = rs.getString("k.exptId");
			Long kId = rs.getLong("k.vpdmfId");
			
			/*Element jEl = doc.createElement("node");
			jEl.setAttribute("label", j);
			jEl.setAttribute("data", jId + "");
			jEl.setAttribute("type", "journal");
			if( jLookup.containsKey(j)) {
				jEl = jLookup.get(j);
			} else {
				jLookup.put(j,jEl);
				root.appendChild(jEl);
			}
			
			Element yEl = doc.createElement("node");
			yEl.setAttribute("label", y);
			yEl.setAttribute("type", "year");
			if( yLookup.containsKey(j + "_" + y)) {
				yEl = yLookup.get(j + "_" + y);
			} else {
				yLookup.put(j + "_" + y,yEl);
				jEl.appendChild(yEl);
			}

			Element vEl = doc.createElement("node");
			vEl.setAttribute("label", v);
			vEl.setAttribute("type", "volume");
			if( vLookup.containsKey(j + "_" + y + "_" + v)) {
				vEl = vLookup.get(j + "_" + y + "_" + v);
			} else {
				vLookup.put(j + "_" + y + "_" + v, vEl);
				yEl.appendChild(vEl);
			}*/

			Element citEl = doc.createElement("node");
			citEl.setAttribute("label", cit);
			citEl.setAttribute("data", citId + "");
			citEl.setAttribute("type", "citation");
			if( pLookup.containsKey(citId + "")) {
				citEl = pLookup.get(citId + "");
			} else {
				pLookup.put(citId + "", citEl);
				root.appendChild(citEl);
			}

			Element frgEl = doc.createElement("node");
			frgEl.setAttribute("label", f + " [ " + b);
			frgEl.setAttribute("data", frgId + "");
			frgEl.setAttribute("type", "fragment");
			if( fLookup.containsKey(citId + "__" + frgId)) {
				frgEl = fLookup.get(citId + "__" + frgId);
			} else {
				fLookup.put(citId + "__" + frgId, frgEl);
				citEl.appendChild(frgEl);
			}

			Element kEl = doc.createElement("node");
			kEl.setAttribute("label", k);
			kEl.setAttribute("data", kId + "");
			kEl.setAttribute("type", "kefedModel");
			if( kLookup.containsKey(citId + "__" + frgId + "__" + kId)) {
				kEl = kLookup.get(citId + "__" + frgId + "__" + kId);
			} else {
				kLookup.put(citId + "__" + frgId + "__" + kId, kEl);
				frgEl.appendChild(kEl);
			}
			
		}
		rs.close();
		coreDao.closeDbConnection();
		
		return doc;
		
	}

	@Override
	public KefedModel createNewKefedModelForFragment(long frgId)
			throws Exception {

		this.extKefedDao.initializeOoevvDao();
		
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		coreDao.connectToDb();
		
		KefedModelCuration kc = new KefedModelCuration();
		KefedModel km = new KefedModel();
		km.setExptId("New KEfED Model");
		km.setNotes("Enter notes here.");
		km.setDate((new Date()).toString());
		km.setUuid( UUID.randomUUID().toString() );
		
		String xmlString = "<diagram version=\"0\" width=\"2879\" height=\"2879\" id=\"" + km.getUuid() + "\">\n" + 
				"  <panels id=\"" +  UUID.randomUUID().toString() + "\">\n" +
				"    <panel id=\"" +  UUID.randomUUID().toString() + "\" height=\"2879\" title=\"Default title\">\n" +
				"      <lane id=\""+  UUID.randomUUID().toString() + "\" height=\"2879\" title=\"Default title\">\n" +
				"        <links id=\"" + UUID.randomUUID().toString() + "\">\n" +
				"	     </links>\n" +
				"   	 <sprites id=\"" + UUID.randomUUID().toString() + "\">\n" +
				"        </sprites>\n" +
				"        <annotations id=\"" + UUID.randomUUID().toString() + "\">\n" +
				"	     </annotations>\n" +
				"      </lane>\n      " +
				"      <links id=\"" + UUID.randomUUID().toString() + "\"/>\n      " +
				"      <annotations id=\"" + UUID.randomUUID().toString() + "\"/>\n    " +
				"    </panel>\n    " +
				"    <links id=\"" + UUID.randomUUID().toString() + "\"/>\n    " +
				"    <annotations id=\"" + UUID.randomUUID().toString() + "\"/>\n" +
				"  </panels>\n" +
				"</diagram>";
				
		km.setDiagramXML( xmlString );
		kc.setKefedModel(km);
		
		FTDFragment f = coreDao.findByIdInTrans(frgId, new FTDFragment(), "FTDFragment");
		kc.setDoc(f);
		
		coreDao.insertInTrans(km, "KefedModel");
		coreDao.insertInTrans(kc, "KefedModelCuration");
		
		coreDao.commitTransaction();
		
		coreDao.closeDbConnection();
		
		return km;
		
	}
	
	@Override
	public Boolean insertKefedElement(KefedModelElement ke, String xml) throws Exception {

		this.extKefedDao.initializeOoevvDao();
		
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		coreDao.connectToDb();
		
		coreDao.insertInTrans(ke, ke.getClass().getSimpleName());
		
		if(xml.length() > 0 ) {
			xml = xml.replaceAll("\"", "\\\\\"");
			String sql = "UPDATE KefedModel SET diagramXML = \"" + xml + "\" " +
				" WHERE uuid = \"" + ke.getModel().getUuid() + "\";";
			coreDao.getCe().executeRawUpdateQuery(sql);
		}
		
		coreDao.commitTransaction();
		
		coreDao.closeDbConnection();
		
		return true;
		
	}
	
	@Override
	public Boolean insertKefedEdge(KefedModelEdge ke, String xml)
			throws Exception {
	
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		coreDao.connectToDb();
		
		coreDao.insertInTrans(ke, "KefedModelEdge");
		
		if(xml.length() > 0 ) {
			xml = xml.replaceAll("\"", "\\\\\"");
			String sql = "UPDATE KefedModel SET diagramXML = \"" + xml + "\" " +
					" WHERE uuid = \"" + ke.getModel().getUuid() + "\";";
			coreDao.getCe().executeRawUpdateQuery(sql);
		}
		
		coreDao.commitTransaction();
		
		coreDao.closeDbConnection();
		
		return true;
	
	}
	

	@Override
	public List<String> deleteKefedElements(List<String> uids) throws Exception {

		this.extKefedDao.initializeOoevvDao();
		
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		coreDao.connectToDb();

		List<String> successes = new ArrayList<String>();
		
		for(String uid : uids) {

			String sql = "select km.uuid, ke.vpdmfId, ke.elementType " + 
					"from " + 
					"KefedModel as km, " +
					"KefedModelElement as ke " +
					"where " +
					"km.vpdmfId = ke.model_id AND " +
					"ke.uuid = \"" + uid + "\"";
			
			ResultSet rs = coreDao.getCe().executeRawSqlQuery(sql);
			rs.next();
			
			String kmUid =  rs.getString("km.uuid");
			Long keId = rs.getLong("ke.vpdmfId");
			String keType =  rs.getString("ke.elementType");
			rs.close();
			
			boolean success = coreDao.deleteByIdInTrans(keId, keType);
			
			if( success ) {
				successes.add(uid);
			}
		
		}
		
		coreDao.commitTransaction();
		
		coreDao.closeDbConnection();		
		
		return successes;
		
	}

	@Override
	public List<String> deleteKefedEdges(List<String> uids) throws Exception {
		
		this.extKefedDao.initializeOoevvDao();
		
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		coreDao.connectToDb();
		
		List<String> successes = new ArrayList<String>();
		
		for(String uid : uids) {
		
			String sql = "select km.uuid, ke.vpdmfId " + 
					"from " + 
					"KefedModel as km, " +
					"KefedModelEdge as ke " +
					"where " +
					"km.vpdmfId = ke.model_id AND " +
					"ke.uuid = \"" + uid + "\"";
			
			ResultSet rs = coreDao.getCe().executeRawSqlQuery(sql);
			rs.next();
			
			Long keId = rs.getLong("ke.vpdmfId");
			rs.close();
			
			boolean success = coreDao.deleteByIdInTrans(keId, "KefedModelEdge");
						
			if( success ) {
				successes.add(uid);
			}

		}		
		
		coreDao.commitTransaction();
		
		coreDao.closeDbConnection();		
		
		return successes;
		
	}
	
	@Override
	public Boolean deleteCompleteKefedModelFromUid(String uid) throws Exception {
		
		this.extKefedDao.initializeOoevvDao();
		
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		coreDao.connectToDb();
		
		String sql = "select ke.vpdmfId " + 
				"from " + 
				"KefedModel as km, " +
				"KefedModelEdge as ke " +
				"where " +
				"km.vpdmfId = ke.model_id AND " +
				"km.uuid = '" + uid + "'";
		ResultSet rs = coreDao.getCe().executeRawSqlQuery(sql);
		List<Long> edgeIds = new ArrayList<Long>();
		while( rs.next() ) {
			edgeIds.add(rs.getLong("ke.vpdmfId"));			
		}
		rs.close();

		sql = "select ke.vpdmfId, ke.elementType " + 
				"from " + 
				"KefedModel as km, " +
				"KefedModelElement as ke " +
				"where " +
				"km.vpdmfId = ke.model_id AND " +
				"km.uuid = '" + uid + "'";
		rs = coreDao.getCe().executeRawSqlQuery(sql);
		
		Map<Long,String> elementIds = new HashMap<Long,String>();
		while( rs.next() ) {
			elementIds.put( rs.getLong("ke.vpdmfId"),
					rs.getString("ke.elementType") );			
		}
		rs.close();

		sql = "select km.vpdmfId " + 
				"from " + 
				"KefedModel as km " +
				"where " +
				"km.uuid = '" + uid + "'";
		
		rs = coreDao.getCe().executeRawSqlQuery(sql);
		if( !rs.next() ) {
			rs.close();
			return false;
		}
		Long id = rs.getLong("km.vpdmfId");
		rs.close();

		for(Long edgeId: edgeIds) {
			coreDao.deleteByIdInTrans(edgeId, "KefedModelEdge");
		}

		for(Long elementId: elementIds.keySet()) {
			coreDao.deleteByIdInTrans(elementId, elementIds.get(elementId));
		}
		
		coreDao.deleteByIdInTrans(id, "KefedModel");
		
		coreDao.commitTransaction();
		
		coreDao.closeDbConnection();		
		
		return true;
	
	}
	
	@Override
	public Boolean deleteCompleteKefedModel(Long id) throws Exception {
		
		this.extKefedDao.initializeOoevvDao();
		
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		coreDao.connectToDb();
		
		String sql = "select ke.vpdmfId " + 
				"from " + 
				"KefedModel as km, " +
				"KefedModelEdge as ke " +
				"where " +
				"km.vpdmfId = ke.model_id AND " +
				"km.vpdmfId = " + id;
		ResultSet rs = coreDao.getCe().executeRawSqlQuery(sql);
		List<Long> edgeIds = new ArrayList<Long>();
		while( rs.next() ) {
			edgeIds.add(rs.getLong("ke.vpdmfId"));			
		}
		rs.close();

		sql = "select ke.vpdmfId, ke.elementType " + 
				"from " + 
				"KefedModel as km, " +
				"KefedModelElement as ke " +
				"where " +
				"km.vpdmfId = ke.model_id AND " +
				"km.vpdmfId = " + id;
		rs = coreDao.getCe().executeRawSqlQuery(sql);
		Map<Long,String> elementIds = new HashMap<Long,String>();
		while( rs.next() ) {
			elementIds.put( rs.getLong("ke.vpdmfId"),
					rs.getString("ke.elementType") );			
		}
		rs.close();

		for(Long edgeId: edgeIds) {
			coreDao.deleteByIdInTrans(edgeId, "KefedModelEdge");
		}

		for(Long elementId: elementIds.keySet()) {
			coreDao.deleteByIdInTrans(elementId, elementIds.get(elementId));
		}

		coreDao.deleteByIdInTrans(id, "KefedModel");
		
		coreDao.commitTransaction();
		
		coreDao.closeDbConnection();		
		
		return true;
	
	}
		
	@Override
	public Boolean moveKefedEdgesAndElements(List<String> uids, int dx, int dy) throws Exception {
		
		this.extKefedDao.initializeOoevvDao();
		
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		coreDao.connectToDb();
		
		if( uids.size() == 0 ) 
			return false;
		
		for(String uuid : uids) {
			String sql = "select e.vpdmfId,e.elementType from KefedModelElement as e " +
					"where e.uuid = '" + uuid + "'";
			ResultSet rs = coreDao.getCe().executeRawSqlQuery(sql);
			rs.next();
			Long vpdmfId = rs.getLong("e.vpdmfId");
			String type = rs.getString("e.elementType");
			rs.close();

			ViewInstance vi = coreDao.getCe().executeUIDQuery(type, vpdmfId);
			coreDao.getCe().storeViewInstanceForUpdate(vi);
			
			AttributeInstance ai = vi.readAttributeInstance("]KefedModelElement|KefedModelElement.x", 0);
			ai.writeValueString(dx + "");

			ai = vi.readAttributeInstance("]KefedModelElement|KefedModelElement.y", 0);
			ai.writeValueString(dy + "");
			
			coreDao.getCe().executeUpdateQuery(vi);

		}
		
		coreDao.getCe().commitTransaction();
		
		coreDao.closeDbConnection();		
		
		return true;
	
	}
		
}