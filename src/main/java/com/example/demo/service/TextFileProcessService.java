package com.example.demo.service;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pengwan on 2017/5/26.
 */
enum TableEvent {
    None,
    RowStart,
    RowEnd,
    CellStart,
    CellEnd
}
@Service
public class TextFileProcessService {
    /*
    * <tr class="c41">
    * <td valign="top" class="c38"><p class="c55"><span class="c8">A03686181_梁晶</span></p>
    * </td>
    * </tr>
    * */
    TableEvent readingEvent = TableEvent.None;
    SAXReader saxReader = new SAXReader();
    //Pattern LineReadPattern = Pattern.compile("(?<=>)(.+)(?=</)");
    static Pattern ESCPattern = Pattern.compile("&(nbsp|lt|gt|apos|quot|copy|reg|trade|mdash|ldquo|rdquo|pound|yen|euro);");
    public void processFile(File file, PrintWriter bufferedWriter) throws Exception{
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            String rowData = "";
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                //System.out.println("line " + line + ": " + tempString);

                if(tempString.startsWith("<tr")){
                    readingEvent = TableEvent.RowStart;
                    rowData+=tempString;
                }else if(tempString.startsWith("<td")){
                    readingEvent = TableEvent.CellStart;
                    rowData+=tempString;
                }else if(tempString.startsWith("</td")){
                    readingEvent = TableEvent.CellEnd;
                    rowData+=tempString;
                }else if(tempString.startsWith("</tr")){
                    readingEvent = TableEvent.RowEnd;
                    rowData+=tempString;
                    rowData = replaceESC(rowData);
                    try {
                        //生成文档对应实体
                        //Document doc = saxReader.read(rowData);
                        Document doc = saxReader.read(new ByteArrayInputStream(rowData.getBytes("UTF-8")));
                        //获取指定路径下的元素列表,这里指获取所有的data下的row元素
                        //List<Node> nodes = doc.selectNodes("//tr/td/p/span");
                        List<Node> nodes = doc.selectNodes("//tr/td");
                        StringBuilder builder = new StringBuilder();
                        for (Node node : nodes) {
                            builder.append("\""+node.getStringValue()+"\",");
                        }
                        //System.out.println(builder);
                        builder.append("\n");
                        bufferedWriter.write(builder.toString());
                    } catch (DocumentException e) {
                        e.printStackTrace();
                        System.out.println(rowData);
                        return;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    rowData = "";
                }
                if(rowData.length()>10000){
                    throw new Exception("too big row!!");
                }
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    static String replaceESC(String input){

        Matcher matcher = ESCPattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        boolean found = false;
        while (matcher.find()){
            found = true;
            String matchStr = matcher.group(1);
            matcher.appendReplacement(sb, "&amp;"+matchStr+";");
            matcher.appendTail(sb);
        }
        if(found){
            return sb.toString();
        }else {
            return input;
        }
    }
}
