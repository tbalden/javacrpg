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
	
	public static final String STEP_ROCK = "steps_rock";
	public static final String STEP_SOFT = "steps_soft";
	public static final String STEP_SOIL = "steps_soil";
	public static final String STEP_SNOW = "steps_snow";
	public static final String STEP_WATER = "steps_water";
	public static final String STEP_FLY = "steps_fly";
	public static final String STEP_SWIM = "steps_swim";
	public static final String STEP_NO_WAY= "noway";
	
	public static final String[] stepTypes = new String[] {
		STEP_SOIL, STEP_NO_WAY
	};
	
	public AudioServer()
	{
		try {
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/warrior/warrior.ogg").toURL(), true);
			mainTheme.setType(TrackType.MUSIC);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(1f);
			hmTracks.put("main", mainTheme);
		}catch (Exception ex)
		{
			ex.printStackTrace();
		}
		try {	
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/oak/oak.ogg").toURL(), true);
			mainTheme.setType(TrackType.MUSIC);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(0.05f);
			hmTracks.put("ingame", mainTheme);
			
		}catch (Exception ex)
		{
			ex.printStackTrace();
		}
		for (String step:stepTypes)
		{
			try {
				AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/sound/steps/"+step+".ogg").toURL(), true);
				mainTheme.setType(TrackType.HEADSPACE);
				mainTheme.setRelative(false);
				mainTheme.setLooping(false);
				mainTheme.setVolume(1f);
				hmTracks.put(step, mainTheme);
			} catch (Exception ex)
			{
				
			}
			
		}
		
		
	}
	
	public void init()
	{
		pause("ingame");
	}
	
	public void playOnlyThis(String id)
	{
		String[] playedA = played.toArray(new String[0]); 
		for (String p:playedA)
		{
			pause(p);
		}
		play(id);
	}
	public void stopAndResumeOthers(String id)
	{
		stop(id);
		played.remove(id);
		String[] pausedA = paused.toArray(new String[0]); 
		for (String p:pausedA)
		{
			play(p);
		}
	}
	
	public synchronized void play(String id)
	{
		try {
			if (!hmTracks.get(id).isPlaying()) {
				float v = hmTracks.get(id).getVolume();
				hmTracks.get(id).play();
				if (hmTracks.get(id).getType().equals(TrackType.MUSIC)) {
					hmTracks.get(id).fadeIn(v, v);
				}
			}
			played.add(id);
		} catch (NullPointerException npex)
		{
			npex.printStackTrace();
		}
	}
	
	public synchronized  void pause(String id)
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
	
	public synchronized  void stop(String id)
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
	public synchronized  void stopStart(String id, String startId)
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
