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

package org.jcrpg.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;

import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;

public class AudioServer implements Runnable {

	HashMap<String, ArrayList<AudioTrack>> hmTracks = new HashMap<String, ArrayList<AudioTrack>>();
	HashMap<String, String> hmTracksAndFiles = new HashMap<String, String>();
	
	
	public static final String STEP_ROCK = "steps_rock";
	public static final String STEP_SOFT = "steps_soft";
	public static final String STEP_SOIL = "steps_soil";
	public static final String STEP_STONE = "steps_stone";
	public static final String STEP_SNOW = "steps_snow";
	public static final String STEP_WATER = "steps_water";
	public static final String STEP_FLY = "steps_fly";
	public static final String STEP_SWIM = "steps_swim";
	public static final String STEP_NO_WAY= "noway";
	
	public static final String EVENT_ENC1 = "enc1";
	
	public int playedAtOnce = 0;
	public int MAX_PLAYED = 7;
	public int MAX_MUSIC = 2;
	
	public static final String[] stepTypes = new String[] {
		STEP_SOIL, STEP_STONE, STEP_NO_WAY, STEP_SNOW
	};
	
	
	public ArrayList<Channel> channels = new ArrayList<Channel>();
	public ArrayList<Channel> musicChannels = new ArrayList<Channel>();
	public AudioServer()
	{
		if (J3DCore.SOUND_ENABLED) {
			for (int i=0; i<MAX_PLAYED; i++)
			{
				channels.add(new Channel(this,"NORM "+i));
			}
			for (int i=0; i<MAX_MUSIC; i++) 
			{
				musicChannels.add(new Channel(this,"MUSIC "+i));
			}
		} else
		{
			return;
		}
		
		
		try {
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/fantasy/fantasy_menu.ogg").toURL(), true);
			mainTheme.setType(TrackType.MUSIC);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(J3DCore.MUSIC_VOLUME_PERCENT/100f);
			ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
			tracks.add(mainTheme);
			hmTracks.put("main", tracks);
		}catch (Exception ex)
		{
			if (Jcrpg.LOGGER.getLevel()!= Level.OFF) {
				ex.printStackTrace();
			}
		}
		try {	
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/oak/oak.ogg").toURL(), true);
			mainTheme.setType(TrackType.MUSIC);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(J3DCore.MUSIC_VOLUME_PERCENT/100f);
			ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
			tracks.add(mainTheme);
			//hmTracks.put("ingame", tracks);
			
		}catch (Exception ex)
		{
			if (Jcrpg.LOGGER.getLevel()!= Level.OFF) {
				ex.printStackTrace();
			}
		}
		for (String step:stepTypes)
		{
			try {
				AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/sound/steps/"+step+".ogg").toURL(), false);
				mainTheme.setType(TrackType.ENVIRONMENT);
				mainTheme.setRelative(false);
				mainTheme.setLooping(false);
				mainTheme.setVolume(J3DCore.EFFECT_VOLUME_PERCENT/100f);
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
		setMusicChannelNotPlayingYet("ingame");
	}
	
	
	public synchronized Channel getAvailableChannel()
	{
		for (Channel c:channels)
		{
			if (c.isAvailable()) return c;
		}
		return null;
	}
	
	public synchronized Channel getAvailableMusicChannel()
	{
		for (Channel c:musicChannels)
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
	public synchronized void pauseAllMusicChannels()
	{
		for (Channel c:musicChannels)
		{
			c.pauseTrack();
		}
	}
	public synchronized void playAllChannels(ArrayList<Channel> channels,String except)
	{
		for (Channel c:channels)
		{
			if (!c.isAvailable() && !c.soundId.equals(except))
				c.playTrack();
		}
	}
	public synchronized void stopIdOnAllChannels(ArrayList<Channel> channels, String id)
	{
		for (Channel c:channels)
		{
			if (c.soundId!=null && c.soundId.equals(id))
				c.stopTrack();
		}
	}
	public synchronized void fadeOutIdOnAllChannels(ArrayList<Channel> channels, String id)
	{
		for (Channel c:channels)
		{
			if (c.soundId!=null && c.soundId.equals(id))
				c.fadeOutTrack();
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
	
	
	public boolean isMusicPlaying(String id)
	{
		for (Channel c:musicChannels)
		{
			if (!c.isAvailable() && c.soundId.equals(id))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isSoundPlaying(String id)
	{
		for (Channel c:channels)
		{
			if (!c.isAvailable() && c.soundId.equals(id))
			{
				return true;
			}
		}
		return false;
	}
	public boolean setSoundPlayingVolume(String id,float volume)
	{
		boolean b = false;
		for (Channel c:channels)
		{
			if (!c.isAvailable() && c.soundId.equals(id))
			{
				//c.track.setLooping(true);
				c.track.setVolume(volume);
				c.track.setTargetVolume(volume);
				if (J3DCore.LOGGING) Jcrpg.LOGGER.info("setSoundPlayingVolume SETTING VOLUME: "+c.soundId+" "+volume+" -- "+c.track.getVolume());
				b = true;
			}
		}
		return b;
	}
	
	public void playOnlyThisMusic(String id)
	{
		if (isMusicPlaying(id)) return;
		pauseAllChannels();
		pauseAllMusicChannels();
		try {
			getAvailableMusicChannel().setTrack(id,getPlayableTrack(id)).playTrack();
		} catch (Exception ex)
		{
			if (J3DCore.SOUND_ENABLED) ex.printStackTrace();

		}
	}
	public void stopAndResumeOthers(String id)
	{
		stopIdOnAllChannels(musicChannels,id);
		playAllChannels(musicChannels,id);
		playAllChannels(channels,id);
	}
	
	public synchronized AudioTrack getPlayableTrack(String id) {
		if (!J3DCore.SOUND_ENABLED) return null;
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
						if (J3DCore.LOGGING) Jcrpg.LOGGER.info("getPlayableTrack CREATING NEW ONE FOR "+id +" "+hmTracks.get(id).size());
						return addTrack(id, hmTracksAndFiles.get(id));
						
					}
				} else
				{
					return track;
				}
			}			
		} catch (Exception ex)
		{
			if (J3DCore.SOUND_ENABLED) {
				if (Jcrpg.LOGGER.getLevel()!= Level.OFF) {
					Jcrpg.LOGGER.finest(ex.toString());
				}
			}
			return null;
		}
	}
	
	
	public synchronized void setMusicChannelNotPlayingYet(String id)
	{
		Channel c = getAvailableMusicChannel();
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
	
	public synchronized void playForced(String id)
	{
		Channel c = getAvailableChannel();
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("playForced Playing "+id);
		if (c==null)
		{
			if (!J3DCore.SOUND_ENABLED) return;
			c = channels.get(0);
			c.stopTrack();
		}
		try {
			AudioTrack track = getPlayableTrack(id);
			if (track==null) {
					return;
			}
			if (!track.getType().equals(TrackType.MUSIC)) {
				track.setVolume(J3DCore.EFFECT_VOLUME_PERCENT/100f);
			}
			c.setTrack(id, track);
			c.playTrack();
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(0.5f, v);
			}
		} catch (NullPointerException npex)
		{
			if (J3DCore.SOUND_ENABLED) npex.printStackTrace();
		}
	}
	
	public synchronized void play(String id)
	{
		Channel c = getAvailableChannel();
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("Playing "+id);
		if (c!=null)
		try {
			AudioTrack track = getPlayableTrack(id);
			if (track==null) {
					return;
			}
			if (!track.getType().equals(TrackType.MUSIC)) {
				track.setVolume(J3DCore.EFFECT_VOLUME_PERCENT/100f);
			}
			c.setTrack(id, track);
			c.playTrack();
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(0.5f, v);
			}
		} catch (NullPointerException npex)
		{
			if (J3DCore.SOUND_ENABLED) npex.printStackTrace();
		}
	}

	public synchronized void playLoading(String id, String type)
	{
		if (!J3DCore.SOUND_ENABLED) return;
		if (id==null) return;
		Channel c = getAvailableChannel();
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("Playing "+id);
		if (c!=null)
		try {
			AudioTrack track = getPlayableTrack(id);
			if (track==null) {
				track = addTrack(id, "./data/audio/sound/"+type+"/"+id+".ogg");
			}
			if (!track.getType().equals(TrackType.MUSIC)) {
				track.setVolume(J3DCore.EFFECT_VOLUME_PERCENT/100f);
			}
			c.setTrack(id, track);
			c.playTrack();
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(v, v);
			}
		} catch (NullPointerException npex)
		{
			if (J3DCore.SOUND_ENABLED) npex.printStackTrace();
		}
	}
	
	public synchronized void stopContinuous(String id, String type)
	{
		
	}

	public synchronized void playContinuousLoading(String id, String type, float volume)
	{
		if (!J3DCore.SOUND_ENABLED) return;
		if (id==null) return;
		if (setSoundPlayingVolume(id,volume* J3DCore.EFFECT_VOLUME_PERCENT/100f)) {
			return;
		}
		Channel c = getAvailableChannel();
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("playContinuousLoading Playing "+id);		
		if (c!=null)
		try {
			AudioTrack track = getPlayableTrack(id);
			if (track==null) {
				track = addTrack(id, "./data/audio/sound/"+type+"/"+id+".ogg");
			}
			track.setLooping(true);
			if (!track.getType().equals(TrackType.MUSIC)) {
				track.setVolume(J3DCore.EFFECT_VOLUME_PERCENT/100f);
			}
			c.setTrack(id, track);
			track.setTargetVolume(volume * J3DCore.EFFECT_VOLUME_PERCENT/100f);
			track.setVolume(volume * J3DCore.EFFECT_VOLUME_PERCENT/100f);
			c.playTrack();
			//float v = track.getVolume();
			track.setVolume(volume * J3DCore.EFFECT_VOLUME_PERCENT/100f);
			//track.fadeIn(0.2f, volume);
		} catch (NullPointerException npex)
		{
			if (J3DCore.SOUND_ENABLED) npex.printStackTrace();
		}
	}
	
	
	public synchronized void pause(String id)
	{
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("Pausing "+id);
		pauseIdOnAllChannels(id);
	}
	
	public synchronized  void stop(String id)
	{
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("Stopping "+id);
		stopIdOnAllChannels(channels,id);
	}
	public synchronized  void fadeOut(String id)
	{
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("Fadingout "+id);
		fadeOutIdOnAllChannels(channels,id);
	}

	public synchronized AudioTrack addTrack(String id, String file)
	{
		if (!J3DCore.SOUND_ENABLED) return null;
		hmTracksAndFiles.put(id, file);
		AudioTrack mainTheme = null;
		try {
			mainTheme = AudioSystem.getSystem().createAudioTrack(new File(file).toURL(), false);
			mainTheme.setType(TrackType.ENVIRONMENT);
			mainTheme.setRelative(false);
			mainTheme.setLooping(false);
			mainTheme.setVolume(J3DCore.EFFECT_VOLUME_PERCENT/100f);
			mainTheme.setMinVolume(J3DCore.EFFECT_VOLUME_PERCENT/100f);
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
			if (J3DCore.SOUND_ENABLED) ex.printStackTrace();
			return null;
		}
		return mainTheme;
	}

	public void run() {
		if (!J3DCore.SOUND_ENABLED) return;
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("-- PREV PRIORITY: "+Thread.currentThread().getPriority());
		Thread.currentThread().setPriority(10);
		while (true)
		{
			AudioSystem.getSystem().update();
			try{Thread.sleep(60);}catch (Exception ex){}
		}
	}
	
}
