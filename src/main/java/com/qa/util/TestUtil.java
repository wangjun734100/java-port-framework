package com.qa.util;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.CellType;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TestUtil {
	
	/**
	 * 
	 * @param responseJson ,这个变量是拿到响应字符串通过json转换成json对象
	 * @param jpath,这个jpath指的是用户想要查询json对象的值的路径写法
	 * jpath写法举例：1) per_page  2) data[1]/first_name ，data是一个json数组，[1]表示索引
	 * /first_name 表示data数组下某一个元素下的json对象的名称为first_name
	 * @return， 返回first_name这个json对象名称对应的值
	 */
	
	//1 json解析方法
	public static String getValueByJPath(JSONObject responseJson, String jpath) {
		
		Object obj = responseJson;
		for(String s : jpath.split("/")) {
			if(!s.isEmpty()) {
				if(!(s.contains("[") || s.contains("]"))) {
					obj = ((JSONObject) obj).get(s);
				}else if(s.contains("[") || s.contains("]")) {
					obj = ((JSONArray)((JSONObject)obj).get(s.split("\\[")[0])).get(Integer.parseInt(s.split("\\[")[1].replaceAll("]", "")));
				}
			}
		}
		return obj.toString();
	}
	//获取返回的token ,使用JsonPath获取json路径
	public static HashMap<String,String> getToken(CloseableHttpResponse closeableHttpResponse,String jsonPath) throws Exception{
		HashMap<String,String> responseToken = new HashMap<String, String>();
		String responseString = EntityUtils.toString( closeableHttpResponse.getEntity(),"UTF-8");
		ReadContext ctx = JsonPath.parse(responseString);
		String Token = ctx.read(jsonPath); //"$.EFPV3AuthenticationResult.Token"
		if(null == Token||"".equals(Token)){
			new Exception("token不存在");
		}

		responseToken.put("x-ba-token",Token);
		return responseToken;
	}


	//遍历excel，sheet参数
	public static Object[][] dtt(String filePath,int sheetId) throws IOException{

		File file = new File(filePath);
		FileInputStream fis = new FileInputStream(file);

		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sh = wb.getSheetAt(sheetId);
		int numberrow = sh.getPhysicalNumberOfRows();
		int numbercell = sh.getRow(0).getLastCellNum();

		Object[][] dttData = new Object[numberrow][numbercell];
		for(int i=0;i<numberrow;i++){
			if(null==sh.getRow(i)||"".equals(sh.getRow(i))){
				continue;
			}
			for(int j=0;j<numbercell;j++) {
				if(null==sh.getRow(i).getCell(j)||"".equals(sh.getRow(i).getCell(j))){
					continue;
				}
				XSSFCell cell = sh.getRow(i).getCell(j);
				cell.setCellType(CellType.STRING);
				dttData[i][j] = cell.getStringCellValue();
			}
		}

		return dttData;
	}

	//获取状态码
	public static int getStatusCode(CloseableHttpResponse closeableHttpResponse){
		int StatusCode = closeableHttpResponse.getStatusLine().getStatusCode();
		return StatusCode;
	}

}



