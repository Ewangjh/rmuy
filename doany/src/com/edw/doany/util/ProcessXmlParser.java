/**
 * 
 */
package com.edw.doany.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.edw.doany.entity.Order;
import com.edw.doany.entity.RunTask;

/**
 * @author Think
 *
 */
//@Service("processXmlParser")
public class ProcessXmlParser {
	
	@SuppressWarnings("rawtypes")
	public Map<String , Object> parseOrderXml(File file){
		Map<String , Object> map = new HashMap<String, Object>();
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(file);
		} catch (Exception e) {
			//logger;
		}
		SAXReader saxReader = new SAXReader();
		Order order = new Order();
		RunTask runTask = new RunTask();
		
		try {
			Document document = saxReader.read(fis);
			Element root = document.getRootElement();
			for(Iterator i = root.elementIterator(); i.hasNext();){
				Element node = (Element) i.next();
				if("FileHeader".equals(node.getName())){
					//获取消息ID
					if(null != document.selectSingleNode("//MessageID")){
						String messageID = document.selectSingleNode("//MessageID").getText();
						if(messageID != null && messageID.length() >0){
							//	order.set;
							//	runTask.set;
						}
						else{
							return null;
						}
					}else{
						return null;
					}
					//获取消息类型
					
					
					//获取生成时间
					if(null != document.selectSingleNode("//MessageCreateTime")){
						String messageCreateTime = document.selectSingleNode("//MessageCreateTime").getText();
						if(messageCreateTime != null && messageCreateTime.length() >0){
							//xml文件内的时间格式，2018-12-22T15:11:11
							Pattern p = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}{T}\\{2}:\\d{2}:\\d{2}$");
							Matcher m = p.matcher(messageCreateTime);
							if(m.find()){
								messageCreateTime.replace("T", " ");
								//	order.set;messageCreateTime
								//	runTask.set;
							}else{
								return null;
							}
						}
						else{
							return null;
						}
					}else{
						return null;
					}
					
					//runTask.set运行状态为normal
				}
				
				if("FileBody".equals(node.getName())){
					//获取消息ID
					if(null != document.selectSingleNode("//MessageID")){
						String messageID = document.selectSingleNode("//MessageID").getText();
						if(messageID != null && messageID.length() >0){
							//	order.set;
							//	runTask.set;
						}
						else{
							return null;
						}
					}else{
						return null;
					}
					//获取消息类型
					
					
					//获取生成时间
					if(null != document.selectSingleNode("//MessageCreateTime")){
						String messageCreateTime = document.selectSingleNode("//MessageCreateTime").getText();
						if(messageCreateTime != null && messageCreateTime.length() >0){
							//xml文件内的时间格式，2018-12-22T15:11:11
							Pattern p = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}{T}\\{2}:\\d{2}:\\d{2}$");
							Matcher m = p.matcher(messageCreateTime);
							if(m.find()){
								messageCreateTime.replace("T", " ");
								//	order.set;messageCreateTime
								//	runTask.set;
							}else{
								return null;
							}
						}
						else{
							return null;
						}
					}else{
						return null;
					}
					
				}
				
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return map;
	}
}
