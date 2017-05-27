package com.example.demo.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by pengwan on 2017/5/26.
 */
@Service
public class XlsxFileProcessService {
    public void processFile(File xlsxFile) throws Exception{
        XSSFWorkbook book =  new XSSFWorkbook(new FileInputStream(xlsxFile));
        XSSFSheet sheet = book.getSheetAt(0);
        for(int i=2; i<sheet.getLastRowNum()+1; i++) {
            XSSFRow row = sheet.getRow(i);
            int colNum = row.getLastCellNum()+1;
            for(int j=0; j<colNum; j++){
                String cellValue = row.getCell(j).getStringCellValue();
                System.out.print(cellValue+",\t");
            }
            System.out.print("\n");
        }
    }
}
