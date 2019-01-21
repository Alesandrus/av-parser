package ru.alesandrus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.alesandrus.models.AdOwner;
import ru.alesandrus.models.Advertisement;
import ru.alesandrus.models.enumerations.OwnerType;
import ru.alesandrus.repositories.AdOwnerRepository;
import ru.alesandrus.utils.DateUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 13.01.2019
 */
@Component
public class AdvertisementWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementWatcher.class);
    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    private CompanyAdvertisementParser companyAdvertisementParser;
    private ExcelCreator excelCreator;
    private AdvertisementSender advertisementSender;
    private AdOwnerRepository adOwnerRepository;

    @Autowired
    public void setCompanyAdvertisementParser(CompanyAdvertisementParser companyAdvertisementParser) {
        this.companyAdvertisementParser = companyAdvertisementParser;
    }

    @Autowired
    public void setAdvertisementSender(AdvertisementSender advertisementSender) {
        this.advertisementSender = advertisementSender;
    }

    @Autowired
    public void setExcelCreator(ExcelCreator excelCreator) {
        this.excelCreator = excelCreator;
    }

    @Autowired
    public void setAdOwnerRepository(AdOwnerRepository adOwnerRepository) {
        this.adOwnerRepository = adOwnerRepository;
    }

//    @Scheduled(cron = "0 0/30 7-22 * * *")
    @Scheduled(fixedRate = 300000)
    public void watch() {
        Iterable<AdOwner> adOwners = adOwnerRepository.findAll();
        Map<AdOwner, List<Advertisement>> adsForSending = new TreeMap<>();
        for (AdOwner adOwner : adOwners) {
            List<Advertisement> updatedAds = new ArrayList<>();
            if (adOwner.isActive()) {
                if (adOwner.getOwnerType() == OwnerType.COMPANY) {
                    updatedAds = companyAdvertisementParser.parsePageAndGetUpdatedAds(adOwner);
                }
            }
            if (!updatedAds.isEmpty()) {
                Collections.sort(updatedAds);
                adsForSending.put(adOwner, updatedAds);
            }
        }
        if (!adsForSending.isEmpty()) {
            String creationTime = DateUtils.getCurrentTime();
            String pathToTemp = String.format("%sreport_%s.xls", System.getProperty(JAVA_IO_TMPDIR), creationTime);
            excelCreator.createReport(adsForSending, pathToTemp);
            //advertisementSender.sendReport(pathToTemp, creationTime);
        }
    }
}
