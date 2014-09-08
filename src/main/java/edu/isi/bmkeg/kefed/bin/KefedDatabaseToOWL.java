package edu.isi.bmkeg.kefed.bin;

import java.io.File;

import edu.isi.bmkeg.kefed.dao.impl.ExtendedKefedDaoImpl;
import edu.isi.bmkeg.uml.interfaces.OwlUmlInterface;
import edu.isi.bmkeg.uml.model.UMLmodel;
import edu.isi.bmkeg.vpdmf.model.definitions.VPDMf;

public class KefedDatabaseToOWL {

	public static String USAGE = "arguments: <database-name> <login> <dbPassword> <workingDirectory> <owl-file>\n"; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if( args.length != 5 ) {
			System.err.println(USAGE);
			System.exit(-1);
		}
		
		try { 
			
			ExtendedKefedDaoImpl dao = new ExtendedKefedDaoImpl();
			dao.init(args[1], args[2], args[0], args[3]);
			VPDMf top = dao.getCoreDao().getTop();			
			UMLmodel m = top.getUmlModel();
			
			OwlUmlInterface oui = new OwlUmlInterface();
			oui.setUmlModel(m);

			String uri = "http://bmkeg.isi.edu/kefed/";

			File owlFile = new File(args[4]);	

			oui.saveUmlAsOwl(owlFile, uri, "\\.(ooevv|kefed)\\.model\\.");
			
			//engine.saveAllOoevvElementSetToOwl(owlFile, uri);
												
		} catch (Exception e) {
			
			e.printStackTrace();
		
		}
		
	}
	
}
