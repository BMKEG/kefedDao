package edu.isi.bmkeg.kefed.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.isi.bmkeg.kefed.dao.ExtendedKefedDao;
import edu.isi.bmkeg.kefed.model.design.KefedModel;
import edu.isi.bmkeg.kefed.model.qo.design.KefedModel_qo;
import edu.isi.bmkeg.kefed.services.ExtendedKefedService;
import edu.isi.bmkeg.vpdmf.dao.CoreDao;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;

@RemotingDestination
@Transactional
@Service
public class ExtendedKefedServiceImpl implements ExtendedKefedService {

	private static final Logger logger = Logger.getLogger(ExtendedKefedServiceImpl.class);

	@Autowired
	private ExtendedKefedDao extKefedDao;

	public void setExtKefedDao(ExtendedKefedDao kefedDao) {
		this.extKefedDao = kefedDao;
	}
	
	public boolean saveCompleteKefedModel(KefedModel kefedModel) throws Exception {
	
		CoreDao coreDao = this.extKefedDao.getCoreDao();
		
		KefedModel_qo kefedQo = new KefedModel_qo();
		kefedQo.setExptId( kefedModel.getExptId() );
		List<LightViewInstance> lviList = coreDao.list(kefedQo, "KefedModel");
		if( lviList.size() == 0 ) {
			this.extKefedDao.insertKefedModel(kefedModel);
		} else if( lviList.size() == 1) {
			this.extKefedDao.saveModel(kefedModel);
		} else {
			return false;
		}
		
		return true;
				
	}

	@Override
	public KefedModel retrieveCompleteKefedModel(long vpdmfId) throws Exception {

		return this.extKefedDao.retrieveModel(vpdmfId);
		
	}

}