package com.example.demo.controller;

import com.example.demo.service.TextFileProcessService;
import com.example.demo.service.XlsFileProcessService;
import com.example.demo.service.Xlsx2CsvProcessService;
import com.example.demo.service.XlsxFileProcessService;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Created by pengwan on 2017/5/25.
 */
@Controller
public class FileUploadController {
    @Autowired
    private XlsFileProcessService xlsFileProcessService;
    @Autowired
    private XlsxFileProcessService xlsxFileProcessService;
    @Autowired
    private Xlsx2CsvProcessService xlsx2CsvProcessService;
    @Autowired
    private TextFileProcessService textFileProcessService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(@RequestParam("file")MultipartFile file){
        if(!file.isEmpty()){
            try {
                File dataFile = new File(file.getOriginalFilename());
                BufferedOutputStream upload = new BufferedOutputStream(new FileOutputStream(
                        dataFile));
                upload.write(file.getBytes());
                upload.flush();
                upload.close();

                OutputStream os = new FileOutputStream("result.csv");
                os.write(239);   // 0xEF
                os.write(187);   // 0xBB
                os.write(191);   // 0xBF
                PrintWriter download = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
                try {
                    this.textFileProcessService.processFile(dataFile, download);
                } finally {
                    download.flush();
                    download.close();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "upload fail,"+e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "upload fail,"+e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                return "process fail,"+e.getMessage();
            }
            return "process success";
        }else{
            return "upload fail，empty file.";
        }
    }

    @GetMapping(value = "/download")
    public ResponseEntity<InputStreamResource> downloadFile(Long id)
            throws IOException {
        FileSystemResource file = new FileSystemResource("result.csv");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }

}
