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
        Sheet sheet = workbook.createSheet("sheet1");
        HSSFCellStyle hssfCellStyle = (HSSFCellStyle) workbook.createCellStyle();
        hssfCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//居中显示
        hssfCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//纵向居中

        HSSFCellStyle specialStyle = (HSSFCellStyle) workbook.createCellStyle();
        specialStyle.setFillBackgroundColor(HSSFColor.BLUE.index);

        //创建行
        Row row = sheet.createRow(0);


        Cell cell = row.createCell(0);
        //设置第一行第一格的值
        cell.setCellValue("Index");
        //设置单元格的文本居中显示
        cell.setCellStyle(hssfCellStyle);

        //创建单元格
        Cell cell0 = row.createCell(1);
        //设置第一行第一格的值
        cell0.setCellValue("ChemicalFormula");
        //设置单元格的文本居中显示
        cell0.setCellStyle(hssfCellStyle);
        //创建单元格
        Cell cell1 = row.createCell(2);
        //设置第一行第一格的值
        cell1.setCellValue("SpaceGroup");
        cell1.setCellStyle(hssfCellStyle);
        //创建单元格
        Cell cell2 = row.createCell(3);
        //设置第一行第一格的值
        cell2.setCellValue("Atomic-Average-Mass");
        cell2.setCellStyle(hssfCellStyle);
        //创建单元格
        Cell cell3 = row.createCell(4);
        //设置第一行第一格的值
        cell3.setCellValue("SpaceGroup-Number");
        cell3.setCellStyle(hssfCellStyle);



        int count = 1;
        for (LinkedHashMap<String,String> hm:container){
            Row rows = sheet.createRow(count);
            Cell cell0s = rows.createCell(0);
//            cell0s.setCellValue(hm.get("original_id"));
            cell0s.setCellValue(count+"");
            Cell cells = rows.createCell(1);
            cells.setCellValue(hm.get("element_simple_name")+"("+hm.get("formula")+")");
            Cell cell1s = rows.createCell(2);
            cell1s.setCellValue(hm.get("spacegroup"));
            Cell cell2s = rows.createCell(3);
            cell2s.setCellValue(hm.get("atomic_average_mass"));
            Cell cell3s = rows.createCell(4);
            cell3s.setCellValue(hm.get("space_group_type_num"));
            count++;

        }
        String path = RedisSession.generateMd5(container.toString());
//        FileOutputStream fout = new FileOutputStream("/usr/local/Cellar/openresty/1.11.2.2_2/nginx/html/static/"+ path+".xls");
        FileOutputStream fout = new FileOutputStream("/temp/"+ path+".xls");
//        FileOutputStream fout = new FileOutputStream("/opt/openresty/nginx/html/static/"+ path+".xls");
        workbook.write(fout);
        workbook.close();
        fout.close();
        return path+".xls";
    }
}
