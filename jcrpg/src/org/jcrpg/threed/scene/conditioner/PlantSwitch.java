/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.threed.scene.conditioner;

import org.jcrpg.threed.scene.side.RenderedSide;

public class PlantSwitch {

	
	RenderedSide defaultSide;
	
	RenderedSide blooming;
	RenderedSide fruitful;
	RenderedSide passing;
	RenderedSide frozen;
	RenderedSide ill;
	
	public PlantSwitch(RenderedSide defaultSide, RenderedSide blooming, RenderedSide fruitful, RenderedSide passing, RenderedSide frozen, RenderedSide ill) {
		super();
		this.defaultSide = defaultSide;
		this.blooming = blooming;
		this.fruitful = fruitful;
		this.passing = passing;
		this.frozen = frozen;
		this.ill = ill;
	}
	
	public RenderedSide getBlooming() {
		return blooming==null?defaultSide:blooming;
	}
	public void setBlooming(RenderedSide blooming) {
		this.blooming = blooming;
	}
	public RenderedSide getDefaultSide() {
		return defaultSide;
	}
	public void setDefaultSide(RenderedSide defaultSide) {
		this.defaultSide = defaultSide;
	}
	public RenderedSide getFrozen() {
		return frozen==null?defaultSide:frozen;
	}
	public void setFrozen(RenderedSide frozen) {
		this.frozen = frozen;
	}
	public RenderedSide getFruitful() {
		return fruitful==null?defaultSide:fruitful;
	}
	public void setFruitful(RenderedSide fruitful) {
		this.fruitful = fruitful;
	}
	public RenderedSide getIll() {
		return ill==null?defaultSide:ill;
	}
	public void setIll(RenderedSide ill) {
		this.ill = ill;
	}
	public RenderedSide getPassing() {
		return passing==null?defaultSide:passing;
	}
	public void setPassing(RenderedSide passing) {
		this.passing = passing;
	}
	
	
	
}
