package edu.isi.bmkeg.kefed.dao;

import java.util.List;

import edu.isi.bmkeg.kefed.model.design.KefedModel;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;

public interface ExtendedKefedDao {

	public List<LightViewInstance> listAllKefedModels() throws Exception;

	public void insertKefedModel(KefedModel kefed) throws Exception;

	public KefedModel retrieveModel(Long uid) throws Exception;

	public void saveModel(KefedModel kefed) throws Exception;

	public boolean deleteModel(Long uuid) throws Exception;
	
}
