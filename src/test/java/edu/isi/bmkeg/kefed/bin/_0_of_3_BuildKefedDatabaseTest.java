package edu.isi.bmkeg.kefed.bin;

import java.io.File;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.isi.bmkeg.utils.springContext.AppContext;
import edu.isi.bmkeg.utils.springContext.BmkegProperties;
import edu.isi.bmkeg.vpdmf.controller.VPDMfKnowledgeBaseBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={ "/edu/isi/bmkeg/kefed/applicationContext-kefedTest-noJPA.xml"})
public class _0_of_3_BuildKefedDatabaseTest {
	
	ApplicationContext ctx;
	
	String login, password, dbName, dbUrl;
	File model, baseDir, specsDir, archive1, archive2;
	VPDMfKnowledgeBaseBuilder builder;
	
	@Before
	public void setUp() throws Exception {
		
		ctx = AppContext.getApplicationContext();
		BmkegProperties prop = (BmkegProperties) ctx.getBean("bmkegProperties");

		login = prop.getDbUser();
		password =  prop.getDbPassword();
		dbUrl = prop.getDbUrl();
		dbName = dbUrl.substring(dbUrl.lastIndexOf("/")+1);
		
		archive1 = ctx.getResource(
				"classpath:edu/isi/bmkeg/cosi/cosi-mysql.zip").getFile();
		
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
		
	}

	@After
	public void tearDown() throws Exception {
		
//		builder.destroyDatabase(dbName);
		
	}

	@Test
	public final void buildKefedDatabaseTest() throws Exception {
		
		// rebuild the kefed archive.
		String[] args = new String[] { 
				dbName, login, password
				};
		
		BuildKefedDatabase.main(args);
	
	}
		
}

