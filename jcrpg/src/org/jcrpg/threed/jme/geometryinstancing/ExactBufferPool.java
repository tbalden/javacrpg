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
package org.jcrpg.threed.jme.geometryinstancing;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.TreeMap;

import com.jme.util.geom.BufferUtils;

public class ExactBufferPool {
	
    private static TreeMap<Integer,HashSet<IntBuffer>> intCache = new TreeMap<Integer, HashSet<IntBuffer>>();
    private static TreeMap<Integer,HashSet<FloatBuffer>> v3Cache = new TreeMap<Integer, HashSet<FloatBuffer>>();
    private static TreeMap<Integer,HashSet<FloatBuffer>> v2Cache = new TreeMap<Integer, HashSet<FloatBuffer>>();
    private static TreeMap<Integer,HashSet<FloatBuffer>> floatCache = new TreeMap<Integer, HashSet<FloatBuffer>>();

    private static final int DIVISOR = 1;
    private static final int MAX_SCAN = 100;
    
    public static IntBuffer getIntBuffer(int capacity)
    {
    	int key = capacity;
    	HashSet<IntBuffer> list = intCache.get(key);
    	IntBuffer buffer = null;
    	if (list == null || list.size()==0)
    	{
    		buffer = BufferUtils.createIntBuffer(key*DIVISOR);
    		//System.out.println("# I. LOADING NEW "+buffer.capacity()+" K: "+key+" C: "+capacity);
    	} else
    	{
    		buffer = list.iterator().next();
    		list.remove(buffer);
        	buffer.clear();
    	}
		buffer.limit(capacity);
		buffer.rewind();
		return buffer;
    }
	
    public static FloatBuffer getVector3Buffer(int capacity)
    {
    	int key = capacity;
    	HashSet<FloatBuffer> list = v3Cache.get(key);
    	FloatBuffer buffer = null;
    	if (list == null || list.size()==0)
    	{
    		buffer = BufferUtils.createVector3Buffer(key*DIVISOR);
    		//System.out.println("# V3. LOADING NEW (SCANS:"+scan+") "+buffer.capacity()+" K: "+key+" C: "+capacity);
    	} else
    	{
    		buffer = list.iterator().next();
    		list.remove(buffer);
    		//System.out.println("GETTING FROM CACHE (SCANS:"+scan+") REALCAP: "+buffer.capacity()+" K: "+key+" C: "+capacity);
    		buffer.clear();
    	}
		buffer.limit(capacity*3);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getVector2Buffer(int capacity)
    {
    	int key = (capacity);
    	HashSet<FloatBuffer> list = v2Cache.get(key);
    	FloatBuffer buffer = null;
    	if (list == null || list.size()==0)
    	{
    		buffer = BufferUtils.createVector2Buffer(key*DIVISOR);
    	} else
    	{
    		buffer = list.iterator().next();
    		list.remove(buffer);
    		buffer.clear();
    	}
		buffer.limit(capacity*2);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getFloatBuffer(int capacity)
    {
    	int key = (capacity);
    	HashSet<FloatBuffer> list = floatCache.get(key);
    	FloatBuffer buffer = null;
    	int scan = 1;
    	while ( (list==null || list.size()==0) && scan<MAX_SCAN)
    	{
    		list = floatCache.get(key+scan);
    		scan++;
    	}
    	if (list == null || list.size()==0)
    	{
    		buffer = BufferUtils.createFloatBuffer(key*DIVISOR);
    	} else
    	{
    		buffer = list.iterator().next();
    		list.remove(buffer);
    		buffer.clear();
    	}
		buffer.limit(capacity);
		buffer.rewind();
		return buffer;
    }
    
    public static void releaseIntBuffer(IntBuffer buff)
    {
    	if (buff==null) return;
    	int key = buff.capacity();
    	HashSet<IntBuffer> list = intCache.get(key);
    	if (list == null)
    	{
    		list = new HashSet<IntBuffer>();
    		intCache.put(key,list);
    	}
    	list.add(buff);
    }

    public static void releaseFloatBuffer(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = buff.capacity();
    	HashSet<FloatBuffer> list = floatCache.get(key);
    	if (list == null)
    	{
    		list = new HashSet<FloatBuffer>();
    		floatCache.put(key,list);
    	}
    	list.add(buff);
    }

    public static void releaseVector3Buffer(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = (buff.capacity()/3);
    	//System.out.println("RELEASING "+buff.capacity()+" K: "+key);
    	HashSet<FloatBuffer> list = v3Cache.get(key);
    	if (list == null)
    	{
    		list = new HashSet<FloatBuffer>();
    		v3Cache.put(key,list);
    	}
    	list.add(buff);
    }
    public static void releaseVector2Buffer(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = (buff.capacity()/2);
    	HashSet<FloatBuffer> list = v2Cache.get(key);
    	if (list == null)
    	{
    		list = new HashSet<FloatBuffer>();
    		v2Cache.put(key,list);
    	}
    	list.add(buff);
    }

    
}
