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

package org.jcrpg.util;

import java.io.File;
import java.util.Vector;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;

public abstract class XMLReader {

	public IXMLElement xml;
	public File xmlFile;
	boolean debug = true;
	public XMLReader(File xmlFile) throws Exception
	{
		this.xmlFile = xmlFile;
		IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
		IXMLReader reader = StdXMLReader.fileReader(xmlFile.getAbsolutePath());
		parser.setReader(reader);
		xml = (IXMLElement)parser.parse();
		for (Object o:xml.getChildren())
		{
			System.out.println("xml: "+((IXMLElement)o).getName());
		}
	}
	
	public String getContentOfNamedChild(IXMLElement xml, String name)
	{
		Vector<Object> v = xml.getChildrenNamed(name);
		if (v==null || v.size()==0) return null;
		return ((IXMLElement)v.get(0)).getContent();
	}
	
	
	
}
