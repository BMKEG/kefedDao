package edu.isi.bmkeg.kefed.services;

import java.util.List;

import org.w3c.dom.Document;

import edu.isi.bmkeg.kefed.model.design.*;

public interface ExtendedKefedService {
	
	public boolean saveCompleteKefedModel(KefedModel kefedModel) throws Exception;

	public KefedModel retrieveCompleteKefedModel(long vpdmfId) throws Exception;
	
	public Document retrieveKefedModelTree(long vpdmfId) throws Exception;

	public KefedModel createNewKefedModelForFragment(long frgId) throws Exception;

	public Boolean deleteKefedElement(String uid, String xml) throws Exception;

	public Boolean deleteKefedEdge(String uid, String xml) throws Exception;

	public Boolean insertKefedElement(KefedModelElement ke, String xml) throws Exception;

	public Boolean insertKefedEdge(KefedModelEdge ke, String xml) throws Exception;

	public Boolean deleteCompleteKefedModel(Long id) throws Exception;
	
	public Boolean moveKefedEdgesAndElements(List<String> uids, int dx, int dy) throws Exception;

}