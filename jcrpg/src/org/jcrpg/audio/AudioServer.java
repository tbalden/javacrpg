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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;

public class AudioServer implements Runnable {

	HashMap<String, ArrayList<AudioTrack>> hmTracks = new HashMap<String, ArrayList<AudioTrack>>();
	HashMap<String, String> hmTracksAndFiles = new HashMap<String, String>();
	
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
	
	public static final String EVENT_ENC1 = "enc1";
	
	public int playedAtOnce = 0;
	public int MAX_PLAYED = 16;
	
	public static final String[] stepTypes = new String[] {
		STEP_SOIL, STEP_NO_WAY
	};
	
	
	public ArrayList<Channel> channels = new ArrayList<Channel>();
	public AudioServer()
	{
		for (int i=0; i<MAX_PLAYED; i++)
		{
			channels.add(new Channel(this));
		}
		
		try {
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/warrior/warrior.ogg").toURL(), true);
			mainTheme.setType(TrackType.MUSIC);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(1f);
			ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
			tracks.add(mainTheme);
			hmTracks.put("main", tracks);
		}catch (Exception ex)
		{
			ex.printStackTrace();
		}
		try {	
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/oak/oak.ogg").toURL(), true);
			mainTheme.setType(TrackType.MUSIC);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(0.07f);
			ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
			tracks.add(mainTheme);
			hmTracks.put("ingame", tracks);
			
		}catch (Exception ex)
		{
			ex.printStackTrace();
		}
		for (String step:stepTypes)
		{
			try {
				AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/sound/steps/"+step+".ogg").toURL(), true);
				mainTheme.setType(TrackType.POSITIONAL);
				mainTheme.setRelative(false);
				mainTheme.setLooping(false);
				mainTheme.setVolume(1f);
				ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
				tracks.add(mainTheme);
				hmTracksAndFiles.put(step, "./data/audio/sound/steps/"+step+".ogg");
				hmTracks.put(step, tracks);
			} catch (Exception ex)
			{
				
			}
			
		}
		addTrack(EVENT_ENC1, "./data/audio/sound/events/enc1.ogg");
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
		
	}
	
	public void init()
	{
		setChannelNotPlayingYet("ingame");
	}
	
	
	public synchronized Channel getAvailableChannel()
	{
		for (Channel c:channels)
		{
			if (c.isAvailable()) return c;
		}
		return null;
	}
	
	public synchronized void pauseAllChannels()
	{
		for (Channel c:channels)
		{
			c.pauseTrack();
		}
	}
	public synchronized void playAllChannels(String except)
	{
		for (Channel c:channels)
		{
			if (!c.isAvailable() && !c.soundId.equals(except))
				c.playTrack();
		}
	}
	public synchronized void stopIdOnAllChannels(String id)
	{
		for (Channel c:channels)
		{
			if (c.soundId!=null && c.soundId.equals(id))
				c.stopTrack();
		}
	}
	public synchronized void pauseIdOnAllChannels(String id)
	{
		for (Channel c:channels)
		{
			if (!c.isAvailable() && c.soundId.equals(id))
				c.pauseTrack();
		}
	}
	
	public void playOnlyThis(String id)
	{
		pauseAllChannels();
		getAvailableChannel().setTrack(id,getPlayableTrack(id)).playTrack();
	}
	public void stopAndResumeOthers(String id)
	{
		stopIdOnAllChannels(id);
		playAllChannels(id);
	}
	
	public synchronized AudioTrack getPlayableTrack(String id) {
		try {
			int counter = 0;
			while (true)
			{
				AudioTrack track = hmTracks.get(id).get(counter);
				if (track.isPlaying())
				{
					counter++;
					if (counter==hmTracks.get(id).size())
					{
						return addTrack(id, hmTracksAndFiles.get(id));
						
					}
				} else
				{
					return track;
				}
			}			
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	
	public synchronized void setChannelNotPlayingYet(String id)
	{
		Channel c = getAvailableChannel();
		try {
			AudioTrack track = getPlayableTrack(id);
			if (track!=null)
			{
				c.setTrack(id, track);
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public synchronized void play(String id)
	{
		Channel c = getAvailableChannel();
		System.out.println("Playing "+id);
		try {
			AudioTrack track = getPlayableTrack(id);
			if (track==null) {
					return;
			}
			if (!track.getType().equals(TrackType.MUSIC)) {
				track.setVolume(1f);
			}
			c.setTrack(id, track);
			c.playTrack();
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(v, v);
			}
		} catch (NullPointerException npex)
		{
			npex.printStackTrace();
		}
	}
	
	public synchronized void pause(String id)
	{
		System.out.println("Pausing "+id);
		pauseIdOnAllChannels(id);
	}
	
	public synchronized  void stop(String id)
	{
		System.out.println("Stopping "+id);
		stopIdOnAllChannels(id);
	}

	public synchronized AudioTrack addTrack(String id, String file)
	{
		hmTracksAndFiles.put(id, file);
		AudioTrack mainTheme = null;
		try {
			mainTheme = AudioSystem.getSystem().createAudioTrack(new File(file).toURL(), true);
			mainTheme.setType(TrackType.POSITIONAL);
			mainTheme.setRelative(false);
			mainTheme.setLooping(false);
			mainTheme.setVolume(1f);
			mainTheme.setMinVolume(1f);
			if (hmTracks.get(id)==null)
			{
				ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
				tracks.add(mainTheme);
				hmTracks.put(id, tracks);	
			} else
			{
				hmTracks.get(id).add(mainTheme);
			}
			
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		return mainTheme;
	}

	public void run() {
		System.out.println("-- PREV PRIORITY: "+Thread.currentThread().getPriority());
		//Thread.currentThread().setPriority(10);
		while (true)
		{
			AudioSystem.getSystem().update();
			try{Thread.sleep(1);}catch (Exception ex){}
		}

	}

	
	
	
	
}
