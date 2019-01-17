package ru.alesandrus.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.alesandrus.models.AdOwner;
import ru.alesandrus.models.Advertisement;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 15.01.2019
 */
@Component
public class ExcelCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelCreator.class);

    public void createReport(Map<AdOwner, List<Advertisement>> updatedAds, String fileName) {
        if (updatedAds.isEmpty()) {
            LOGGER.error("Ads are empty");
            throw new IllegalArgumentException("Ads should not be empty");
        }

        try (Workbook workbook = new HSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(fileName)) {
            Sheet sheet = workbook.createSheet("Обновленные объявления");
            createTitle(sheet);
            fillReport(updatedAds, sheet);
            workbook.write(fileOut);
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
    }

    private void createTitle(Sheet sheet) {
        Row row = sheet.createRow(0);
        Cell number = row.createCell(0);
        number.setCellValue("№ п/п");
        Cell owner = row.createCell(1);
        owner.setCellValue("Владелец");
        Cell name = row.createCell(2);
        name.setCellValue("Название");
        Cell price = row.createCell(3);
        price.setCellValue("Цена");
        Cell updatedTime = row.createCell(4);
        updatedTime.setCellValue("Время обновления");
        Cell category = row.createCell(5);
        category.setCellValue("Категория товара");
        Cell url = row.createCell(6);
        url.setCellValue("Ссылка на объявление");
    }

    private void fillReport(Map<AdOwner, List<Advertisement>> updatedAds, Sheet sheet) {
        int rowNumber = 1;
        for (Map.Entry<AdOwner, List<Advertisement>> ownerAds : updatedAds.entrySet()) {
            String ownerName = ownerAds.getKey().getName();
            List<Advertisement> ads = ownerAds.getValue();
            for (Advertisement ad : ads) {
                createRow(sheet, rowNumber, ownerName, ad);
                rowNumber++;
            }
        }
    }

    private void createRow(Sheet sheet, int rowNumber, String ownerName, Advertisement ad) {
        Row row = sheet.createRow(rowNumber);
        Cell number = row.createCell(0);
        number.setCellValue(rowNumber);
        Cell owner = row.createCell(1);
        owner.setCellValue(ownerName);
        Cell name = row.createCell(2);
        name.setCellValue(ad.getName());
        Cell price = row.createCell(3);
        price.setCellValue(ad.getPrice().toString());
        Cell updatedTime = row.createCell(4);
        updatedTime.setCellValue(ad.getLastUpdateTime());
        Cell category = row.createCell(5);
        category.setCellValue(ad.getCategory().getName());
        Cell url = row.createCell(6);
        url.setCellValue(ad.getUrl());
    }
}
