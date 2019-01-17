/**
 * 
 */
package com.edw.doany.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import sun.misc.BASE64Encoder;


/**
 * FreemarkerTest
 * TODO freemarker生成word文件测试
 * @author wangjh
 * @date 2018年12月29日 下午3:52:19
 */
public class FreemarkerTest {

	private volatile static FreemarkerTest fmt;
	private Configuration configure = null;
	

	/**
	 * TODO
	 * @author wangjh
	 * @date 2018年12月29日 下午3:59:12
	 */
	private FreemarkerTest(){
		configure= new Configuration();
		configure.setDefaultEncoding("utf-8");
	}
	
	/*
    * 根据word模板打印文件工具类__
    * 双检锁/双重校验锁__这种方式采用双锁机制，安全且在多线程情况下能保持高性能。
    */
	/**
	 * @method getSingleTon
	 * TODO
	 * @author wangjh
	 * @date 2018年12月29日 下午3:58:54
	 * @return
	 */
	public static FreemarkerTest getSingleTon(){
		if(fmt == null){
			synchronized (FreemarkerTest.class) {
				if(fmt == null){
					fmt = new FreemarkerTest();
				}
			}
		}
		return fmt;
	}
	
	/**
	 * 根据Doc模板生成word文件
	 * @param dataMap Map 需要填入模板的数据
	 * @param fileName 文件名称
	 * @param savePath 保存路径
	 */
	/**
	 * createDoc
	 * TODO
	 * @author wangjh
	 * @date 2018年12月29日 下午4:05:36
	 * @param dataMap
	 * @param downloadType
	 * @param savePath
	 * @return void
	 */
	public void createDoc(Map<String, Object> dataMap, String downloadType, String savePath){
		try{
			//加载需要装填的模板
			Template template  = null;
			//加载模板文件
			configure.setClassForTemplateLoading(this.getClass(),"/template");
			//设置对象包装器
			configure.setObjectWrapper(new DefaultObjectWrapper());
			//设置异常处理器
			configure.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
			//定义Template对象,注意模板类型名字与downloadType要一致
			template= configure.getTemplate(downloadType + ".ftl");
			//输出文档
			File outFile = new File(savePath);
			Writer out = null;
			out= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"utf-8"));                                    
			template.process(dataMap,out);
			outFile.delete();
			System.out.println("Finished.");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//获得图片的base64码
 //   @SuppressWarnings("deprecation")
    public static String getImageBase(String src) {
        if(src==null||src==""){
            return "";
        }
//        File file = new File(getRequest().getRealPath("/")+src.replace(getRequest().getContextPath(), ""));
        File file = new File(src);
        if(!file.exists()) {
            return "";
        }
        InputStream in = null;
        byte[] data = null;  
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {  
            data = new byte[in.available()];  
            in.read(data);  
            in.close();  
        } catch (IOException e) {  
          e.printStackTrace();  
        } 
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FreemarkerTest docUtil = FreemarkerTest.getSingleTon();
        Map<String, Object> dataMap = new HashMap<>();
        List<Map<String, Object>> tableList = new ArrayList<Map<String, Object>>();
        for(int i = 0; i< 5; i++) {
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("num", null);
            map.put("name", "name-"+i);
            tableList.add(map);
        }
        dataMap.put("tableList", tableList);
        dataMap.put("tableTag", null);
//        dataMap.put("year", "2018");
//        dataMap.put("month", "11");
//        dataMap.put("day", "22");
//        dataMap.put("businessName", "远东国际");
//        dataMap.put("businessGroupLeader", "薛胜东");
//        dataMap.put("applicant", "赵远东");
//        dataMap.put("tag", "示例");
//        dataMap.put("image", getImageBase("C:\\Users\\WANGJ\\Pictures\\7581_06151611_1_lit.jpg"));
        docUtil.createDoc(dataMap, "test04", "D:/test04.doc");
	}

}
