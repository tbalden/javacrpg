/*
*   This file is part of jClassicRPG.
*	Copyright (C) 2008 Illes Pal Zoltan
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/                                                                       

package org.jcrpg.world.ai;

import java.io.File;
import java.util.ArrayList;

public class MusicDescription {
	
	private String backgroundMusicCollectionLocation = null;
	
	public MusicDescription(String backgroundMusicCollectionLocation)
	{
		this.backgroundMusicCollectionLocation = backgroundMusicCollectionLocation;
	}
	
	public static final String PATH="./data/audio/music/background/";

	String lastPlayed = null;
	public String getRandomBackgroundMusicFileOfCollection()
	{
		String path = PATH+backgroundMusicCollectionLocation;
		File f = new File(path);
		if (f.isDirectory())
		{
			String[] list = f.list();
			int c = 0;
			ArrayList<String> locations = new ArrayList<String>();
			for (String l:list)
			{
				if (l.endsWith(".ogg"))
				{
					if (new File(path+"/"+l).isFile())
					{
						c++;
						locations.add(path+"/"+l);
					}
				}
			}
			int rand = (int)((Math.random()*100)%c);
			String ret = locations.get(rand);
			lastPlayed = ret;
			return ret;
		} else
		{
			return PATH+backgroundMusicCollectionLocation;
		}
	}

}
