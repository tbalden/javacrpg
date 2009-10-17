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

import org.jcrpg.game.scenario.Events.Event;
import org.jcrpg.game.scenario.ScenarioLoader.ScenarioDescription;
import org.jcrpg.threed.J3DCore;

public class Scenario {

	ScenarioDescription desc;
	public File worldParams;
	public File ecology;
	public File eventsFile;
	
	public Events events;
	
	
	public static final String INTRO_PLAYED = "intPlyd";
	
	public void initiateScenario()
	{
		Boolean introPlayed = J3DCore.getInstance().gameState.scenarioState.isTrue(INTRO_PLAYED);
		
		if (introPlayed!=null && introPlayed)
		{
			//J3DCore.getInstance().gameState.engine.setPause(false);
		} else
		{
			if (events.intro!=null)
			{
				playEvent(events.intro);
			}
			J3DCore.getInstance().gameState.scenarioState.setTrue(INTRO_PLAYED);
		}
	}
	
	public void playEvent(Event event)
	{
		event.play();
	}
	
	public void finishedEvent(Event event)
	{
		J3DCore.getInstance().gameState.engine.setPause(false);
	}
	
	public void load() throws Exception
	{
		File[] files = desc.directory.listFiles();
		for (File file:files)
		{
			if (file.getAbsolutePath().endsWith("worldparams.xml"))
			{
				worldParams = file;
			}
			if (file.getAbsolutePath().endsWith("ecology.xml"))
			{
				ecology = file;
			}
			if (file.getAbsolutePath().endsWith("events.xml"))
			{
				eventsFile = file;
				events = new Events(this,file,desc.directory.getAbsolutePath());
			}
		}
	}
	
}
