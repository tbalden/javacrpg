/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 

package org.jcrpg.ui;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;

import org.jcrpg.ui.text.FontTT;

public class FontUtils {
	
	public static final String GEN_FONT_PATH = "./data/font/free/vinque.ttf"; //"./data/font/free/Sebaldus-Gotisch.ttf"
	
	public static Font fontVerdana = new Font("Verdana", Font.BOLD, 32);
	//public static Font f = new Font();
	static {
		try
		{
			//fontVerdana = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("./data/font/free/FetteTrumpDeutsch.ttf")));
			fontVerdana = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File(GEN_FONT_PATH)));
		} catch (Exception ex) {ex.printStackTrace();}
	}
	public static FontTT textVerdana = new FontTT(fontVerdana.deriveFont(Font.BOLD),44,0);
	public static Font fontNonBoldVerdana = new Font("Verdana", Font.PLAIN, 32);
	static {
		try
		{
			//fontNonBoldVerdana = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("./data/font/free/FetteTrumpDeutsch.ttf")));
			fontNonBoldVerdana = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File(GEN_FONT_PATH)));
		} catch (Exception ex) {ex.printStackTrace();}
	}
	public static FontTT textNonBoldVerdana = new FontTT(fontVerdana.deriveFont(Font.BOLD),44,0);

}
