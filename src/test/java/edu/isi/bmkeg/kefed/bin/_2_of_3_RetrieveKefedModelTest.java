package edu.isi.bmkeg.kefed.bin;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.isi.bmkeg.kefed.dao.ExtendedKefedDao;
import edu.isi.bmkeg.kefed.dao.KefedDao;
import edu.isi.bmkeg.kefed.dao.impl.ExtendedKefedDaoImpl;
import edu.isi.bmkeg.kefed.dao.impl.KefedDaoImpl;
import edu.isi.bmkeg.kefed.model.design.KefedModel;
import edu.isi.bmkeg.kefed.model.qo.design.KefedModel_qo;
import edu.isi.bmkeg.ooevv.bin.OoevvSvnToDatabase;
import edu.isi.bmkeg.utils.springContext.AppContext;
import edu.isi.bmkeg.utils.springContext.BmkegProperties;
import edu.isi.bmkeg.vpdmf.controller.VPDMfKnowledgeBaseBuilder;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/edu/isi/bmkeg/kefed/applicationContext-kefedTest-noJPA.xml"})
public class _2_of_3_RetrieveKefedModelTest {

	ApplicationContext ctx;

	OoevvSvnToDatabase svnToDb;
	
	File excel;
	String output;

	String dbLogin, dbPassword, dbUrl;
	String svnLogin, svnPassword, svnUrl;
	File svnDir;
	
	VPDMfKnowledgeBaseBuilder builder;
	
	ExtendedKefedDao extKefedDao;
	KefedDao kefedDao;
	
	@Before
	public void setUp() throws Exception {
		
		ctx = AppContext.getApplicationContext();
		BmkegProperties prop = (BmkegProperties) ctx.getBean("bmkegProperties");

		dbLogin = prop.getDbUser();
		dbPassword = prop.getDbPassword();
		dbUrl = "kefed_test"; //prop.getDbUrl();
		
		int l = dbUrl.lastIndexOf("/");
		if (l != -1)
			dbUrl = dbUrl.substring(l + 1, dbUrl.length());
		
		svnDir = ctx.getResource(
				"classpath:edu/isi/bmkeg/ooevv/svnStore").getFile();
		
		File archiveFile = ctx.getResource(
				"classpath:edu/isi/bmkeg/cosi/cosi-mysql.zip").getFile();
		
		builder = new VPDMfKnowledgeBaseBuilder(archiveFile, 
				dbLogin, dbPassword, dbUrl);
		
		extKefedDao = new ExtendedKefedDaoImpl();
		extKefedDao.init(dbLogin, dbPassword, dbUrl);
		
		kefedDao = new KefedDaoImpl();
		kefedDao.setCoreDao(this.extKefedDao.getCoreDao());
		
	}
	
	@After
	public void tearDown() throws Exception {

//		builder.destroyDatabase(dbUrl);
		
	}
	
	@Test
	public final void testListKefedModels() throws Exception {

		KefedModel_qo qo = new KefedModel_qo();
		qo.setExptId("ad");
		List<LightViewInstance> l = kefedDao.listKefedModel(qo);

		KefedModel kefed = extKefedDao.retrieveModel(l.get(0).getVpdmfId());
		
		int pauseHere = 0;
		//assert(kefed.getElements().size() == )
				
	}
	
}
