package ru.alesandrus.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.alesandrus.models.AdOwner;
import ru.alesandrus.models.Advertisement;
import ru.alesandrus.models.enumerations.Category;
import ru.alesandrus.repositories.AdvertisementRepository;
import ru.alesandrus.utils.DateUtils;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 15.01.2019
 */
@Component
public class AdvertisementParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementParser.class);
    private static final String avitoRoot = "https://www.avito.ru";
    private static final String PHANTOMJS_ENV = "PHANTOMJS";
    private static final String SCROLL_DOWN_PATH = "//div[@class=\"js-footer\"]";
    private static final String ACTIVE_ADS_PATH = "//div[@data-marker=\"profile-item-box\"]/div[@itemprop=\"makesOffer\"]/a";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String PHANTOMJS_EXE = "/phantomjs.exe";
    private static final String EMPTY_STRING = "";

    private AdvertisementRepository advertisementRepository;

    @Autowired
    public void setAdvertisementRepository(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    public List<Advertisement> parsePageAndGetUpdatedAds(AdOwner owner) {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        final String phantomjsPath = System.getenv(PHANTOMJS_ENV);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjsPath + PHANTOMJS_EXE);
        caps.setBrowserName("chrome");
        WebDriver driver = new PhantomJSDriver(caps);
        List<WebElement> webElements = getWebElements(owner.getUrl(), driver);
        List<Advertisement> updatedAds = getUpdatedAds(webElements, owner);
        driver.quit();
        return updatedAds;
    }

    private List<Advertisement> getUpdatedAds(List<WebElement> webElements, AdOwner owner) {
        List<Advertisement> updatedAds = new ArrayList<>();
        for (WebElement element : webElements) {
            Advertisement curAd = getAdFromWebElement(element, owner);
            if (curAd != null) {
                checkOrSaveAd(curAd, updatedAds);
            }
        }
        return updatedAds;
    }

    private List<WebElement> getWebElements(String url, WebDriver driver) {
        driver.get(url);
        scrollDown(driver, SCROLL_DOWN_PATH);
        By by = By.xpath(ACTIVE_ADS_PATH);
        List<WebElement> webElements = driver.findElements(by);
        int size;
        do {
            size = webElements.size();
            scrollDown(driver, SCROLL_DOWN_PATH);
            webElements = driver.findElements(by);
        } while (size != webElements.size());
        return webElements;
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

    private void scrollDown(WebDriver webDriver, String xpath) {
        WebElement element = webDriver.findElement(By.xpath(xpath));
        Coordinates cors = ((Locatable) element).getCoordinates();
        cors.inViewPort();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.error("Thread is interrupted {}", e.getMessage());
        }
    }

    private Advertisement getAdFromWebElement(WebElement webElement, AdOwner owner) {
        String url = webElement.getAttribute(HREF_ATTRIBUTE);
        Category category = Category.getCategoryFromUrl(url);
        if (category != null) {
            String[] adAttrs = webElement.getText().split("\n");
            String name = adAttrs[0];
            String price = adAttrs[1].replaceAll("\\D", EMPTY_STRING);
            Timestamp date = DateUtils.conertToTimestamp(adAttrs[2]);
            Advertisement advertisement = new Advertisement();
            advertisement.setOwner(owner);
            advertisement.setCategory(category);
            advertisement.setUrl(url);
            advertisement.setName(name);
            advertisement.setPrice(new BigInteger(price));
            advertisement.setLastUpdateTime(date);
            return advertisement;
        }
        return null;
    }
}
