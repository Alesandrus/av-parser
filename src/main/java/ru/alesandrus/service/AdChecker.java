package ru.alesandrus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.alesandrus.models.Advertisement;
import ru.alesandrus.repositories.AdvertisementRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdChecker.class);

    private AdvertisementRepository advertisementRepository;

    @Autowired
    public void setAdvertisementRepository(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    public List<Advertisement> checkAds(List<Advertisement> ads) {
        List<Advertisement> updatedAds = new ArrayList<>();
        for (Advertisement ad : ads) {
            checkOrSaveAd(ad, updatedAds);
        }
        return updatedAds;
    }

    private void checkOrSaveAd(Advertisement curAd, List<Advertisement> updatedList) {
        Advertisement oldAd = advertisementRepository.findByUrl(curAd.getUrl()).orElse(null);
        if (oldAd != null && curAd.getLastUpdateTime().after(oldAd.getLastUpdateTime())) {
            oldAd.setLastUpdateTime(curAd.getLastUpdateTime());
            advertisementRepository.save(oldAd);
            updatedList.add(oldAd);
        } else if (oldAd == null) {
            curAd.setCreateTime(curAd.getLastUpdateTime());
            advertisementRepository.save(curAd);
            updatedList.add(curAd);
        }
    }
}
