package ru.alesandrus.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
import java.time.LocalDateTime;
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

    private AdvertisementParser advertisementParser;
    private ExcelCreator excelCreator;
    private AdvertisementSender advertisementSender;
    private AdOwnerRepository adOwnerRepository;

    @Autowired
    public void setAdvertisementParser(AdvertisementParser advertisementParser) {
        this.advertisementParser = advertisementParser;
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

    @Scheduled(cron = "0 0/30 7-23 * * *")
//    @Scheduled(fixedRate = 100000)
    public void watch() {
        Iterable<AdOwner> adOwners = adOwnerRepository.findAll();
        Map<AdOwner, List<Advertisement>> adsForSending = new TreeMap<>();
        for (AdOwner adOwner : adOwners) {
            List<Advertisement> updatedAds = new ArrayList<>();
            if (adOwner.isActive()) {
                if (adOwner.getOwnerType() == OwnerType.COMPANY) {
                    updatedAds = advertisementParser.parsePageAndGetUpdatedAds(adOwner);
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





    /*public static void main(String[] args) throws IOException, ScriptException, NoSuchMethodException, InterruptedException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        final String phantomjsPath = System.getenv(PHANTOMJS_ENV);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjsPath + "/phantomjs.exe");
        WebDriver driver = new PhantomJSDriver(caps);
        driver.get("https://www.avito.ru/user/f20b2dd57349fd96815bbdc7f581308b/profile?id=810817888&src=item");
        WebElement e=driver.findElement(By.xpath("//div[@class=\"js-footer\"]"));
        Coordinates cor=((Locatable)e).getCoordinates();
        cor.inViewPort();
        Thread.sleep(1000);

        By by = By.xpath("//div[@data-marker=\"profile-item-box\"]/div[@itemprop=\"makesOffer\"]/a");
//        By by = By.xpath("//div[@data-marker=\"profile-item(1501757134)\"]");
        List<WebElement> elements = driver.findElements(by);
        int size;
        do {
            size = elements.size();
            cor.inViewPort();
            Thread.sleep(1000);
            elements = driver.findElements(by);
        } while (size != elements.size());
        for (WebElement we: elements) {
            String p = we.getAttribute("href");
            System.out.println(p);
            String[] arr = we.getText().split("\n");
            String pr = arr[1].replaceAll("\\D", "");
            System.out.println(arr[0] + " " + pr);
            System.out.println(getDate(arr[2]));
        }
        System.out.println(elements.size() );
        driver.quit();
    }*/

    public static void main(String[] args) throws IOException, InterruptedException {

    }
}
