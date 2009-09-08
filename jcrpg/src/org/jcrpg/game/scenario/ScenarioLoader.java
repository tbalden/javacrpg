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

package org.jcrpg.game.scenario;

import java.io.File;
import java.util.ArrayList;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.Language;

import com.jme.scene.shape.Quad;

public class ScenarioLoader {

	public class ScenarioDescription
	{
		public File directory = null;
		public String name;
		public String descText;
		public String date;	
		public String license;
		public String author;
		public String descImageName;
		public Quad descImage;
		public String version;
		
		public ScenarioDescription(File dirOfScenario)
		{
			directory = dirOfScenario;
			File[] files = dirOfScenario.listFiles();
			for (File f:files)
			{
				if (f.getAbsolutePath().endsWith("scenario.properties"))
				{
					Language l = new Language(f);
					name = l.getValue("name");
					descText = l.getValue("descText");
					date = l.getValue("date");
					license = l.getValue("license");
					author = l.getValue("author");
					descImageName = l.getValue("descImageName");
					version = l.getValue("version");
				}
			}
		}
	}
	
	public ArrayList<ScenarioDescription> listOfScenarios = new ArrayList<ScenarioDescription>();

	String baseDir = null;
	public ScenarioLoader(J3DCore core, String baseDir)
	{
		this.baseDir = baseDir;
		updateList();
	}
	
	public void updateList()
	{
		listOfScenarios.clear();
		File f = new File(baseDir);
		if (f.isDirectory())
		{
			for (File fD :f.listFiles())
			{
				if (fD.isDirectory())
				{
					ScenarioDescription d = new ScenarioDescription(fD);
					if (d.name!=null)
					{
						listOfScenarios.add(d);
					}
				}
			}
		}
	}
	
	public Scenario load(ScenarioDescription desc)
	{
		Scenario s = new Scenario();
		s.desc = desc;
		s.load();
		return s;
	}
	
}
