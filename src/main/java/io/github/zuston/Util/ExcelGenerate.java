package io.github.zuston.Util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by zuston on 17/4/16.
 */
public class ExcelGenerate {
    public static String excelGenerate(ArrayList<LinkedHashMap<String,String>> container) throws IOException, NoSuchAlgorithmException {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("元素信息");
        HSSFCellStyle hssfCellStyle = (HSSFCellStyle) workbook.createCellStyle();
        hssfCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//居中显示
        hssfCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//纵向居中

        HSSFCellStyle specialStyle = (HSSFCellStyle) workbook.createCellStyle();
        specialStyle.setFillBackgroundColor(HSSFColor.BLUE.index);

        //创建行
        Row row = sheet.createRow(0);


        Cell cell = row.createCell(0);
        //设置第一行第一格的值
        cell.setCellValue("m-id");
        //设置单元格的文本居中显示
        cell.setCellStyle(hssfCellStyle);

        //创建单元格
        Cell cell0 = row.createCell(1);
        //设置第一行第一格的值
        cell0.setCellValue("名称");
        //设置单元格的文本居中显示
        cell0.setCellStyle(hssfCellStyle);
        //创建单元格
        Cell cell1 = row.createCell(2);
        //设置第一行第一格的值
        cell1.setCellValue("空间群");
        cell1.setCellStyle(hssfCellStyle);
        //创建单元格
        Cell cell2 = row.createCell(3);
        //设置第一行第一格的值
        cell2.setCellValue("valence_electrons_sum");
        cell2.setCellStyle(hssfCellStyle);
        //创建单元格
        Cell cell3 = row.createCell(4);
        //设置第一行第一格的值
        cell3.setCellValue("space_group_type_num");
        cell3.setCellStyle(hssfCellStyle);



        int count = 1;

        for (LinkedHashMap<String,String> hm:container){

            Row rows = sheet.createRow(count);
            Cell cell0s = rows.createCell(0);
            cell0s.setCellValue(hm.get("original_id"));
            Cell cells = rows.createCell(1);
            cells.setCellValue(hm.get("化合物名称"));
            Cell cell1s = rows.createCell(2);
            cell1s.setCellValue(hm.get("空间群"));
            Cell cell2s = rows.createCell(3);
            cell2s.setCellValue(hm.get("valence_electrons_sum"));
            Cell cell3s = rows.createCell(4);
            cell3s.setCellValue(hm.get("space_group_type_num"));
            count++;

            if (Double.valueOf(hm.get("space_group_type_num"))==216.0){
                rows.setRowStyle(specialStyle);
            }

        }
        String path = RedisSession.generateMd5(container.toString());
        FileOutputStream fout = new FileOutputStream("/usr/local/Cellar/openresty/1.11.2.2_2/nginx/html/static/"+ path+".xlsx");
        workbook.write(fout);
        workbook.close();
        fout.close();
        return path+".xlsx";
    }
}
