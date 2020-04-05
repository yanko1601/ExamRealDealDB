package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.constants.GlobalConstants;
import softuni.exam.models.dtos.SellerRootSeedDto;
import softuni.exam.models.entities.Seller;
import softuni.exam.repository.SellerRepository;
import softuni.exam.service.SellerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static softuni.exam.constants.GlobalConstants.*;

@Service
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    public SellerServiceImpl(SellerRepository sellerRepository, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser) {
        this.sellerRepository = sellerRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return this.sellerRepository.count() > 0;
    }

    @Override
    public String readSellersFromFile() throws IOException {
        return Files.readString(Path.of(SELLERS_FILE_PATH));
    }

    @Override
    public String importSellers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        SellerRootSeedDto sellerRootSeedDto = this.xmlParser.convertFromFile(SELLERS_FILE_PATH, SellerRootSeedDto.class);
        sellerRootSeedDto.getSellers().forEach(sellerSeedDto -> {
            if(this.validationUtil.isValid(sellerSeedDto)){
                if (this.sellerRepository.findSellerByEmail(sellerSeedDto.getEmail()) == null){
                    Seller seller = this.modelMapper.map(sellerSeedDto, Seller.class);
                    sb.append(String.format("Successfully import seller %s - %s", seller.getLastName(), seller.getEmail()));
                    this.sellerRepository.saveAndFlush(seller);
                }else {
                    sb.append("Already in DB");
                }
            }else {
                sb.append("Invalid seller");
            }
            sb.append(System.lineSeparator());
        });

        return sb.toString();
    }

    @Override
    public Seller getSellerById(Long id) {
        return this.sellerRepository.findSellerById(id);
    }

    @Override
    public void setPictures() {
    }
}
