package ru.alesandrus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.alesandrus.models.Advertisement;

import java.util.*;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 13.01.2019
 */
@Component
public class AdvertisementWatcher {
    private static Map<String, String> rivals = new HashMap<>();
    static {
        rivals.put("Dmitry", "https://www.avito.ru/user/035a3d92e035ed50e9e1283b0aac6031/profile?id=1247568718&src=item");
    }

    private AdvertisementParser advertisementParser;
    private AdvertisementSender advertisementSender;

    @Autowired
    public void setAdvertisementParser(AdvertisementParser advertisementParser) {
        this.advertisementParser = advertisementParser;
    }

    @Autowired
    public void setAdvertisementSender(AdvertisementSender advertisementSender) {
        this.advertisementSender = advertisementSender;
    }

    //    @Scheduled(cron = "0 0/30 7-23 * * *")
    @Scheduled(fixedRate = 120000)
    public void watch() {
        Map<String, List<Advertisement>> adsForSending = new TreeMap<>();
        for (Map.Entry<String, String> ownerAds : rivals.entrySet()) {
            List<Advertisement> updatedAds = advertisementParser.parsePageAndGetUpdatedAds(ownerAds.getValue());
            if (!updatedAds.isEmpty()) {
                Collections.sort(updatedAds);
                adsForSending.put(ownerAds.getKey(), updatedAds);
            }
        }
        if (!adsForSending.isEmpty()) {
            //отправить email со списком новых объявлений
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
}
