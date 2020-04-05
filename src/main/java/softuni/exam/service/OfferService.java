package softuni.exam.service;

import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;


public interface OfferService {

    boolean areImported();

    String readOffersFileContent() throws IOException;
	
	String importOffers() throws IOException, JAXBException;


}
