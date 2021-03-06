package edu.isi.bmkeg.kefed.bin;

import java.io.File;
import java.sql.SQLException;

import edu.isi.bmkeg.vpdmf.controller.VPDMfKnowledgeBaseBuilder;

public class BuildKefedDatabase {
	
	public static String USAGE = "arguments: <dbname> <login> <password>\nOld database will be overwritten."; 
	public static String KEFED_ARCHIVE = "kefed_vpdmf.zip"; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		if( args.length != 3 ) {
			System.err.println(USAGE);
			System.exit(-1);
		}
		
		String buildFilePath = ClassLoader.getSystemResource("edu/isi/bmkeg/cosi/cosi-mysql.zip").getFile();
		File buildFile = new File(buildFilePath);
		
		String dbName = args[0];
		int l = dbName.lastIndexOf("/");
		if( l != -1 )
			dbName = dbName.substring(l+1, dbName.length());
		
		String login = args[1];
		String password =  args[2];
		
		System.out.println("Database: " + dbName);
		System.out.println("Login: " + login);
		System.out.println("Password: **********************");
		
		try {
			
			VPDMfKnowledgeBaseBuilder builder = new VPDMfKnowledgeBaseBuilder(buildFile, login, password, dbName);
			
			// Gully: Make sure that this runs, avoid silly issues.
			try {
				builder.destroyDatabase(dbName);
			} catch (SQLException sqlE) {		
				if( !sqlE.getMessage().contains("database doesn't exist") ) {
					sqlE.printStackTrace();
				}
			} 
			
			builder.buildDatabaseFromArchive();

			System.out.println("Build Complete From " + buildFile.getPath());
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			System.err.println("Build Failed From " + buildFile.getPath());
			
		}
		
	}

}
