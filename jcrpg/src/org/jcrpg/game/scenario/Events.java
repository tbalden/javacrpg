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
import org.jcrpg.util.XMLReader;

public class Events extends XMLReader{

	Vector<IXMLElement> eventsXML = new Vector<IXMLElement>();
	
	public ArrayList<Event> events = new ArrayList<Event>();
	
	public Event intro;
	
	public String rootDirPath;
	
	@SuppressWarnings("unchecked")
	public Events(File file, String rootDirPath) throws Exception
	{
		super(file);
		this.rootDirPath = rootDirPath;
		eventsXML = (Vector<IXMLElement>)xml.getChildrenNamed("event");
		for (IXMLElement e:eventsXML)
		{
			Event event = new Event(e);
			events.add(event);
		}
	}
	public class Event
	{
		public String type, name;
		public ArrayList<Element> elements = new ArrayList<Element>();
		
		public class Element {
			public String type;
			public String name;
			public String file;
			public StoryPart storyPart = null; // optional instance..
			public Element(IXMLElement xml) throws Exception 
			{
				type = getContentOfNamedChild(xml, "type");
				name = getContentOfNamedChild(xml, "name");
				file = getContentOfNamedChild(xml, "file");
				if (type.equals("story"))
				{
					storyPart = new StoryPart(new File(rootDirPath+File.separator+file));
					System.out.println(name+ " STORY PART size"+storyPart.blocks.size());
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		public Event(IXMLElement xml) throws Exception 
		{
			type = getContentOfNamedChild(xml, "type");
			name = getContentOfNamedChild(xml, "name");
			if (type.equals("intro"))
			{
				intro = this;
			}
			Vector<IXMLElement> elementsXML = (Vector<IXMLElement>)xml.getChildrenNamed("element");
			for (IXMLElement e:elementsXML)
			{
				Element element = new Element(e);
				elements.add(element);
			}

		}
	}
	
}
