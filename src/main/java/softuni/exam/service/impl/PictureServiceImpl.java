package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.PicturesSeedDto;
import softuni.exam.models.entities.Car;
import softuni.exam.models.entities.Picture;
import softuni.exam.repository.PictureRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.PictureService;
import softuni.exam.util.ValidationUtil;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static softuni.exam.constants.GlobalConstants.*;

@Service
public class PictureServiceImpl implements PictureService {
    private final PictureRepository pictureRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final CarService carService;

    public PictureServiceImpl(PictureRepository pictureRepository, ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, CarService carService) {
        this.pictureRepository = pictureRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.carService = carService;
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count() > 0;
    }

    @Override
    public String readPicturesFromFile() throws IOException {
        return Files.readString(Path.of(PICTURES_FILE_PATH));
    }

    @Override
    public String importPictures() throws IOException {
        StringBuilder sb = new StringBuilder();
        PicturesSeedDto[] dtos = this.gson.fromJson(new FileReader(PICTURES_FILE_PATH), PicturesSeedDto[].class);
        Arrays.stream(dtos).forEach(picturesSeedDto -> {
            if(this.validationUtil.isValid(picturesSeedDto)) {
                if(this.pictureRepository.findPictureByName(picturesSeedDto.getName()) == null) {
                    Picture picture = this.modelMapper.map(picturesSeedDto, Picture.class);
                    String s = picturesSeedDto.getDateAndTime();
                    LocalDateTime dateTime = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    Car car = this.carService.getCarById(picturesSeedDto.getCar());
                    picture.setDateAndTime(dateTime);
                    picture.setCar(car);
                    sb.append(String.format("Successfully imported picture - %s", picture.getName()));
                    this.pictureRepository.saveAndFlush(picture);
                }else {
                    sb.append("Already in DB");
                }
            }else{
                sb.append("Invalid picture");
            }
            sb.append(System.lineSeparator());
        });

        return sb.toString();
    }

    @Override
    public Set<Picture> getAllPicturesByCarId(Long carId) {
        return this.pictureRepository.findPicturesByCar_Id(carId);
    }
}
