package ru.alesandrus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 13.01.2019
 */
@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        //System.setProperty("webdriver.chrome.driver", "D:\\Установки\\chromedriver_win32\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", "/home/chromedriver");
        SpringApplication.run(Application.class);
    }
}
