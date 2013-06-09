package edu.isi.bmkeg.kefed.utils.json;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.isi.bmkeg.kefed.utils.json.JsonKefedModel;
import edu.isi.bmkeg.kefed.utils.json.KefedObject;
import edu.isi.bmkeg.kefed.utils.json.KefedJsonToVpdmfConverter;
import edu.isi.bmkeg.ooevv.model.ExperimentalVariable;
import edu.isi.bmkeg.ooevv.model.OoevvEntity;
import edu.isi.bmkeg.ooevv.model.OoevvProcess;
import edu.isi.bmkeg.terminology.model.Ontology;
import edu.isi.bmkeg.terminology.model.Term;
import edu.isi.bmkeg.utils.springContext.AppContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/edu/isi/bmkeg/kefed/applicationContext-kefedTest-noJPA.xml"})
public class OoevvJsonToVpdmfConverterTest {

	
	ApplicationContext ctx;
	
	private KefedJsonToVpdmfConverter jsonToVpdmf;	
	
	String kefedUrl;

	File smallJsonFile, largeJsonFile, report1, report2;
	
	@Before
	public void setUp() throws Exception {
        
		ctx = AppContext.getApplicationContext();
		
		smallJsonFile = ctx.getResource("classpath:edu/isi/bmkeg/kefed/utils/json/kefedSimple.json").getFile();
		largeJsonFile = ctx.getResource("classpath:edu/isi/bmkeg/kefed/utils/json/kefed.json").getFile();
		report1 = new File(smallJsonFile.getParent() + "/report1.xls");
		report2 = new File(smallJsonFile.getParent() + "/report2.xls");

		kefedUrl = "http://hugin.isi.edu:8180/persevere/KefedModel";
		jsonToVpdmf = new KefedJsonToVpdmfConverter();
			
	}

	@After
	public void tearDown() throws Exception {
		
	}

/*	@Test
	public void testConvertJsonFileToOoevv() throws Exception {
		jsonToVpdmf.generateOoevvReport(largeJsonFile, report1);
	}*/

	@Test
	public void testConvertJsonUrlToOoevv() throws Exception {
		jsonToVpdmf.generateOoevvReport(kefedUrl, report2);
	}

/*	@Test
	public void testCommand() throws Exception {
		String[] args = new String[] { 
				kefedUrl, report2.getPath()
				};	
		OoevvJsonToVpdmfConverter.main(args);
	}*/
	
	
}
