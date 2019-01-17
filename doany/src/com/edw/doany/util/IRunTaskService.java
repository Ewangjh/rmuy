/**
 * 
 */
package com.edw.doany.util;

import com.edw.doany.entity.RunTask;

/**
 * @author Think
 *
 */
public interface IRunTaskService {

	public RunTask findByFileName(String fileName);
}
