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

import edu.isi.bmkeg.ooevv.bin.OoevvDirToDatabase;
import edu.isi.bmkeg.ooevv.bin.OoevvSvnToDatabase;
import edu.isi.bmkeg.utils.springContext.AppContext;
import edu.isi.bmkeg.utils.springContext.BmkegProperties;
import edu.isi.bmkeg.vpdmf.controller.VPDMfKnowledgeBaseBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/edu/isi/bmkeg/kefed/applicationContext-kefedTest-noJPA.xml"})
public class _2_of_3_OoevvDirToDatabaseTest {

	ApplicationContext ctx;

	OoevvSvnToDatabase svnToDb;
	
	File excel;
	String output;

	String dbLogin, dbPassword, dbUrl;
	String svnLogin, svnPassword, svnUrl;
	File svnDir;
	
	VPDMfKnowledgeBaseBuilder builder;
	
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
				"classpath:edu/isi/bmkeg/ooevv/ppmi").getFile();
		
		File archiveFile = ctx.getResource(
				"classpath:edu/isi/bmkeg/cosi/cosi-mysql.zip").getFile();
		
		builder = new VPDMfKnowledgeBaseBuilder(archiveFile, 
				dbLogin, dbPassword, dbUrl);
		
	}
	
	@After
	public void tearDown() throws Exception {

//		builder.destroyDatabase(dbUrl);
		
	}
	
	@Test
	public final void testRunExecWithFullPaths() throws Exception {
		
		String[] args = new String[] { 
				svnDir.getPath(), dbUrl, dbLogin, dbPassword
				};
		
		OoevvDirToDatabase.main(args);
				
	}
	
}
