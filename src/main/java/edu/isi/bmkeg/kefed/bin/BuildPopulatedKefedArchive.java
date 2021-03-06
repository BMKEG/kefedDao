package edu.isi.bmkeg.kefed.bin;

import edu.isi.bmkeg.ooevv.bin.OoevvDirToDatabase;
import edu.isi.bmkeg.vpdmf.bin.DumpDatabaseToVpdmfArchive;

public class BuildPopulatedKefedArchive  {

	public static String USAGE = "arguments: <xl-dir> <db-name> <db-login> <db-password> <new-archive-file> \n";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 5) {
			System.err.println(USAGE);
			System.exit(-1);
		}
		
		String localDirPath = args[0];
		String dbName = args[1];
		String dbLogin = args[2];
		String dbPassword = args[3];
		String archive = args[4];
		
		String[] args1 = new String[] { 
				localDirPath, dbName, dbLogin,dbPassword
				};
		
		OoevvDirToDatabase.main(args1);
		
		String archiveFile = ClassLoader.getSystemResource("edu/isi/bmkeg/kefed/kefed_VPDMf.zip").getFile();

		String[] args2 = new String[] { 
				archiveFile, dbName, dbLogin, dbPassword, archive
				};
			
		DumpDatabaseToVpdmfArchive.main(args2);
	
	}
	
}
