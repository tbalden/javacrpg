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

package org.jcrpg.audio;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;

public class AudioServer {

	HashMap<String, AudioTrack> hmTracks = new HashMap<String, AudioTrack>();
	
	HashSet<String> played = new HashSet<String>();
	HashSet<String> paused = new HashSet<String>();
	
	public AudioServer()
	{
		try {
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/warrior/warrior.ogg").toURL(), true);
			mainTheme.setType(TrackType.POSITIONAL);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(1f);
			hmTracks.put("main", mainTheme);
			mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/oak/oak.ogg").toURL(), true);
			mainTheme.setType(TrackType.POSITIONAL);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(0.05f);
			hmTracks.put("ingame", mainTheme);
			
		}catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		
	}
	
	public void init()
	{
		pause("ingame");
	}
	
	public void playOnlyThis(String id)
	{
		for (String p:played)
		{
			pause(p);
		}
		play(id);
	}
	public void stopAndResumeOthers(String id)
	{
		stop(id);
		played.remove(id);
		for (String p:paused)
		{
			play(p);
		}
	}
	
	public void play(String id)
	{
		try {
			if (!hmTracks.get(id).isPlaying()) {
				float v = hmTracks.get(id).getVolume();
				hmTracks.get(id).play();
				hmTracks.get(id).fadeIn(v, v);
			}
			played.add(id);
		} catch (NullPointerException npex)
		{
			npex.printStackTrace();
		}
	}
	
	public void pause(String id)
	{
		try {
			//hmTracks.get(id).fadeOut(0.7f);
			if (hmTracks.get(id).isPlaying())
				hmTracks.get(id).pause();
			played.remove(id);
			paused.add(id);
		} catch (NullPointerException npex)
		{
			npex.printStackTrace();
		}
	}
	
	public void stop(String id)
	{
		try {
			//hmTracks.get(id).fadeOut(0.7f);
			if (hmTracks.get(id).isPlaying())
				hmTracks.get(id).stop();
			played.remove(id);
			paused.remove(id);
		} catch (NullPointerException npex)
		{
			npex.printStackTrace();
		}
	}
	public void stopStart(String id, String startId)
	{
		try {
			//hmTracks.get(id).fadeOut(0.7f);
			if (hmTracks.get(id).isPlaying())
				hmTracks.get(id).stop();
			hmTracks.get(startId).play();
		} catch (NullPointerException npex)
		{
			npex.printStackTrace();
		}
	}
	
}
