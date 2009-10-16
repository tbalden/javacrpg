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

package org.jcrpg.world.ai.audio.desc;

import java.util.ArrayList;

import org.jcrpg.world.ai.AudioDescription;

/**
 * The list to be appended with the voice packages for characters.
 * @author illes
 *
 */
public class VoiceList {
	
	public static VoiceList male = new VoiceList();
	public static VoiceList female = new VoiceList();
	
	static
	{
		male.addVoice(new MaleNeutral1());
		male.addVoice(new MaleNeutral2());
		male.addVoice(new MaleNeutral3());
		male.addVoice(new MaleCheerful1());
		female.addVoice(new FemaleNeutral1());
	}
	
	public ArrayList<AudioDescription> list = new ArrayList<AudioDescription>();
	
	public VoiceList()
	{
	}
	public void addVoice(AudioDescription desc)
	{
		list.add(desc);
	}
	

}