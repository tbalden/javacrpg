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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.jme.util.geom.BufferUtils;

public class BufferPool {
	

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

    private static final int DIVISOR = 500;
    private static final int MAX_SCAN = 100;
    
    private static int hitCount = 0;
    private static int missCount = 0;
    private static int getCount = 0;
    private static int relCount = 0;
    
    public static IntBuffer getIntBuffer2(int capacity)
    {
    	int key = (capacity/DIVISOR)+1;
    	ArrayList<IntBuffer> list = intCache.get(key);
    	IntBuffer buffer = null;
    	int scan = 1;
    	while ( (list==null || list.size()==0) && scan<MAX_SCAN)
    	{
    		list = intCache.get(key+scan);
    		scan++;
    	}
    	if (list == null || list.size()==0)
    	{
    		intBuffCount++;
    		buffer = BufferUtils.createIntBuffer(key*DIVISOR);
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
    	int key = (capacity/DIVISOR)+1;
    	IntBuffer buffer = (IntBuffer)intList.removeElementWithEqualOrBiggerOrderingValue(key);
    	if (buffer == null)
    	{
    		intBuffCount++;
    		buffer = BufferUtils.createIntBuffer(key*DIVISOR);
    		//System.out.println("# I. LOADING NEW "+buffer.capacity()+" K: "+key+" C: "+capacity);
    	} else
    	{
        	buffer.clear();
    		/*buffer.position((capacity)-1);
    		for (int i=(capacity)-1; i<buffer.capacity(); i++)
    		{
    			buffer.put(0);
    		}
    		buffer.position(0);*/
        	intBuffCacheSize--;
    	}
		buffer.limit(capacity);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getVector3Buffer2(int capacity)
    {
    	int key = (capacity/DIVISOR)+1;
    	ArrayList<FloatBuffer> list = v3Cache.get(key);
    	FloatBuffer buffer = null;
    	int scan = 1;
    	while ( (list==null || list.size()==0) && scan<MAX_SCAN)
    	{
    		list = v3Cache.get(key+scan);
    		scan++;
    	}
    	if (list == null || list.size()==0)
    	{
    		v3BuffCount++;
    		buffer = BufferUtils.createVector3Buffer(key*DIVISOR);
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
    	int key = (capacity/DIVISOR)+1;
    	FloatBuffer buffer = (FloatBuffer)v3List.removeElementWithEqualOrBiggerOrderingValue(key);
    	if (buffer == null)
    	{
    		v3BuffCount++;
    		buffer = BufferUtils.createVector3Buffer(key*DIVISOR);
    		//System.out.println("# V3. LOADING NEW  "+buffer.capacity()+" K: "+key+" C: "+capacity);
    	} else
    	{
    		//System.out.println("GETTING FROM CACHE  REALCAP: "+buffer.capacity()+" K: "+key+" C: "+capacity+" LSIZE: ");//+list.size());
    		buffer.clear();
    		/*buffer.position((capacity*3)-1);
    		for (int i=(capacity*3)-1; i<buffer.capacity(); i++)
    		{
    			buffer.put(0.0f);
    		}
    		buffer.position(0);*/
    		v3BuffCacheSize--;
    	}
		buffer.limit(capacity*3);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getVector2Buffer2(int capacity)
    {
    	int key = (capacity/DIVISOR)+1;
    	ArrayList<FloatBuffer> list = v2Cache.get(key);
    	FloatBuffer buffer = null;
    	int scan = 1;
    	while ( (list==null || list.size()==0) && scan<MAX_SCAN)
    	{
    		list = v2Cache.get(key+scan);
    		scan++;
    	}
    	if (list == null || list.size()==0)
    	{
    		v2BuffCount++;
    		buffer = BufferUtils.createVector2Buffer(key*DIVISOR);
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
    	int key = (capacity/DIVISOR)+1;
    	FloatBuffer buffer = (FloatBuffer)v2List.removeElementWithEqualOrBiggerOrderingValue(key);
    	if (buffer==null)
    	{
    		v2BuffCount++;
    		buffer = BufferUtils.createVector2Buffer(key*DIVISOR);
    	} else
    	{
    		buffer.clear();
    		/*buffer.position((capacity*2)-1);
    		for (int i=(capacity*2)-1; i<buffer.capacity(); i++)
    		{
    			buffer.put(0.0f);
    		}
    		buffer.position(0);*/
    		v2BuffCacheSize--;
    	}
		buffer.limit(capacity*2);
		buffer.rewind();
		return buffer;
    }

    public static FloatBuffer getFloatBuffer2(int capacity)
    {
    	int key = (capacity/DIVISOR)+1;
    	ArrayList<FloatBuffer> list = floatCache.get(key);
    	FloatBuffer buffer = null;
    	int scan = 1;
    	while ( (list==null || list.size()==0) && scan<MAX_SCAN)
    	{
    		list = floatCache.get(key+scan);
    		scan++;
    	}
    	if (list == null || list.size()==0)
    	{
    		floatBuffCount++;
    		buffer = BufferUtils.createFloatBuffer(key*DIVISOR);
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

    public static FloatBuffer getFloatBuffer(Object parent, int capacity)
    {
    	getCount++;
    	parents.add(parent);
    	int key = (capacity/DIVISOR)+1;
    	FloatBuffer buffer = (FloatBuffer)floatList.removeElementWithEqualOrBiggerOrderingValue(key);
    	if (buffer==null)
    	{
    		floatBuffCount++;
    		missCount++;
    		buffer = BufferUtils.createFloatBuffer(key*DIVISOR);
    	} else
    	{
    		buffer.clear();
    		floatBuffCacheSize--;
    		hitCount++;
    	}
		buffer.limit(capacity);
		buffer.rewind();
		return buffer;
    }

    public static void releaseIntBuffer2(IntBuffer buff)
    {
    	if (buff==null) return;
    	int key = buff.capacity()/DIVISOR;
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
    	int key = buff.capacity()/DIVISOR;
    	intList.addElement(key, buff);
    	intBuffCacheSize++;
    }

    public static void releaseFloatBuffer2(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = buff.capacity()/DIVISOR;
    	ArrayList<FloatBuffer> list = floatCache.get(key);
    	if (list == null)
    	{
    		list = new ArrayList<FloatBuffer>();
    		floatCache.put(key,list);
    	}
    	floatBuffCacheSize++;
    	list.add(buff);
    }
    
    public static HashSet<Object> parents = new HashSet<Object>();

    public static void releaseFloatBuffer(Object parent, FloatBuffer buff)
    {
    	if (buff==null) return;
    	parents.remove(parent);
    	relCount++;
       	int key = buff.capacity()/DIVISOR;
    	floatList.addElement(key, buff);
    	floatBuffCacheSize++;
    }

    public static void releaseVector3Buffer2(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = ((buff.capacity()/3)/DIVISOR);
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
    	int key = ((buff.capacity()/3)/DIVISOR);
    	v3List.addElement(key, buff);
    	v3BuffCacheSize++;
    	//System.out.println("RELEASING "+buff.capacity()+" K: "+key+" LSIZE: ");//+list.size());
    }

    public static void releaseVector2Buffer2(FloatBuffer buff)
    {
    	if (buff==null) return;
    	int key = (buff.capacity()/2)/DIVISOR;
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
    	int key = (buff.capacity()/2)/DIVISOR;
    	v2List.addElement(key, buff);
    	v2BuffCacheSize++;
    }

    public static String getBufferInfo()
    {
    	String ret = ""+new Date();
    	ret = ret+" BPOOL v2BuffCount = "+ v2BuffCount + " "+v2BuffCacheSize+ "\n";
    	ret = ret+" v3BuffCount = "+ v3BuffCount + " "+v3BuffCacheSize+ "\n";
    	ret = ret+" intBuffCount = "+ intBuffCount + " "+intBuffCacheSize+ "\n";
    	ret = ret+" floatBuffCount = "+ floatBuffCount + " "+floatBuffCount+ "\n--\n";
    	return ret;
    }
    public static String getShortBufferInfo()
    {
    	String ret = ""+new Date();
    	ret = ret+" BP v2 = "+ v2BuffCount + "/"+v2BuffCacheSize+" f "+floatBuffCount+ "/"+floatBuffCacheSize+" H/M "+hitCount+"/"+missCount+" G/R "+getCount+"/"+relCount;
    	//hitCount = 0;
    	//missCount = 0;
    	return ret;
    }
    
    public static void listRemaining()
    {
    	System.out.println("---------------------------------");
    	for (Object o:parents)
    	{
    		System.out.println("-- "+o);
    	}
    }
    
}
