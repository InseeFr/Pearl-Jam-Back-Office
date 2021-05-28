package fr.insee.pearljam.api.service;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public interface DataSetInjectorService {


	HttpStatus createDataSet();

	HttpStatus deleteDataSet();

}
