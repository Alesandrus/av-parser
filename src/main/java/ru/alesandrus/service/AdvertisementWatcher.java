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

import java.io.File;
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
    private MarketAdvertisementParser marketAdvertisementParser;
    private ExcelCreator excelCreator;
    private AdvertisementSender advertisementSender;
    private AdOwnerRepository adOwnerRepository;
    private AdChecker adChecker;

    @Autowired
    public void setCompanyAdvertisementParser(CompanyAdvertisementParser companyAdvertisementParser) {
        this.companyAdvertisementParser = companyAdvertisementParser;
    }

    @Autowired
    public void setMarketAdvertisementParser(MarketAdvertisementParser marketAdvertisementParser) {
        this.marketAdvertisementParser = marketAdvertisementParser;
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

    @Autowired
    public void setAdChecker(AdChecker adChecker) {
        this.adChecker = adChecker;
    }

    @Scheduled(cron = "0 0 8-22 * * *")
//    @Scheduled(fixedRate = 300000)
    public void watchAds() {
        LOGGER.info("Start scan ads");
        Iterable<AdOwner> adOwners = adOwnerRepository.findAll();
        Map<AdOwner, List<Advertisement>> adsForSending = new TreeMap<>();
        for (AdOwner adOwner : adOwners) {
            List<Advertisement> updatedAds = new ArrayList<>();
            if (adOwner.isActive()) {
                List<Advertisement> ads;
                if (adOwner.getOwnerType() == OwnerType.COMPANY) {
                    ads = companyAdvertisementParser.parsePageAndGetAds(adOwner);
                } else {
                    ads = marketAdvertisementParser.parsePageAndGetAds(adOwner);
                }
                updatedAds = adChecker.checkAds(ads);
            }
            if (!updatedAds.isEmpty()) {
                Collections.sort(updatedAds);
                adsForSending.put(adOwner, updatedAds);
            }
        }
        if (!adsForSending.isEmpty()) {
            String creationTime = DateUtils.getCurrentTime();
            String pathToTemp = String.format("%s%sreport_%s.xls", System.getProperty(JAVA_IO_TMPDIR), File.separatorChar, creationTime);
            excelCreator.createReport(adsForSending, pathToTemp);
            LOGGER.info("Report was created");
            advertisementSender.sendReport(pathToTemp, creationTime);
            LOGGER.info("Email was sent");
        }
        LOGGER.info("Scanning ads was finished");
    }
}
