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

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;

import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.event.TrackStateListener;

public class Channel implements TrackStateListener{

	
	public boolean playing;
	public boolean paused;
	public AudioTrack track;
	public String soundId;
	public String channelId = "";
	AudioServer server;
	public Channel(AudioServer server, String id)
	{
		this.server = server;
		this.channelId = id;
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
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("STOP TRACK : "+soundId);
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
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info(channelId+" PLAY TRACK : "+soundId);
		if (track!=null && !track.isPlaying()) {
			track.play();
			if (track!=null)
			if (track.getType().equals(TrackType.MUSIC)) {
				float v = track.getVolume();
				track.fadeIn(0.5f, v);
			}
		}
	}

	public synchronized void fadeTrack(float volume)
	{
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info(channelId+" FADE TRACK : "+soundId);
		if (track!=null && track.isPlaying()) {
			//track.play();
			if (track!=null)
				track.fadeIn(0.5f, volume);
		}
	}
	public synchronized void fadeOutTrack()
	{
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info(channelId+" FADE OUT TRACK : "+soundId);
		if (track!=null && track.isPlaying()) {
			//track.play();
			if (track!=null)
				track.fadeOut(0.5f);
		}
	}
	
	public synchronized boolean isAvailable()
	{
		return track==null && !playing;
	}
	
	public synchronized void trackFinishedFade(AudioTrack arg0) {
		Jcrpg.LOGGER.info(channelId+" ##### FINISHED FADE TRACK : "+soundId+ " "+arg0.getTargetVolume());
		if (arg0.getTargetVolume()==0f)
		{
			arg0.stop();
		} else
		{
			arg0.setVolume(arg0.getTargetVolume());
		}
		//playing = false;
		//paused = false;
		//track = null;
		//soundId = null;
	}

	public synchronized void trackPaused(AudioTrack arg0) {
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info(channelId+" ##### PAUSED TRACK : "+soundId);
		paused = true;
	}

	public synchronized void trackPlayed(AudioTrack arg0) {
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info(channelId+" ##### PLAYED TRACK : "+soundId);
		if (!arg0.isStreaming() && !arg0.isLooping()) // Workaround for trackStopped not called when non-streaming audiotrack
			trackStopped(arg0); else 
		{
			playing = true;
			paused = false;
		}
	}

	public synchronized void trackStopped(AudioTrack arg0) {
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info(channelId+" ##### STOPPED TRACK : "+soundId);
		track.removeTrackStateListener(this);
		if (this.track!=null && this.track.equals(track)) {
			track = null;
			soundId = null;
			paused = false;
			playing = false;
		}
	}
	
	
	
}
