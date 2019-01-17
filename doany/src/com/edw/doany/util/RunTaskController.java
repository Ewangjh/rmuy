/**
 * 
 */
package com.edw.doany.util;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;

/**
 * @author Think
 * 2018-12-19
 * @TODO copy from mesic-oss
 */

//@Controller
//@RequestMapping(value = "/runTask")
public class RunTaskController {

	private static final Logger LOGGER = Logger.getLogger(RunTaskController.class);
	
	private ScheduledExecutorService scheduExec;
	
	public long start;
	
	public RunTaskController() {
		this.scheduExec = Executors.newScheduledThreadPool(4);
		this.start = System.currentTimeMillis();
	}
	
	//@Autowired
	private ServletContext servletContext;
	
	//@Resource
	//若干个接口引用申明
	private IOrderService orderService;
	
	private IRunTaskService runTaskService;
	
	private INodeDefService nodeDefService;

	private IParameterService parameterService;
	
	private static RunTaskThread runTaskThread;
	
	private static ScheduledFuture<?> future;
	
	public static long getTimeMillis(String time){
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
			return curDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			//logger
		}
		return 0;
	}
	
	//@PostConstruct
	public void initRunTaskTimeTask(){
		final String paramPath = servletContext.getRealPath("/WEB-INF/classes/conf/system.properties");
		String processScanTime = parameterService.getParam("processScanTime", paramPath);
		long period = Long.parseLong(processScanTime);
		
		//得到项目路径： 如D:\Java_workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp2\wtpwebapps\doany\
		final String path = servletContext.getRealPath("/");
		runTaskThread = new RunTaskThread(path);
		future = scheduExec.scheduleAtFixedRate(runTaskThread, 0, period, TimeUnit.MILLISECONDS);
		
		final long oneDay = 24 * 60 * 60 * 1000;
		long initDelay = getTimeMillis("") - System.currentTimeMillis();
		initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
		//将监控状态的订单设置为历史订单
		scheduExec.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				/*
				 * 一、获取数据库数据 最后一个运行任务生成时间
				 * 二、将一得到的时间用DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")转换诚Date
				 * 三、将二得到的date.getTime();得到Long型runTaskTime
				 * 四、判断，如果runTaskTime+oneDay < 系统当前时间，处理
				 */
			}
		}, initDelay, oneDay, TimeUnit.MILLISECONDS);
		
		//将warn状态的监控订单设置为error状态
		scheduExec.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				//同上
			}
		}, initDelay, oneDay, TimeUnit.MILLISECONDS);
		
		//删除ftp里error文件
		scheduExec.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				//获取当前目录下的所有文件
				File rootFile = new File("D:\\oss\\oss_processXml\\");
				File[] files = rootFile.listFiles();
				for(File file : files){
					if(file.isFile()){
						String fileName = file.getName();
						if(fileName.endsWith(".error")){
							file.delete();
						}
					}
				}
			}
		}, initDelay, oneDay*2, TimeUnit.MILLISECONDS);
		
		//预警
		WarnOrderThread warnOrderThread = new WarnOrderThread(paramPath);
		scheduExec.scheduleAtFixedRate(warnOrderThread, 0, 20000, TimeUnit.MILLISECONDS);
	}
	
}

class RunTaskThread implements Runnable{
	private String path = null;
	
	private IOrderService orderService;
	
	private IRunTaskService runTaskService;
	
	private INodeDefService nodeDefService;
	
	private ProcessXmlParser processXmlParser;
	
	private SimpleDateFormat sysDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public RunTaskThread(String path) {
		super();
		this.path = path;
		this.orderService = ContextLoaderListener.getCurrentWebApplicationContext()
				.getBean(IOrderService.class);
		this.runTaskService = ContextLoaderListener.getCurrentWebApplicationContext()
				.getBean(IRunTaskService.class);
		this.nodeDefService = ContextLoaderListener.getCurrentWebApplicationContext()
				.getBean(INodeDefService.class);
		this.processXmlParser = ContextLoaderListener.getCurrentWebApplicationContext()
				.getBean(ProcessXmlParser.class);
	}
	
