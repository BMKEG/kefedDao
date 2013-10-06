package edu.isi.bmkeg.kefed.bin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.isi.bmkeg.ooevv.bin.OoEVVSpreadsheetToOWL;
import edu.isi.bmkeg.utils.springContext.AppContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/edu/isi/bmkeg/kefed/applicationContext-kefedTest-noJPA.xml"})
public class OoEVVSpreadsheetToOWLTest {
	
	ApplicationContext ctx;
	
	File drugInfusion, fbirn, gdi, radOnc, stroke, tractTracing, vaccine;
	File out;
	String output;

	@Before
	public void setUp() throws Exception {
		
		ctx = AppContext.getApplicationContext();
		
		tractTracing = ctx.getResource(
				"classpath:edu/isi/bmkeg/ooevv/svnStore/tractTracing_ooevv.xls"
				).getFile();
		vaccine = ctx.getResource(
				"classpath:edu/isi/bmkeg/ooevv/svnStore/vaccine_ooevv.xls"
				).getFile();
		
		output = "out.owl";
				
	}

	@After
	public void tearDown() throws Exception {
		out.delete();
	}
	
	@Test
	public final void testRunExecWithFullPaths_tractTracing() throws Exception {
		
		String[] args = new String[] { 
				tractTracing.getAbsolutePath(), 
				output};
		
		OoEVVSpreadsheetToOWL.main(args);
		
		out = new File(tractTracing.getParent() + "/" + output); 
		long fileSize = out.length();
		
		assertTrue("Owl file size: " + fileSize, 
				fileSize > 10  );
		
	}
	
	@Test
	public final void testRunExecWithFullPaths_vaccine() throws Exception {
		
		String[] args = new String[] { 
				vaccine.getAbsolutePath(), 
				output};
		
		OoEVVSpreadsheetToOWL.main(args);
		
		out = new File(vaccine.getParent() + "/" + output); 
		long fileSize = out.length();
		
		assertTrue("Owl file size: " + fileSize, 
				fileSize > 10  );
		
	}
	
	@Test
	public final void testRunExecWithFullPaths_all() throws Exception {
		
		String[] args = new String[] { 
				vaccine.getAbsolutePath(), 
				output};
		
		OoEVVSpreadsheetToOWL.main(args);

		args = new String[] { 
				tractTracing.getAbsolutePath(), 
				output};
		
		OoEVVSpreadsheetToOWL.main(args);
		
		out = new File(vaccine.getParent() + "/" + output); 
		long fileSize = out.length();
		
		assertTrue("Owl file size: " + fileSize, 
				fileSize > 10  );
		
	}
		
}

