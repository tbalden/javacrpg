/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.jcrpg.threed.J3DCore;

public class Language {

	public static final String LANG_DIR = "./data/lang/";
	
	Properties properties = new Properties();
	
	public Language(String lang)
	{
		File f;
		try {
			
			f = new File(LANG_DIR+"language_"+lang+".properties");
			if (!f.exists())
			{
				lang = "";
				f = new File(LANG_DIR+"language.properties");
			}
		} catch (Exception ex)
		{
			f = new File(LANG_DIR+"language.properties");
		}
		try {
			properties.load(new FileInputStream(f));
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public String getValue(String key)
	{
		String r = properties.getProperty(key);
		if (r==null)
		{
			try {
				return key.substring(key.lastIndexOf('.')+1);
			} catch (Exception ex)
			{}
		}
		return r;
	}
	
	public static String v(String key)
	{
		return J3DCore.getInstance().language.getValue(key);
	}
}
