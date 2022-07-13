package fr.insee.pearljam.api.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.service.DataSetInjectorService;


@Service
@Transactional
public class DataSetInjectorServiceImpl implements DataSetInjectorService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSetInjectorServiceImpl.class);

	@Autowired
	private EntityManagerFactory emf;

	@Autowired
	private CampaignRepository campaignRepository;

	public HttpStatus createDataSet() {
		EntityManager em = emf.createEntityManager();

		if(!campaignRepository.findAllIds().isEmpty()){
			LOGGER.info("The database already contains a campaign, the dataset was not imported");
			return HttpStatus.NOT_MODIFIED;
		}
		LOGGER.info("Dataset creation start");
		try (
			InputStream sqlFileInputStream = getClass().getClassLoader()
	                .getResource("dataset//insert_test_data.sql").openStream()
				){
	        BufferedReader sqlFileBufferedReader = new BufferedReader( new InputStreamReader(sqlFileInputStream));
	        executeStatements(sqlFileBufferedReader, em);
		} catch (Exception e) {
			e.printStackTrace();
			return HttpStatus.NOT_MODIFIED;
		}
		LOGGER.info("Dataset creation end");	
		return HttpStatus.OK;

	}

	public HttpStatus deleteDataSet() {
		try (InputStream sqlFileInputStream = getClass().getClassLoader()
	                .getResource("dataset//delete_data.sql").openStream()){		
			EntityManager em = emf.createEntityManager();
	        BufferedReader sqlFileBufferedReader = new BufferedReader( new InputStreamReader(sqlFileInputStream));
	        executeStatements(sqlFileBufferedReader, em);
		} catch (Exception e) {
			e.printStackTrace();
			return HttpStatus.NOT_MODIFIED;
		}
		return HttpStatus.OK;

	}


	void executeStatements(BufferedReader br, EntityManager entityManager) throws IOException {
	    String line;
	    while( (line = br.readLine()) != null ){
	    	entityManager.joinTransaction();
	        entityManager.createNativeQuery(line).executeUpdate();
	    }
	}
}
