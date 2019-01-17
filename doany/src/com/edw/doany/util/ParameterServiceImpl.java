/**
 * 
 */
package com.edw.doany.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Think
 *
 */
//@Service("parameterService")
public class ParameterServiceImpl implements IParameterService{

	@Override
	public String getParam(String pKey, String path) {

		File file = new File(path);
		String pValue = null;
		
		try {
			InputStream in = new FileInputStream(file);
			Properties p = new Properties();
			p.load(in);
			in.close();
			if(pKey != null && !pKey.trim().isEmpty()){
				pValue = p.getProperty(pKey);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pValue;
	}

}
