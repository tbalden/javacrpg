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

package org.jcrpg.game.scenario;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import net.n3.nanoxml.IXMLElement;

import org.jcrpg.game.scenario.element.StoryPart;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.XMLReader;

public class Events extends XMLReader{

	Vector<IXMLElement> eventsXML = new Vector<IXMLElement>();
	
	public ArrayList<Event> events = new ArrayList<Event>();
	
	public Event intro;
	
	public String rootDirPath;
	
	public Scenario scenario;
	
	@SuppressWarnings("unchecked")
	public Events(Scenario scenario, File file, String rootDirPath) throws Exception
	{
		super(file);
		this.scenario = scenario;
		this.rootDirPath = rootDirPath;
		eventsXML = (Vector<IXMLElement>)xml.getChildrenNamed("event");
		for (IXMLElement e:eventsXML)
		{
			Event event = new Event(this,e);
			events.add(event);
		}
	}
	public class Event
	{
		public String type, name;
		public ArrayList<Element> elements = new ArrayList<Element>();
		
		public class Element {
			public Event event;
			public String type;
			public String name;
			public String file;
			public StoryPart storyPart = null; // optional instance..
			public Element(Event event, IXMLElement xml) throws Exception 
			{
				this.event = event;
				type = getContentOfNamedChild(xml, "type");
				name = getContentOfNamedChild(xml, "name");
				file = getContentOfNamedChild(xml, "file");
				if (type.equals("story"))
				{
					storyPart = new StoryPart(this,new File(rootDirPath+File.separator+file));
					System.out.println(name+ " STORY PART size"+storyPart.blocks.size());
				}
			}
			public void play()
			{
				if (storyPart!=null)
				{
					storyPart.play();
				}
			}
			
			public void finishedPlaying(StoryPart storyPart)
			{
				event.finishedPlaying();
			}
		}
		
		public Events events;
		
		@SuppressWarnings("unchecked")
		public Event(Events events, IXMLElement xml) throws Exception 
		{
			this.events = events;
			type = getContentOfNamedChild(xml, "type");
			name = getContentOfNamedChild(xml, "name");
			if (type.equals("intro"))
			{
				intro = this;
			}
			Vector<IXMLElement> elementsXML = (Vector<IXMLElement>)xml.getChildrenNamed("element");
			for (IXMLElement e:elementsXML)
			{
				Element element = new Element(this,e);
				elements.add(element);
			}
		}
		
		
		public int playedElement = 0;
		
		public void play()
		{
			playedElement = 0;
			playElement(playedElement);
		}
		private void playElement(int element)
		{
			elements.get(element).play();
		}
		
		public void finishedPlaying()
		{
			if (playedElement==elements.size()-1)
			{
				scenario.finishedEvent(this);
			} else
			{
				playedElement++;
				playElement(playedElement);
			}
		}
	}
	
}
