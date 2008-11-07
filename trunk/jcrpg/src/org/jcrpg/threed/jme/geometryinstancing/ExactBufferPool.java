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
package org.jcrpg.threed.jme.geometryinstancing;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import com.jme.util.geom.BufferUtils;

public class ExactBufferPool {
	public static int intBuffCount = 0;
	public static int v3BuffCount = 0;
	public static int v2BuffCount = 0;
	public static int floatBuffCount = 0;

	public static int intBuffCacheSize = 0;
	public static int v3BuffCacheSize = 0;
	public static int v2BuffCacheSize = 0;
	public static int floatBuffCacheSize = 0;
	
	private static QuickOrderedList intList = new QuickOrderedList();
	private static QuickOrderedList v3List = new QuickOrderedList();
	private static QuickOrderedList v2List = new QuickOrderedList();
	private static QuickOrderedList floatList = new QuickOrderedList();
	
    private static HashMap<Integer,ArrayList<IntBuffer>> intCache = new HashMap<Integer, ArrayList<IntBuffer>>();
    private static HashMap<Integer,ArrayList<FloatBuffer>> v3Cache = new HashMap<Integer, ArrayList<FloatBuffer>>();
    private static HashMap<Integer,ArrayList<FloatBuffer>> v2Cache = new HashMap<Integer, ArrayList<FloatBuffer>>();
    private static HashMap<Integer,ArrayList<FloatBuffer>> floatCache = new HashMap<Integer, ArrayList<FloatBuffer>>();

    public static IntBuffer getIntBuffer2(int capacity)
    {
    	int key = capacity;
    	ArrayList<IntBuffer> list = intCache.get(key);
    	IntBuffer buffer = null;
    	if (list == null || list.size()==0)
    	{
    		intBuffCount++;
    		buffer = BufferUtils.createIntBuffer(key);
    		//System.out.println("# I. LOADING NEW "+buffer.capacity()+" K: "+key+" C: "+capacity);
    	} else
    	{
    		buffer = list.remove(0);
        	buffer.clear();
        	intBuffCacheSize--;
    	}
		buffer.limit(capacity);
		buffer.rewind();
		return buffer;
    }

