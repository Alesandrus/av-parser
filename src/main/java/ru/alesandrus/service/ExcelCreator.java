package ru.alesandrus.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import ru.alesandrus.models.Advertisement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 15.01.2019
 */
@Component
public class ExcelCreator {
    public void createReport(Map<String, List<Advertisement>> ads, String fileName) {
        /*if (ads.isEmpty()) {
            throw new IllegalArgumentException("Ads should not be empty");
        }*/

        try (Workbook workbook = new HSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(fileName)){
            Sheet sheet = workbook.createSheet("Обновленные объявления");
            titleCreate(sheet);
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void titleCreate(Sheet sheet) {
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

    public static void main(String[] args) {
        ExcelCreator creator = new ExcelCreator();
        creator.createReport(new HashMap<>(), "D:/Users/Public/Window/TFS/test.xls");
    }
}
