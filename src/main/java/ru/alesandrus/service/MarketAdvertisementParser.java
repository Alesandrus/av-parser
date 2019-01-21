package ru.alesandrus.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.alesandrus.models.AdOwner;
import ru.alesandrus.models.Advertisement;
import ru.alesandrus.models.enumerations.Category;
import ru.alesandrus.utils.DateUtils;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 21.01.2019
 */
@Component
public class MarketAdvertisementParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketAdvertisementParser.class);
    private static final String AVITO_ROOT = "https://www.avito.ru";
    private static final String PHANTOMJS_ENV = "PHANTOMJS";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String PHANTOMJS_EXE = "/phantomjs.exe";
    private static final String EMPTY_STRING = "";
    private static final String MARKET_ADS_PATH = "//div[@class=\"catalog_table\"]/div/div/div[@class=\"description item_table-description\"]";
    private static final String NEXT_PAGE_PATH = "//div[@class=\"pagination-nav clearfix\"]/a[@class=\"pagination-page js-pagination-next\"]";
    private static final String AD_URL_PATH = ".//div/h3/a[@itemprop=\"url\"]";

    public List<Advertisement> parsePageAndGetAds(AdOwner owner) {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        final String phantomjsPath = System.getenv(PHANTOMJS_ENV);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjsPath + PHANTOMJS_EXE);
        caps.setBrowserName("chrome");
        WebDriver driver = new PhantomJSDriver(caps);
        List<Advertisement> ads = getAds(driver, owner);
        driver.quit();
        return ads;
    }

    private List<Advertisement> getAds(WebDriver driver, AdOwner owner) {
        driver.get(owner.getUrl());
        By by = By.xpath(MARKET_ADS_PATH);
        List<WebElement> paginations;
        List<Advertisement> advertisements = new ArrayList<>();
        do {
            List<WebElement> elements = driver.findElements(by);
            advertisements.addAll(transformToAds(elements, owner));
            paginations = driver.findElements(By.xpath(NEXT_PAGE_PATH));
            if (!paginations.isEmpty()) {
                String href = paginations.get(0).getAttribute(HREF_ATTRIBUTE);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage());
                }
                driver.get(href);
            }
        } while (!paginations.isEmpty());
        return advertisements;
    }

    private List<Advertisement> transformToAds(List<WebElement> webElements, AdOwner owner) {
        List<Advertisement> ads = new ArrayList<>();
        for (WebElement element : webElements) {
            Advertisement ad = transformToAd(element);
            if (ad != null) {
                ad.setOwner(owner);
                ads.add(ad);
            }
        }
        return ads;
    }

    private Advertisement transformToAd(WebElement element) {
        String[] text = element.getText().split("\n");
        String url = element.findElement(By.xpath(AD_URL_PATH)).getAttribute(HREF_ATTRIBUTE);
        Category category = Category.getCategoryFromUrl(url);
        if (category != null) {
            Advertisement ad = new Advertisement();
            String name = text[0];
            String price = text[1].replaceAll("\\D", EMPTY_STRING);
            String city = text[2];
            Timestamp date = DateUtils.conertToTimestamp(text[3]);
            ad.setCategory(category);
            ad.setName(name);
            ad.setLastUpdateTime(date);
            ad.setPrice(new BigInteger(price));
            ad.setCity(city);
            ad.setUrl(url);
            return ad;
        }
        return null;
    }

    /*public static void main(String[] args) throws IOException, NoSuchMethodException, InterruptedException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        final String phantomjsPath = System.getenv(PHANTOMJS_ENV);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjsPath + "/phantomjs.exe");
        WebDriver driver = new PhantomJSDriver(caps);
        driver.get("https://www.avito.ru/begemotik/rossiya");
        By by = By.xpath("//div[@class=\"catalog_table\"]/div/div/div[@class=\"description item_table-description\"]");
        List<WebElement> paginations;
        List<Advertisement> advertisements = new ArrayList<>();
        do {
            List<WebElement> elements = driver.findElements(by);
            advertisements.addAll(transformToAds(elements));
            paginations = driver.findElements(By.xpath("//div[@class=\"pagination-nav clearfix\"]/a[@class=\"pagination-page js-pagination-next\"]"));
            if (!paginations.isEmpty()) {
                String href = paginations.get(0).getAttribute("href");
                Thread.sleep(500);
                driver.get(href);
            }
        } while (!paginations.isEmpty());
        *//*for (WebElement we: elements) {
            String href = we.findElement(By.xpath(".//div/h3/a[@itemprop=\"url\"]")).getAttribute("href");
            *//**//*String p = we.getAttribute("href");
            System.out.println(p);
            String[] arr = we.getText().split("\n");
            String pr = arr[1].replaceAll("\\D", "");
            System.out.println(arr[0] + " " + pr);*//**//*
            System.out.println(we.getText());
            System.out.println(href);
        }*//*
        System.out.println(advertisements.size());
        driver.quit();
    }*/
}
