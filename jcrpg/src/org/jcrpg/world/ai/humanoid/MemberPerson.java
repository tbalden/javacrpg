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

package org.jcrpg.world.ai.humanoid;

import java.io.OutputStream;
import java.io.Reader;

import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.EntityMember;
import org.jcrpg.world.ai.abs.attribute.Attributes;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MemberPerson extends EntityMember {

	public static String UNDEFINED_VISIBLE_TYPEID = "--UNDEFINED--";
	
	public int possibleGenders = EntityDescription.GENDER_BOTH;
	public String pictureRoot = "human";
	
	public Attributes attributes = null;
	
	public MemberPerson()
	{
		super(UNDEFINED_VISIBLE_TYPEID, null); 
	}
	
	public MemberPerson(String visibleTypeId, AudioDescription audio) {
		super(visibleTypeId, audio);
	}
	public String id;
	public String foreName;
	public String sureName;
	public String pictureId;
	public String getForeName() {
		return foreName;
	}
	public void setForeName(String foreName) {
		this.foreName = foreName;
	}
	public String getSureName() {
		return sureName;
	}
	public void setSureName(String sureName) {
		this.sureName = sureName;
	}
	public String getPictureId() {
		return pictureId;
	}
	public void setPictureId(String pictureId) {
		this.pictureId = pictureId;
	}
	
	public String getPicturePath()
	{
		String genderPath = "";
		if (genderType==EntityDescription.GENDER_MALE) genderPath="male/";
		if (genderType==EntityDescription.GENDER_FEMALE) genderPath="female/";
		String path = "./data/portraits/"+pictureRoot+"/"+genderPath;
		return path;
	}
	
	public MemberPerson copy(MemberPerson copy)
	{
		if (copy==null)
		{
			copy = new MemberPerson(visibleTypeId,audioDescription);
		}
		copy.setForeName(foreName);
		copy.setPictureId(pictureId);
		copy.setSureName(sureName);
		copy.id = id;
		copy.genderType = genderType;
		copy.setAttributes(attributes);
		return copy;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}
	
	public void getXml(OutputStream output)
	{
		XStream xstream = new XStream(new DomDriver());
		xstream.toXML(this,output);
	}
	public static MemberPerson createFromXml(Reader xml)
	{
		XStream xstream = new XStream(new DomDriver());
		MemberPerson m = (MemberPerson)xstream.fromXML(xml);
		return m;
	}
	
}
