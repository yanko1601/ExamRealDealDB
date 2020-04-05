package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.OffersRootSeedDto;
import softuni.exam.models.entities.Car;
import softuni.exam.models.entities.Offer;
import softuni.exam.models.entities.Picture;
import softuni.exam.models.entities.Seller;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.OfferService;
import softuni.exam.service.PictureService;
import softuni.exam.service.SellerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static softuni.exam.constants.GlobalConstants.*;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final CarService carService;
    private final SellerService sellerService;
    private final PictureService pictureService;

    public OfferServiceImpl(OfferRepository offerRepository, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser, CarService carService, SellerService sellerService, PictureService pictureService) {
        this.offerRepository = offerRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.carService = carService;
        this.sellerService = sellerService;
        this.pictureService = pictureService;
    }

    @Override
    public boolean areImported() {
        return this.offerRepository.count() > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(Path.of(OFFERS_FILE_PATH));
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        OffersRootSeedDto offersRootSeedDto = this.xmlParser.convertFromFile(OFFERS_FILE_PATH, OffersRootSeedDto.class);
        offersRootSeedDto.getOffers().forEach(offersSeedDto -> {
            if(this.validationUtil.isValid(offersSeedDto)){
                if(this.offerRepository.findOfferByDescriptionAndAddedOn(offersSeedDto.getDescription(), offersSeedDto.getAddedOn()) == null){
                    Offer offer = this.modelMapper.map(offersSeedDto, Offer.class);
                    Seller seller = this.sellerService.getSellerById(offersSeedDto.getSeller().getId());
                    Car car = this.carService.getCarById(offersSeedDto.getCar().getId());

                    offer.setSeller(seller);
                    offer.setCar(car);

                    Set<Picture> pictures = this.pictureService.getAllPicturesByCarId(car.getId());
                    offer.setPictures(pictures);

                    sb.append(String.format("Successfully import offer %s - %s", offer.getAddedOn(), offer.isHasGoldStatus()));
                    this.offerRepository.saveAndFlush(offer);

                }else {
                    sb.append("Already in DB");
                }
            }else {
                sb.append("Invalid offer");
            }
            sb.append(System.lineSeparator());
        });

        return sb.toString();
    }
}
