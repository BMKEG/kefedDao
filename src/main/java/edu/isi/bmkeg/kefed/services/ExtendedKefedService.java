package edu.isi.bmkeg.kefed.services;

import edu.isi.bmkeg.kefed.model.design.KefedModel;

public interface ExtendedKefedService {
	
	public boolean saveCompleteKefedModel(KefedModel kefedModel) throws Exception;

	public KefedModel retrieveCompleteKefedModel(long vpdmfId) throws Exception;

}