package edu.isi.bmkeg.kefed.dao;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.isi.bmkeg.ooevv.dao.ExtendedOoevvDaoImpl;
import edu.isi.bmkeg.ooevv.model.OoevvElementSet;
import edu.isi.bmkeg.terminology.model.Ontology;
import edu.isi.bmkeg.terminology.model.Term;
import edu.isi.bmkeg.utils.springContext.AppContext;
import edu.isi.bmkeg.utils.springContext.BmkegProperties;
import edu.isi.bmkeg.vpdmf.controller.VPDMfKnowledgeBaseBuilder;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={ "/edu/isi/bmkeg/kefed/applicationContext-kefedTest-noJPA.xml"})
public class KefedDaoImplTest {
	
	ApplicationContext ctx;
	
	String login, password, dbName, dbUrl;
	File model, baseDir, specsDir, archive1, archive2;
	VPDMfKnowledgeBaseBuilder builder;

	ExtendedKefedDaoImpl kefedDao;
	ExtendedOoevvDaoImpl ooevvDao;

	OoevvElementSet oes;
	
	@Before
	public void setUp() throws Exception {
		
		ctx = AppContext.getApplicationContext();
		BmkegProperties prop = (BmkegProperties) ctx.getBean("bmkegProperties");

		login = prop.getDbUser();
		password =  prop.getDbPassword();
		dbUrl = prop.getDbUrl();
		dbName = dbUrl.substring(dbUrl.lastIndexOf("/")+1);
		
		archive1 = ctx.getResource(
				"classpath:edu/isi/bmkeg/kefed/kefed_vpdmf_populated.zip").getFile();
		
		builder = new VPDMfKnowledgeBaseBuilder(archive1, 
				login, password, dbName); 
		
		try {
			builder.destroyDatabase(dbName);
		} catch (SQLException sqlE) {		
			
			// Gully: Make sure that this runs, avoid silly issues.
			if( !sqlE.getMessage().contains("database doesn't exist") ) {
				sqlE.printStackTrace();
			}
			
		}
		
		builder.buildDatabaseFromArchive();

		kefedDao = new ExtendedKefedDaoImpl();
		//kefedDao.init(login, password, dbUrl);
		//kefedDao.generateVbogs();
		
		//ooevvDao = new KefedDaoImpl();
		//ooevvDao.init(login, password, dbUrl);
		//.ooevvDao.generateVbogs();
		
		OoevvElementSet q = new OoevvElementSet();
		q.setShortTermId("tract-tracing-experimental-variables");
		q.setTermValue("Tract-tracing experimental variables");
		Ontology o = new Ontology();
		o.setShortName("OoEVV");
		q.setOntology(o);

//		ooevvDao.getCe().connectToDB();
//		List<LightViewInstance> lviList = ooevvDao.goGetLightViewList("OoevvElementSet", 
//				"]Term|Term.termValue", "Tract-tracing experimental variables");
//		ooevvDao.getCe().closeDbConnection();

		//String key = lviList.get(0).getUIDString();
		
		//oes = ooevvDao.loadWholeOoevvElementSet(key);
		
	}
	
	@After
	public void tearDown() throws Exception {
		
		builder.destroyDatabase(dbName);
		
	}

	/*@Test
	public void testInsertKefedModel() throws Exception {
		
		oes.setVpdmfLabel("VariableSet: OoEVV:vaccine-protection-study-variables (Vaccine protection study variables)");
		
		Term t = new Term();
		t.setShortTermId("vaccine-protection-study-variables");
		oes.setTerm(t);
		
		ArticleCitationPlaceholder cite = new ArticleCitationPlaceholder();
		cite.setCitation("test");
		
		List<KefedModel> kmList = new ArrayList<KefedModel>();
		
		for(int i=0; i<1; i++) {
		
			KefedModel k = KefedModelFactory.generateRandomKefedModel("Model " + i, oes, cite);
			kefedDao.insertKefedModel(k);
			kmList.add(k);
			
			System.out.println(i);
			
		}
		
		List<LightViewInstance> l = kefedDao.listAllKefedModels();
		
		// Let's edit and change kmList.get(0);
		KefedModel m = kmList.get(0);
		for(int i=m.getElements().size(); i > 3; i--) {
			KefedModelElement e = m.getElements().get(i-1);
			KefedModelFactory.deleteNodeFromGraph(m, e);
		}
		
		kefedDao.saveModel(m);
				
	}*/

	
	@Test
	public void passTests() {
		assert(true);
	}

	/*@Test
	public void testRetrieveModel() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveModel() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteModel() {
		fail("Not yet implemented");
	}*/

}
