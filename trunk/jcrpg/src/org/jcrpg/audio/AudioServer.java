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
import com.jmex.audio.event.TrackStateListener;

public class AudioServer implements Runnable, TrackStateListener {

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
	
	public static final String[] stepTypes = new String[] {
		STEP_SOIL, STEP_NO_WAY
	};
	
	public AudioServer()
	{
		try {
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File("./data/audio/music/warrior/warrior.ogg").toURL(), true);
			mainTheme.addTrackStateListener(this);
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
			mainTheme.addTrackStateListener(this);
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
				mainTheme.addTrackStateListener(this);
				mainTheme.setType(TrackType.POSITIONAL);
				mainTheme.setRelative(false);
				mainTheme.setLooping(false);
				mainTheme.setVolume(1f);
				ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
				tracks.add(mainTheme);
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
		pause("ingame");
		paused.add("ingame");
	}
	
	public void playOnlyThis(String id)
	{
		
		String[] playedA = played.toArray(new String[0]); 
		for (String p:playedA)
		{
			if (!p.equals(id))
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
			if (!p.equals(id))
				play(p);
		}
	}
	
	public synchronized AudioTrack getPlayableTrack(String id) {
		try {
			if (hmTracks.get(id).get(0).isPlaying())
			{
				System.out.println("############       ############# IS PLAYING TRACK!!!!"+ id+" "+hmTracks.get(id).size());
				if (hmTracksAndFiles.get(id)==null)
				{
					return null;
				}
				
				if (hmTracks.get(id).size()==1)
				{
					System.out.println("############################### DUPLICATING TRACK!!!!");
					addTrack(id, hmTracksAndFiles.get(id));
					return hmTracks.get(id).get(1);
				} else
				{
					if (!hmTracks.get(id).get(1).isPlaying())
					{
						System.out.println("############################### RET DUPLICATING TRACK!!!!");
						return hmTracks.get(id).get(1);
					}
					else 
						return null;
				}
			} else
			{
				return (hmTracks.get(id).get(0));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	public int playedAtOnce = 0;
	public int MAX_PLAYED = 4;
	
	public synchronized void play(String id)
	{
		System.out.println("######## PLAYED AT ONCE = "+playedAtOnce);
		if (MAX_PLAYED<=playedAtOnce) return;
		System.out.println("Playing "+id);
		try {
			if (getPlayableTrack(id)==null) {
				/*if (hmTracks.get(id).get(0).getCurrentTime()>0.2f)
				{
					hmTracks.get(id).get(0).stop();
				} else*/
				{
					return;
				}
			}
			AudioTrack track = getPlayableTrack(id);
			if (!track.getType().equals(TrackType.MUSIC)) {
				track.setVolume(1f);
			}
			track.play();
			playedAtOnce++;
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(v, v);
			}
			played.add(id);
		} catch (NullPointerException npex)
		{
			npex.printStackTrace();
		}
	}
	
	public synchronized  void pause(String id)
	{
		System.out.println("Pausing "+id);
		try {
			//hmTracks.get(id).fadeOut(0.7f);
			for (AudioTrack t:hmTracks.get(id)) {
				if (t.isPlaying())
				{
					t.pause();
					paused.add(id);
				}
			}
			played.remove(id);
		} catch (NullPointerException npex)
		{
			npex.printStackTrace();
		}
	}
	
	public synchronized  void stop(String id)
	{
		System.out.println("Stopping "+id);
		try {
			//hmTracks.get(id).fadeOut(0.7f);
			for (AudioTrack t:hmTracks.get(id)) {
				if (t.isPlaying())
				{
					t.stop();
				}
			}
			played.remove(id);
			paused.remove(id);
		} catch (NullPointerException npex)
		{
			npex.printStackTrace();
		}
	}

	public synchronized boolean addTrack(String id, String file)
	{
		hmTracksAndFiles.put(id, file);
		try {
			AudioTrack mainTheme = AudioSystem.getSystem().createAudioTrack(new File(file).toURL(), true);
			mainTheme.addTrackStateListener(this);
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
			return false;
		}
		return true;
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

	public void trackFinishedFade(AudioTrack arg0) {
		// TODO Auto-generated method stub
		System.out.println("FINISHED FADE");
		playedAtOnce--;
	}

	public void trackPaused(AudioTrack arg0) {
		// TODO Auto-generated method stub
		System.out.println("PAUSED");
		playedAtOnce--;
	}

	public void trackPlayed(AudioTrack arg0) {
		// TODO Auto-generated method stub
		System.out.println("PLAYED");
		//playedAtOnce--;
	}

	public void trackStopped(AudioTrack arg0) {
		// TODO Auto-generated method stub
		System.out.println("STOPPED");
		playedAtOnce--;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
