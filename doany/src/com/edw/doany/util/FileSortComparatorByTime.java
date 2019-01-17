/**
 * 
 */
package com.edw.doany.util;

import java.io.File;
import java.util.Comparator;

/**
 * @author Think
 *
 */
public class FileSortComparatorByTime implements Comparator<File>{

	@Override
	public int compare(File f1, File f2) {
		Long t1 = f1.lastModified();
		Long t2 = f2.lastModified();
		
		if(f1.getName().contains("PSS_DMS")){
			t1 = t1 - (60000 * 5);
		}
		if(f2.getName().contains("PSS_DMS")){
			t2 = t2 - (60000 * 5);
		}
		if(f1.getName().contains("PSS_IPS")){
			t1 = t1 - (60000 * 5);
		}
		if(f2.getName().contains("PSS_IPS")){
			t2 = t2 - (60000 * 5);
		}
		if(f1.getName().contains("DMS_IPS") && f1.getName().contains("PRODORDER")){
			t1 = t1 - (60000 * 5);
		}
		if(f2.getName().contains("DMS_IPS") && f2.getName().contains("PRODORDER")){
			t2 = t2 - (60000 * 5);
		}
		
		if(f1.getName().contains("PSS_TAS")){
			t1 = t1 - (60000 * 5);
		}
		if(f2.getName().contains("PSS_TAS")){
			t2 = t2 - (60000 * 5);
		}

		if(f1.getName().contains("DMS_PSS") && f1.getName().contains("DELREORDER")){
			t1 = t1 - (60000 * 5);
		}
		if(f2.getName().contains("DMS_PSS") && f2.getName().contains("DELREORDER")){
			t2 = t2 - (60000 * 5);
		}
			
		return t1.compareTo(t2);
	}

}
