package com.example.demo.service;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by pengwan on 2017/5/26.
 */
@Service
public class XlsFileProcessService {
    public void processFile(File xlsFile) throws Exception{
        HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(xlsFile));
        HSSFSheet sheet = book.getSheetAt(0);
        for(int i=2; i<sheet.getLastRowNum()+1; i++) {
            HSSFRow row = sheet.getRow(i);
            int colNum = row.getLastCellNum()+1;
            for(int j=0; j<colNum; j++){
                String cellValue = row.getCell(j).getStringCellValue();
                System.out.print(cellValue+",\t");
            }
            System.out.print("\n");
        }
    }
}