    public static IntBuffer getIntBuffer(int capacity)
    {
    	int key = (capacity);
    	IntBuffer buffer = (IntBuffer)intList.removeElementWithEqualOrderingValue(key);
    	if (buffer == null)
    	{
    		intBuffCount++;
    		buffer = BufferUtils.createIntBuffer(key);
    		//System.out.println("# I. LOADING NEW "+buffer.capacity()+" K: "+key+" C: "+capacity);
    	} else
    	{
        	buffer.clear();
        	intBuffCacheSize--;
    	}
		buffer.limit(capacity);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getVector3Buffer2(int capacity)
    {
    	int key = (capacity);
    	ArrayList<FloatBuffer> list = v3Cache.get(key);
    	FloatBuffer buffer = null;
    	if (list == null || list.size()==0)
    	{
    		v3BuffCount++;
    		buffer = BufferUtils.createVector3Buffer(key);
    		//System.out.println("# V3. LOADING NEW (SCANS:"+scan+") "+buffer.capacity()+" K: "+key+" C: "+capacity);
    	} else
    	{
    		buffer = list.remove(0);
    		//System.out.println("GETTING FROM CACHE (SCANS:"+scan+") REALCAP: "+buffer.capacity()+" K: "+key+"/"+(key+scan)+" C: "+capacity+" LSIZE: "+list.size());
    		buffer.clear();
    		v3BuffCacheSize--;
    	}
		buffer.limit(capacity*3);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getVector3Buffer(int capacity)
    {
    	int key = (capacity);
    	FloatBuffer buffer = (FloatBuffer)v3List.removeElementWithEqualOrderingValue(key);
    	if (buffer == null)
    	{
    		v3BuffCount++;
    		buffer = BufferUtils.createVector3Buffer(key);
    		//System.out.println("# V3. LOADING NEW  "+buffer.capacity()+" K: "+key+" C: "+capacity);
    	} else
    	{
    		//System.out.println("GETTING FROM CACHE  REALCAP: "+buffer.capacity()+" K: "+key+" C: "+capacity*3+" LSIZE: ");//+list.size());
    		buffer.clear();
    		v3BuffCacheSize--;
    	}
		buffer.limit(capacity*3);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getVector2Buffer2(int capacity)
    {
    	int key = (capacity);
    	ArrayList<FloatBuffer> list = v2Cache.get(key);
    	FloatBuffer buffer = null;
    	if (list == null || list.size()==0)
    	{
    		v2BuffCount++;
    		buffer = BufferUtils.createVector2Buffer(key);
    	} else
    	{
    		buffer = list.remove(0);
    		buffer.clear();
    		v2BuffCacheSize--;
    	}
		buffer.limit(capacity*2);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getVector2Buffer(int capacity)
    {
    	int key = (capacity);
    	FloatBuffer buffer = (FloatBuffer)v2List.removeElementWithEqualOrderingValue(key);
    	if (buffer==null)
    	{
    		v2BuffCount++;
    		buffer = BufferUtils.createVector2Buffer(key);
    	} else
    	{
    		buffer.clear();
    		v2BuffCacheSize--;
    	}
		buffer.limit(capacity*2);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getFloatBuffer2(int capacity)
    {
    	int key = (capacity);
    	ArrayList<FloatBuffer> list = floatCache.get(key);
    	FloatBuffer buffer = null;
    	if (list == null || list.size()==0)
    	{
    		floatBuffCount++;
    		buffer = BufferUtils.createFloatBuffer(key);
    	} else
    	{
    		buffer = list.remove(0);
    		buffer.clear();
    		floatBuffCacheSize--;
    	}
		buffer.limit(capacity);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getFloatBuffer(int capacity)
    {
    	int key = (capacity);
    	FloatBuffer buffer = (FloatBuffer)v2List.removeElementWithEqualOrderingValue(key);
    	if (buffer==null)
    	{
    		floatBuffCount++;
    		buffer = BufferUtils.createFloatBuffer(key);
    	} else
    	{
    		buffer.clear();
    		floatBuffCacheSize--;
    	}
		buffer.limit(capacity);
		buffer.rewind();
		return buffer;
    }

    public static void releaseIntBuffer2(IntBuffer buff)
    {
    	if (buff==null) return;
    	int key = buff.capacity();
    	ArrayList<IntBuffer> list = intCache.get(key);
    	if (list == null)
    	{
    		list = new ArrayList<IntBuffer>();
    		intCache.put(key,list);
    	}
    	intBuffCacheSize++;
    	list.add(buff);
    }

    public static void releaseIntBuffer(IntBuffer buff)
    {
    	if (buff==null) return;
    	int key = buff.capacity();
    	intList.addElement(key, buff);
    	intBuffCacheSize++;
    }

    public static void releaseFloatBuffer2(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = buff.capacity();
    	ArrayList<FloatBuffer> list = floatCache.get(key);
    	if (list == null)
    	{
    		list = new ArrayList<FloatBuffer>();
    		floatCache.put(key,list);
    	}
    	floatBuffCacheSize++;
    	list.add(buff);
    }

    public static void releaseFloatBuffer(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = buff.capacity();
    	floatList.addElement(key, buff);
    	floatBuffCacheSize++;
    }

    public static void releaseVector3Buffer2(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = ((buff.capacity()/3));
    	ArrayList<FloatBuffer> list = v3Cache.get(key);
    	if (list == null)
    	{
    		list = new ArrayList<FloatBuffer>();
    		v3Cache.put(key,list);
    	}
    	v3BuffCacheSize++;
    	list.add(buff);
    	//System.out.println("RELEASING "+buff.capacity()+" K: "+key+" LSIZE: "+list.size());
    }
    public static void releaseVector3Buffer(FloatBuffer buff)
    {
    	if (buff==null) return;    	
    	int key = ((buff.capacity()/3));
    	v3List.addElement(key, buff);
    	v3BuffCacheSize++;
    	//System.out.println("RELEASING "+buff.capacity()+" K: "+key+" LSIZE: ");//+list.size());
    }

    public static void releaseVector2Buffer2(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = (buff.capacity()/2);
    	ArrayList<FloatBuffer> list = v2Cache.get(key);
    	if (list == null)
    	{
    		list = new ArrayList<FloatBuffer>();
    		v2Cache.put(key,list);
    	}
    	v2BuffCacheSize++;
    	list.add(buff);
    }

    public static void releaseVector2Buffer(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = (buff.capacity()/2);
    	v2List.addElement(key, buff);
    	v2BuffCacheSize++;
    }

    
}
