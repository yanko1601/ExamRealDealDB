package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.constants.GlobalConstants;
import softuni.exam.models.dtos.CarSeedDto;
import softuni.exam.models.entities.Car;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;
import softuni.exam.util.ValidationUtil;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;

import static softuni.exam.constants.GlobalConstants.*;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;

    public CarServiceImpl(CarRepository carRepository, ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }


    @Override
    public boolean areImported() {
        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsFileContent() throws IOException {
        return Files.readString(Path.of(CARS_FILE_PATH));
    }

    @Override
    public String importCars() throws IOException {
        StringBuilder sb = new StringBuilder();
        CarSeedDto[] dtos = this.gson.fromJson(new FileReader(CARS_FILE_PATH), CarSeedDto[].class);
        Arrays.stream(dtos).forEach(carSeedDto -> {
            if(this.validationUtil.isValid(carSeedDto)){
                if(this.carRepository.findCarByMakeAndModelAndKilometers(carSeedDto.getMake(), carSeedDto.getModel(), carSeedDto.getKilometers()) == null){
                    Car car = this.modelMapper.map(carSeedDto, Car.class);
                    String s = carSeedDto.getRegisteredOn();
                    LocalDate date = LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    car.setRegisteredOn(date);
                    sb.append(String.format("Successfully imported car - %s - %s", car.getMake(), car.getModel()));
                    this.carRepository.saveAndFlush(car);
                }else {
                    sb.append("Already in DB");
                }
            }else {
                sb.append("Invalid car");
            }
            sb.append(System.lineSeparator());
        });


        return sb.toString();
    }

    @Override
    public String getCarsOrderByPicturesCountThenByMake() {
        StringBuilder sb = new StringBuilder();
        this.carRepository.getCarsOrderByPicturesCountThenByMake().forEach(car -> {
            sb.append(String.format("Car make - %s, model - %s\n" +
                    "\tKilometers - %d\n" +
                    "\tRegistered on - %s\n" +
                    "\tNumber of pictures - %d\n", car.getMake(), car.getModel(), car.getKilometers(), car.getRegisteredOn(), car.getPictures().size()));
        });


        return sb.toString();
    }

    @Override
    public Car getCarById(Long id) {
        return this.carRepository.findCarById(id);
    }
}
