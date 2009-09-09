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

package org.jcrpg.game.scenario.element;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import net.n3.nanoxml.IXMLElement;

import org.jcrpg.game.scenario.Events.Event.Element;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.XMLReader;

/**
 * The story element with a larger portion of text (blocks) with optional image shown (no interacion). Like an intro, a longer story part etc.
 * A special UI element (StoryPartWindow) is bound to show it. 
 * @author illes
 *
 */
public class StoryPart extends XMLReader {

	public ArrayList<Block> blocks = new ArrayList<Block>();
	
	public class Block
	{
		public String text;
		public String imageFileName;
		public Block(IXMLElement xml)
		{
			text = getContentOfNamedChild(xml, "text");
			imageFileName = getContentOfNamedChild(xml, "image");
		}
	}
	
	public Element element;
	public StoryPart(Element element, File xmlFile) throws Exception {
		super(xmlFile);
		this.element = element;
		Vector<IXMLElement> blocksXML = (Vector<IXMLElement>)xml.getChildrenNamed("block");
		for (IXMLElement e:blocksXML)
		{
			Block block = new Block(e);
			blocks.add(block);
		}

	}
	
	public void finishedPlaying()
	{
		element.finishedPlaying(this);
	}
	
	public void play()
	{
		J3DCore.getInstance().storyPartDispWindow.playStoryPart(this);
	}

}
