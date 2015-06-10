package edu.isi.bmkeg.kefed.bin;

import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.google.gson.Gson;

import edu.isi.bmkeg.kefed.dao.impl.ExtendedKefedDaoImpl;
import edu.isi.bmkeg.kefed.model.design.KefedModel;
import edu.isi.bmkeg.kefed.model.qo.design.KefedModel_qo;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;

public class KefedModelToJson {

	
	private static Logger logger = Logger.getLogger(KefedModelToJson.class);

	public static class Options {

		@Option(name = "-exptId", usage = "KEfED Model Id", required = true, metaVar = "KEFED")
		public String exptId;
		
		@Option(name = "-l", usage = "Database login", required = true, metaVar = "LOGIN")
		public String login = "";

		@Option(name = "-p", usage = "Database password", required = true, metaVar = "PASSWD")
		public String password = "";

		@Option(name = "-db", usage = "Database name", required = true, metaVar  = "DBNAME")
		public String dbName = "";

		@Option(name = "-wd", usage = "Working directory", required = true, metaVar  = "WDIR")
		public String workingDirectory = "";
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Options options = new Options();
		
		CmdLineParser parser = new CmdLineParser(options);

		try {
			
			parser.parseArgument(args);
	
			ExtendedKefedDaoImpl dao = new ExtendedKefedDaoImpl();
			dao.init(options.login, options.password, options.dbName, options.workingDirectory);
			dao.getCoreDao().connectToDb();
			
			KefedModel_qo mQo = new KefedModel_qo();
			mQo.setExptId(options.exptId);
			List<LightViewInstance> l = dao.getCoreDao().list(mQo, "KefedModel");
			
			for( LightViewInstance lvi : l ) {
				
				KefedModel m = dao.retrieveModel( lvi.getVpdmfId() );
				m.setDiagramXML(null);
				m.setIndexTuple(null);
				m.setViewType(null);
				m.setVpdmfLabel(null);
				Gson gson = new Gson();
				String json = gson.toJson( m );				
	
				System.out.print(json);
				int i = 0;
			}
			
												
		} catch (Exception e) {
			
			e.printStackTrace();
		
		}
		
	}
	
}