	@Override
	public void run() {
		//扫描流程文件
		getProcessByExt("D:\\oss\\oss_processXml\\", path + "Files\\Process\\");
		System.out.println("task, get process file at "+ sysDateFormat.format(new Date(System.currentTimeMillis()))+". ");
	}
	
	private void getProcessByExt(String rootPath, String downPath){
		if(!new File(downPath).exists()){
			try {
				new File(downPath).mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String fileName  = null;
		boolean flag = false;
		
		try {
			//获取当前目录下的所有文件
			File rootFile = new File(rootPath);
			File[] files = rootFile.listFiles();
			Arrays.sort(files, new FileSortComparatorByTime());
			for(File file : files){
				fileName = file.getName();
				
				if(file.isFile()){
					if(!fileName.endsWith(".error")){
						//流程文件名的正则表达式，DMS_IPS_GF-1_20181222_012345678900.PRODORDER
						Pattern p = Pattern.compile("^[A-z]{3}_[A-z]{3}_.+_\\d{8}_\\d{12}\\.{A-z}+$");
						Matcher m = p.matcher(fileName);
						
						//如果是以ext为后缀的文件名
						if(null == runTaskService.findByFileName(fileName)){
							if(m.find()){
								//下载文件至本地文件系统中
								//获取流程信息，添加至数据库中
								flag = getProcessInfo(file, downPath);
								if(flag){
									//删除ftp服务器中的该文件
									file.delete();
								}else{
									boolean f0 = file.renameTo(new File(rootPath + fileName + ".error"));
									if(!f0){
										file.delete();
									}
								}
							}else{
								boolean f1 = file.renameTo(new File(rootPath + fileName + ".error"));
								if(!f1){
									file.delete();
								}
							}
						}else{
							boolean f2 = file.renameTo(new File(rootPath + fileName + ".error"));
							if(!f2){
								file.delete();
							}
						}
					}
					
				}else if(file.isDirectory()){
					//递归获取文件
					getProcessByExt(rootPath + fileName + "/", downPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean getProcessInfo(File file, String downPath){
		boolean flag = false;
		//流程编码
		String processCode = null;
		//消息类型
		String messageType = null;
		//消息ID
		String messageID = null;
		//发送方
		String sender = null;
		//文件名
		String fileName = file.getName();
		String[] fileName_arr = fileName.split("\\.");
		//接口类型
		String interfaceName = fileName_arr[1];
		//文件名上的messageID
		String messageID_fileName = fileName_arr[0].substring(fileName_arr[0].length()-12);
		
		//订单解析方法
		if(interfaceName.equals("BUYORDER") 
				|| interfaceName.equals("BUYORDER")
				|| interfaceName.equals("DELREORDER")
				|| interfaceName.equals("MDAORDER")){
	//		Map<String, object> map = processXmlParser
			
			
		}
		
		
		return false;
		
	}
}

class WarnOrderThread implements Runnable{
	private String paramPath;
	
	private IParameterService parameterService;
	
	private IOrderService orderService;
	
	private IRunTaskService runTaskService;
	
	private INodeDefService nodeDefService;
	
	private SimpleDateFormat sysDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public WarnOrderThread(String paramPath) {
		super();
		this.paramPath = paramPath;
		this.orderService = ContextLoaderListener.getCurrentWebApplicationContext()
				.getBean(IOrderService.class);
		this.runTaskService = ContextLoaderListener.getCurrentWebApplicationContext()
				.getBean(IRunTaskService.class);
		this.nodeDefService = ContextLoaderListener.getCurrentWebApplicationContext()
				.getBean(INodeDefService.class);
		this.parameterService = ContextLoaderListener.getCurrentWebApplicationContext()
				.getBean(IParameterService.class);
	}
	
	@Override
	public void run() {
		
	}
	
	
}

































