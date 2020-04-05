package softuni.exam.service;


import softuni.exam.models.entities.Picture;

import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface PictureService {

    boolean areImported();

    String readPicturesFromFile() throws IOException;
	
	String importPictures() throws IOException;

	Set<Picture> getAllPicturesByCarId(Long carId);

}
