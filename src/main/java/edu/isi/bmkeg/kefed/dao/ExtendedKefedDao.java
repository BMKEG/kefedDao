package edu.isi.bmkeg.kefed.dao;

import java.util.List;
import java.util.Map;

import edu.isi.bmkeg.kefed.model.design.KefedModel;
import edu.isi.bmkeg.vpdmf.dao.CoreDao;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;
import edu.isi.bmkeg.vpdmf.model.instances.ViewBasedObjectGraph;

public interface ExtendedKefedDao {
	
	public CoreDao getCoreDao();
	
	public void init(String login, String password, String uri, String wd) throws Exception;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public void initializeOoevvDao() throws Exception;

	public List<LightViewInstance> listAllKefedModels() throws Exception;

	public void insertKefedModel(KefedModel kefed) throws Exception;

	public KefedModel retrieveModel(Long uid) throws Exception;

	public KefedModel retrieveModelFromUuid(String uuid) throws Exception;

	public void saveModel(KefedModel kefed, long vpdmfId) throws Exception;

	public boolean deleteModel(Long uuid) throws Exception;

	
}
