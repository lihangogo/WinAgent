package com.utils;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;

public class readXLSX {


    public HashMap<String,String> getStudentList_2018(){
        HashMap<String,String> list=new HashMap<>();
        XSSFWorkbook xb = null;
        XSSFSheet sheet=null;
        XSSFRow row=null;
        XSSFCell cell0=null,cell1=null,cell2=null,cell3=null,cell4=null,cell5=null,cell6=null;
        Connection Conn=DBhelper.getConnection();


        try{
            Statement ss=Conn.createStatement();
            InputStream is=this.getClass().getResource("/resource/2018student.xlsx").openStream();
            xb=new XSSFWorkbook(is);
            sheet=xb.getSheetAt(0);
            for(int rowNum=1;rowNum<=sheet.getLastRowNum();rowNum++){
                row=sheet.getRow(rowNum);
                if(row!=null){

                    cell0=row.getCell(0);
                     cell1=row.getCell(1);
                    cell1.setCellType(1);
                    cell2=row.getCell(2);
                     cell3=row.getCell(3);
                     cell4=row.getCell(4);
                     cell5=row.getCell(5);
                     cell6=row.getCell(6);
                    System.out.println(cell0.getStringCellValue()+" "+
                        cell1.getStringCellValue()+" "+
                        cell2.getStringCellValue()+" "+
                        cell3.getStringCellValue()+" "+
                        cell4.getStringCellValue()+" "+
                        cell5.getStringCellValue()+" "+
                        cell6.getStringCellValue());

                    String sql="insert into 2018student(id,name,gender,college,major,tutor,kind)" +
                            " values('"+cell1.getStringCellValue()+"','"+
                            cell2.getStringCellValue()+"','"+
                            cell3.getStringCellValue()+"','"+
                            cell4.getStringCellValue()+"','"+
                            cell5.getStringCellValue()+"','"+
                            cell6.getStringCellValue()+"','"+
                            cell0.getStringCellValue()+
                            "');";
                    //cell0.getStringCellValue()+
                    System.out.println(new String(sql.getBytes("utf-8")));
                    ss.execute(sql);
                }
            }
            Conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Test
    public void get(){
        //System.out.println(DBhelper.getConnection());
        getStudentList_2018();

    }
}
