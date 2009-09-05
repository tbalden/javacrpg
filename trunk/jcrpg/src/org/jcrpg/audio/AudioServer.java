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
import java.util.HashSet;
import java.util.logging.Level;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;

import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.event.TrackStateListener;

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
	
	public static String[] combatMusicList = new String[]{
		"combat0","combat1"
	                                                    };
	
	public ArrayList<Channel> channels = new ArrayList<Channel>();
	public ArrayList<Channel> musicChannels = new ArrayList<Channel>();
	public AudioServer()
	{
		if (J3DCore.SETTINGS.SOUND_ENABLED) {
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
		EFFECT_VOLUME_PERCENT_LAST = J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT;
		MUSIC_VOLUME_PERCENT_LAST = J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT;
		
		try {
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/fantasy/fantasy_menu.ogg").toURL(), true);
			mainTheme.setType(TrackType.MUSIC);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
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
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/struggle/combat.ogg").toURL(), true);
			mainTheme.setType(TrackType.MUSIC);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
			ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
			tracks.add(mainTheme);
			hmTracks.put("combat0", tracks);
			
		}catch (Exception ex)
		{
			if (Jcrpg.LOGGER.getLevel()!= Level.OFF) {
				ex.printStackTrace();
			}
		}
		try {
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/stones/tense.ogg").toURL(), true);
			mainTheme.setType(TrackType.MUSIC);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
			ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
			tracks.add(mainTheme);
			hmTracks.put("combat1", tracks);
			
		}catch (Exception ex)
		{
			if (Jcrpg.LOGGER.getLevel()!= Level.OFF) {
				ex.printStackTrace();
			}
		}

		try {
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/horizon/leveling.ogg").toURL(), true);
			mainTheme.setType(TrackType.MUSIC);
			mainTheme.setRelative(false);
			mainTheme.setLooping(true);
			mainTheme.setVolume(J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
			ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
			tracks.add(mainTheme);
			hmTracks.put("leveling", tracks);
			
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
				mainTheme.setType(TrackType.HEADSPACE);
				mainTheme.setRelative(false);
				mainTheme.setLooping(false);
				mainTheme.setVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
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
	public synchronized void playAllMusicChannels()
	{
		for (Channel c:musicChannels)
		{
			if (c.playing && c.paused)
				c.playTrack();
		}
	}

	public synchronized void stopAllMusicChannels()
	{
		for (Channel c:musicChannels)
		{
			c.stopTrack();
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
			if (!c.isAvailable()) if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer(c.soundId);
			if (!c.isAvailable() && c.soundId.equals(id))
			{
				//c.track.setLooping(true);
				//c.track.setVolume(volume);
				//System.out.println("setSoundPlayingVolume "+id+" "+volume+ " CV: "+c.track.getVolume()+" CR: "+c.track.getVolumeChangeRate());
				
				c.track.setTargetVolume(volume* J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
				
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("setSoundPlayingVolume SETTING VOLUME: "+c.soundId+" "+volume+" -- "+c.track.getVolume());
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
			if (J3DCore.SETTINGS.SOUND_ENABLED) ex.printStackTrace();

		}
	}
	
	
	String eventMusicId = null;

	/**
	 * Playing music for an event pausing other music channels.
	 * @param id
	 * @param loop
	 */
	public void playEventMusic(String id,boolean loop)
	{
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("###################### EVENT MUSIC: "+id);
		if (isMusicPlaying(id)) return;
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("NOT PLAYING, EVENT MUSIC: "+id);
		eventMusicId = id;
		pauseAllMusicChannels();
		try {
			AudioTrack t = getPlayableTrack(id);
			t.setLooping(loop);
			t.setTargetVolume(J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
			getAvailableMusicChannel().setTrack(id,t).playTrack();
		} catch (Exception ex)
		{
			if (J3DCore.SETTINGS.SOUND_ENABLED) ex.printStackTrace();

		}
	}
	
	public void stopEventMusicAndResumeOthers()
	{
		stopIdOnAllChannels(musicChannels,eventMusicId);
		playAllChannels(musicChannels,eventMusicId);
		eventMusicId = null;
	}
	

	public void stopAndResumeOthers(String id)
	{
		stopIdOnAllChannels(musicChannels,id);
		playAllChannels(musicChannels,id);
		playAllChannels(channels,id);
	}
	
	public synchronized AudioTrack getPlayableTrack(String id) {
		return getPlayableTrack(id,false);
	}
	public synchronized AudioTrack getPlayableTrack(String id, boolean continuous) {
		if (!J3DCore.SETTINGS.SOUND_ENABLED) return null;
		try {
			int counter = 0;
			while (true)
			{
				AudioTrack track = hmTracks.get(id).get(counter);
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("TRACK = "+ track+ " P "+track.isPlaying()+ " A "+track.isActive()+" S "+track.isStopped());
				if (track.isPlaying())
				{
					//if (continuous) 
					if (true) return track;
					counter++;
					if (counter==hmTracks.get(id).size())
					{
						if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("getPlayableTrack CREATING NEW ONE FOR "+id +" "+hmTracks.get(id).size());
						return addTrack(id, hmTracksAndFiles.get(id));
						
					}
				} else
				{
					return track;
				}
			}			
		} catch (Exception ex)
		{
			if (J3DCore.SETTINGS.SOUND_ENABLED) {
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
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("playForced Playing "+id);
		if (c==null)
		{
			if (!J3DCore.SETTINGS.SOUND_ENABLED) return;
			c = channels.get(0);
			c.stopTrack();
		}
		try {
			AudioTrack track = getPlayableTrack(id);
			if (track==null) {
					return;
			}
			if (!track.getType().equals(TrackType.MUSIC)) {
				track.setVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
			}
			c.setTrack(id, track);
			c.playTrack();
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(0.5f, v);
			}
		} catch (NullPointerException npex)
		{
			if (J3DCore.SETTINGS.SOUND_ENABLED) npex.printStackTrace();
		}
	}
	
	public synchronized void play(String id)
	{
		Channel c = getAvailableChannel();
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("Playing "+id);
		if (c!=null)
		try {
			AudioTrack track = getPlayableTrack(id);
			if (track.isPlaying()) 
				{//track.stop(); track.play(); 
				return;
				}
			if (track==null) {
				//System.out.println("NEW TRACK");

					//track = addTrack(id, hmTracksAndFiles.get(id));
					return;
			}
			if (!track.getType().equals(TrackType.MUSIC)) {
				track.setVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
			}
			c.setTrack(id, track);
			track.getPlayer().setStartTime(System.currentTimeMillis());
			c.playTrack();
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(0.5f, v);
			} else
			{
				track.setTargetVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
			}
		} catch (NullPointerException npex)
		{
			if (J3DCore.SETTINGS.SOUND_ENABLED) npex.printStackTrace();
		}
	}

	public synchronized void playLoading(String id, String type)
	{
		if (!J3DCore.SETTINGS.SOUND_ENABLED) return;
		if (id==null) return;
		Channel c = getAvailableChannel();
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("Playing "+id);
		if (c!=null)
		try {
			AudioTrack track = getPlayableTrack(id);
			if (track==null) {
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("NEW TRACK");

				track = addTrack(id, "./data/audio/sound/"+type+"/"+id+".ogg");
			}
			if (!track.getType().equals(TrackType.MUSIC)) {
				track.setVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
			}
			c.setTrack(id, track);
			c.playTrack();
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(0.5f, v);
			} else
			{
				track.setTargetVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
			}
		} catch (NullPointerException npex)
		{
			if (J3DCore.SETTINGS.SOUND_ENABLED) npex.printStackTrace();
		}
	}

	public synchronized void playLoadingAndPauseMusic(String id, String type)
	{
		if (!J3DCore.SETTINGS.SOUND_ENABLED) return;
		if (id==null) return;
		Channel c = getAvailableChannel();
		c.unpauseMusicNeeded = true;
		ArrayList<Channel> pausedChannels = new ArrayList<Channel>();
		for (Channel musicChannel:musicChannels)
		{
			if (musicChannel.playing)
			{
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("## PAUSING: "+musicChannel.soundId);
				pausedChannels.add(musicChannel);
			}
		}
		c.pausedChannels = pausedChannels;
		pauseAllMusicChannels();
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("Playing "+id);
		if (c!=null)
		try {
			AudioTrack track = getPlayableTrack(id);
			if (track==null) {
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("NEW TRACK");

				track = addTrack(id, "./data/audio/sound/"+type+"/"+id+".ogg");
			}
			if (!track.getType().equals(TrackType.MUSIC)) {
				track.setVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
			}
			c.setTrack(id, track);
			c.playTrack();
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(0.5f, v);
			} else
			{
				track.setTargetVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
			}
		} catch (NullPointerException npex)
		{
			if (J3DCore.SETTINGS.SOUND_ENABLED) npex.printStackTrace();
		}
	}

	
	public synchronized void stopContinuous(String id, String type)
	{
		
	}

	public synchronized void playContinuousLoading(String id, String type, float volume)
	{
		if (!J3DCore.SETTINGS.SOUND_ENABLED) return;
		if (id==null) return;
		if (setSoundPlayingVolume(id,volume* J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f)) {
			return;
		}
		Channel c = getAvailableChannel();
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("playContinuousLoading Playing "+id);		
		if (c!=null)
		try {
			AudioTrack track = null;
			
			ArrayList<AudioTrack> tracks = hmTracks.get(id);
			if (tracks==null)
			{
				track = addTrackStreaming(id, "./data/audio/sound/"+type+"/"+id+".ogg");
				track.setLooping(true);
				if (!track.getType().equals(TrackType.MUSIC)) {
					track.setVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
				}				
			} else
			{
				track = tracks.get(0);
			}
			
			if (track.isPlaying()) {
				track.setTargetVolume(volume * J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
				return;
			} else
			{
				c.setTrack(id, track);
				//c.setLooping();
				c.playTrack();
				track.setTargetVolume(volume * J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
			}
		} catch (NullPointerException npex)
		{
			if (J3DCore.SETTINGS.SOUND_ENABLED) npex.printStackTrace();
		}
	}
	
	
	public synchronized void pause(String id)
	{
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("Pausing "+id);
		pauseIdOnAllChannels(id);
	}
	
	public synchronized  void stop(String id)
	{
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("Stopping "+id);
		stopIdOnAllChannels(channels,id);
	}
	public synchronized  void fadeOut(String id)
	{
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("Fadingout "+id);
		fadeOutIdOnAllChannels(channels,id);
	}

	public synchronized AudioTrack addTrack(String id, String file)
	{
		if (!J3DCore.SETTINGS.SOUND_ENABLED) return null;
		hmTracksAndFiles.put(id, file);
		AudioTrack mainTheme = null;
		try {
			//AudioSystem.getSystem().cleanup();
			mainTheme = AudioSystem.getSystem().createAudioTrack(new File(file).toURL(), false);
			mainTheme.setType(TrackType.HEADSPACE);
			mainTheme.setRelative(false);
			mainTheme.setLooping(false);
			mainTheme.setVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
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
			if (J3DCore.SETTINGS.SOUND_ENABLED) ex.printStackTrace();
			return null;
		}
		return mainTheme;
	}

	public synchronized AudioTrack addTrackStreaming(String id, String file)
	{
		if (!J3DCore.SETTINGS.SOUND_ENABLED) return null;
		hmTracksAndFiles.put(id, file);
		AudioTrack mainTheme = null;
		try {
			mainTheme = AudioSystem.getSystem().createAudioTrack(new File(file).toURL(), true);
			mainTheme.setType(TrackType.HEADSPACE);
			mainTheme.setRelative(false);
			mainTheme.setLooping(false);
			mainTheme.setVolume(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
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
			if (J3DCore.SETTINGS.SOUND_ENABLED) ex.printStackTrace();
			return null;
		}
		return mainTheme;
	}
	
	boolean needForBackgroundMusicUpdate = false;
	
	public void run() {
		if (!J3DCore.SETTINGS.SOUND_ENABLED) return;
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("-- PREV PRIORITY: "+Thread.currentThread().getPriority());
		Thread.currentThread().setPriority(10);
		while (true)
		{
			//AudioSystem.getSystem().update();
			try{Thread.sleep(60);}catch (Exception ex){}
		}
	}
	
	public int EFFECT_VOLUME_PERCENT_LAST;
	public int MUSIC_VOLUME_PERCENT_LAST;
	
	public void applyVolumeSettings()
	{
		for (Channel c:channels)
		{
			
			AudioTrack t = c.track;
			if (t!=null)
			{
				if (t.getType()== TrackType.ENVIRONMENT)
				{
					float newLevel = (J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
					try {
						float relativeLevel = t.getVolume() / (EFFECT_VOLUME_PERCENT_LAST/100f);
						if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("C: "+t.getVolume());
						newLevel = relativeLevel * (J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
					} catch (Exception ex) {}
					if (Float.isNaN(newLevel)) newLevel =(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
					if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("NEWL: "+newLevel);
					if (!t.isPlaying())
					{
						t.setVolume(newLevel);
					} else
					{
						t.setVolumeChangeRate(1f);
						t.setTargetVolume(newLevel);
					}
				}
			}
		}
		for (ArrayList<AudioTrack> ts : hmTracks.values())
		{
			for (AudioTrack t:ts)
			{
				boolean found = false;
				for (Channel c: channels)
				{
					if (c.track == t) found = true;
				}
				for (Channel c: musicChannels)
				{
					if (c.track == t) found = true;
				}
				if (!found)
				{
					if (t.getType()== TrackType.ENVIRONMENT)
					{
						float newLevel = (J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
						try {
							float relativeLevel = t.getVolume() / (EFFECT_VOLUME_PERCENT_LAST/100f);
							if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("C: "+t.getVolume());
							newLevel = relativeLevel * (J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
						} catch (Exception ex) {}
						if (Float.isNaN(newLevel)) newLevel =(J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT/100f);
						if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("NEWL: "+newLevel);
						if (!t.isPlaying())
						{
							t.setVolume(newLevel);
						} else
						{
							t.setVolumeChangeRate(1f);
							t.setTargetVolume(newLevel);
						}
					}
					if (t.getType()== TrackType.MUSIC)
					{
						float newLevel = (J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
						try {
							float relativeLevel = t.getVolume() / (MUSIC_VOLUME_PERCENT_LAST/100f);
							if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("C: "+t.getVolume());
							newLevel = relativeLevel * (J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
						} catch (Exception ex) {}
						if (Float.isNaN(newLevel)) newLevel =(J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
						if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("NEWL: "+newLevel);
						if (!t.isPlaying())
						{
							t.setVolume(newLevel);
						} else
						{
							t.setVolumeChangeRate(1f);
							t.setTargetVolume(newLevel);
						}
					}
				}
			}
		}
		for (Channel c:musicChannels)
		{
			AudioTrack t = c.track;
			if (t!=null)
			{
				if (t.getType()== TrackType.MUSIC)
				{
					float newLevel = (J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
					try {
						float relativeLevel = t.getVolume() / (MUSIC_VOLUME_PERCENT_LAST/100f);
						if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("C: "+t.getVolume());
						newLevel = relativeLevel * (J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
					} catch (Exception ex) {}
					if (Float.isNaN(newLevel)) newLevel =(J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
					if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("NEWL: "+newLevel);
					if (!t.isPlaying())
					{
						t.setVolume(newLevel);
					} else
					{
						t.setVolumeChangeRate(1f);
						t.setTargetVolume(newLevel);
					}
				}
			}
		}
		EFFECT_VOLUME_PERCENT_LAST = J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT;
		MUSIC_VOLUME_PERCENT_LAST = J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT;
	}
	
	public class BackgroundMusicStateListener implements TrackStateListener
	{
		boolean gotStopped = false;
		AudioServer server;
		public BackgroundMusicStateListener(AudioServer server) {
			this.server = server;
		}

		public void trackFinishedFade(AudioTrack arg0) {
		}

		public void trackPaused(AudioTrack arg0) {
		}

		public void trackPlayed(AudioTrack arg0) {
			gotStopped = false;
		}

		public void trackStopped(AudioTrack arg0) {
			if (!gotStopped)
			{
				if (!stoppingBackgroundMusic)
				{
					server.initialBackgroundMusic(false);
				}
				gotStopped = true;
			}
		}
	}
	
	
	
	HashSet<String> currentlyPlayingBackgroundMusic = new HashSet<String>();
	public String lastPlayed = null;
	public synchronized void initialBackgroundMusic()
	{
		initialBackgroundMusic(true);
	}
	
	boolean stoppingBackgroundMusic = false;
	public synchronized void initialBackgroundMusic(boolean stop)
	{
		/*try {
			throw new Exception();
		} catch (Exception ee)
		{
			ee.printStackTrace();
			System.out.println("STOP = "+stop);
		}*/
		String music = J3DCore.getInstance().gameState.getUpdatedBackgroundMusic();
		if (lastPlayed!=null && music.equals(lastPlayed))
		{
			return;
		}
		lastPlayed = music;
		if (currentlyPlayingBackgroundMusic!=null) 
		{
			if (stop)
			{
				stoppingBackgroundMusic = true;
				for (String lastPlayedBackgroundMusic:currentlyPlayingBackgroundMusic)
				{
					fadeOutIdOnAllChannels(musicChannels, lastPlayedBackgroundMusic);
				}
				stoppingBackgroundMusic = false;
			}
			//currentlyPlayingBackgroundMusic.clear();
		}
		if (J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT==0) return;
		currentlyPlayingBackgroundMusic.add(music);
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("------------ BACKGROUND MUSIC ----------");
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("############ "+music);
		Channel c = getAvailableMusicChannel();
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("-#_#_#_#_#"+c);
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.info("playContinuousLoading Playing "+music);		
		if (c!=null)
		try {
			AudioTrack track = getPlayableTrack(music);
			if (track==null) {
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("LOADING BRAND NEW BG MUSIC TRACK");
				track = addTrackStreaming(music, music);
				track.addTrackStateListener(new BackgroundMusicStateListener(this));
				
				//if (!track.getType().equals(TrackType.MUSIC)) {
					track.setVolume(J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
				//}
			} else
			{
				track.setTargetVolume(J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT/100f);
			}
			
			track.setLooping(false);
			c.setTrack(music, track);
			//c.playTrack();
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("TIME: "+c.track.getCurrentTime());
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("VOLU: "+c.track.getVolume());
			c.track.getPlayer().setStartTime(System.currentTimeMillis());
			c.track.setEnabled(true);
			
			//c.track.getPlayer().setMaxAudibleDistance(maxDistance)
			c.track.play();
			c.track.getPlayer().updateTrackPlacement();
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("$$$$$$$$$$ PLAYING...");
		} catch (NullPointerException npex)
		{
			if (J3DCore.SETTINGS.SOUND_ENABLED) npex.printStackTrace();	
		} catch (Exception npex)
		{
			npex.printStackTrace();
		
		}
	}
	
}
