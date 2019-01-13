package ru.alesandrus.watchers;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.alesandrus.models.Advertisement;
import ru.alesandrus.repositories.AdvertismentRepository;

import javax.script.ScriptException;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 13.01.2019
 */
@Component
public class DmitryCompanyWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DmitryCompanyWatcher.class);
    private static final String DMITRY_HTTP_PATH = "https://www.avito.ru/user/035a3d92e035ed50e9e1283b0aac6031/profile?id=1247568718&src=item";
    private static final String avitoRoot = "https://www.avito.ru";
    private static final String PHANTOMJS_ENV = "PHANTOMJS";


    @Autowired
    private AdvertismentRepository advertismentRepository;

    //    @Scheduled(cron = "0 0/30 7-23 * * *")
    @Scheduled(fixedRate = 30000)
    public void parseDmitryMainPage() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        final String phantomjsPath = System.getenv(PHANTOMJS_ENV);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjsPath + "/phantomjs.exe");
        WebDriver driver = new PhantomJSDriver(caps);
        driver.get(DMITRY_HTTP_PATH);
        scrollDown(driver, "//div[@class=\"js-footer\"]");

        By by = By.xpath("//div[@data-marker=\"profile-item-box\"]/div[@itemprop=\"makesOffer\"]/a");
        List<WebElement> webElements = driver.findElements(by);

        List<Advertisement> updatedAds = new ArrayList<>();
        for (WebElement element : webElements) {
            Advertisement ad = getAd(element);
            //если обяъвление обновлено, то добавить в updatedAds
        }

        //если updatedAds не пустой, то отправить email со списком новых объявлений

        driver.quit();
    }

    public void scrollDown(WebDriver webDriver, String xpath) {
        WebElement element = webDriver.findElement(By.xpath(xpath));
        Coordinates cors = ((Locatable) element).getCoordinates();
        cors.inViewPort();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.error("Thread is interrupted {}", e.getMessage());
        }
    }

    public Advertisement getAd(WebElement webElement) {
        String url = webElement.getAttribute("href");
        String[] adAttrs = webElement.getText().split("\n");
        String name = adAttrs[0];
        String price = adAttrs[1].replaceAll("\\D", "");
        //Timestamp date = getDate(adAttrs[2]);
        Advertisement advertisement = new Advertisement();
        advertisement.setUrl(url);
        advertisement.setName(name);
        advertisement.setPrice(new BigInteger(price));
        //advertisement.setLastUpdateTime(date);
        return advertisement;
    }


    public static void main(String[] args) throws IOException, ScriptException, NoSuchMethodException, InterruptedException {
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
    }

    public static LocalDateTime getDate(String str) {
        LocalDateTime date;
        String[] arr = str.split("\\s|:");
        if(str.contains("Сегодня")) {
            date = LocalDateTime.of(LocalDate.now(), LocalTime.of(Integer.parseInt(arr[1]), Integer.parseInt(arr[2])));
        } else if (str.contains("Вчера")) {
            date = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(Integer.parseInt(arr[1]), Integer.parseInt(arr[2])));
        } else if (!str.contains(":")) {
            date = LocalDateTime.of(Integer.parseInt(arr[2]), parseMonth(arr[1]), Integer.parseInt(arr[0]), 0, 0);
        } else {
            int currentYear = LocalDate.now().getYear();
            date = LocalDateTime.of(currentYear, parseMonth(arr[1]), Integer.parseInt(arr[0]),
                    Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
        }
        return date;
    }

    public static int parseMonth(String s) {
        switch (s) {
            case "января" : return 1;
            case "февраля" : return 2;
            case "марта" : return 3;
            case "апреля" : return 4;
            case "мая" : return 5;
            case "июня" : return 6;
            case "июля" : return 7;
            case "августа" : return 8;
            case "сентября" : return 9;
            case "октября" : return 10;
            case "ноября" : return 11;
            case "декабря" : return 12;
        }
        return -1;
    }
}
