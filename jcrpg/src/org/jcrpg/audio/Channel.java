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

import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.event.TrackStateListener;

public class Channel implements TrackStateListener{

	
	public boolean playing;
	public boolean paused;
	public AudioTrack track;
	public String soundId;
	
	AudioServer server;
	public Channel(AudioServer server)
	{
		this.server = server;		
	}
	
	public synchronized Channel setTrack(String soundId, AudioTrack track)
	{
		this.track = track;
		this.soundId = soundId;
		track.addTrackStateListener(this);
		return this;
	}
	
	public synchronized void pauseTrack()
	{
		if (track!=null && track.isPlaying())
			track.pause();
	}
	public synchronized void stopTrack()
	{
		System.out.println("STOP TRACK : "+soundId);
		try {
			track.stop();
			playing = false;
			paused = false;
			track = null;
			soundId = null;
		} catch (Exception ex)
		{
			
		}
	}
	public synchronized void playTrack()
	{
		System.out.println("PLAY TRACK : "+soundId);
		if (track!=null && !track.isPlaying()) {
			track.play();
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(v, v);
			}
		}
	}
	
	public synchronized boolean isAvailable()
	{
		return track==null && !playing;
	}
	
	public synchronized void trackFinishedFade(AudioTrack arg0) {
		System.out.println("##### FINISHED TRACK : "+soundId);
		//playing = false;
		//paused = false;
		//track = null;
		//soundId = null;
	}

	public synchronized void trackPaused(AudioTrack arg0) {
		paused = true;
	}

	public synchronized void trackPlayed(AudioTrack arg0) {
		playing = true;
		paused = false;
	}

	public synchronized void trackStopped(AudioTrack arg0) {
		System.out.println("##### STOPPED TRACK : "+soundId);
		track.removeTrackStateListener(this);
		if (this.track!=null && this.track.equals(track)) {
			track = null;
			soundId = null;
			paused = false;
			playing = false;
		}
	}

	
	
}
