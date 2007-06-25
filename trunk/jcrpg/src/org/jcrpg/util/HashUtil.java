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

package org.jcrpg.util;

/**
 * Exact int value hash functions to use it in predictably "randomizing" all aspects of JCRPG. 
 * @author pali, Robert Jenkins
 */
public class HashUtil {
	
	/**
	 * Robert Jenkins' 96 bit Mix Function
	 * Robert Jenkins has developed a hash function based on a sequence of subtraction, exclusive-or, and bit shift. 
	 * @return
	 */
	public static int mix(int a, int b, int c)
	{
	  a=a-b;  a=a-c;  a=a^(c >>> 13);
	  b=b-c;  b=b-a;  b=b^(a << 8); 
	  c=c-a;  c=c-b;  c=c^(b >>> 13);
	  a=a-b;  a=a-c;  a=a^(c >>> 12);
	  b=b-c;  b=b-a;  b=b^(a << 16);
	  c=c-a;  c=c-b;  c=c^(b >>> 5);
	  a=a-b;  a=a-c;  a=a^(c >>> 3);
	  b=b-c;  b=b-a;  b=b^(a << 10);
	  c=c-a;  c=c-b;  c=c^(b >>> 15);
	  return Math.abs(c);
	}
	
	public static int mixPercentage(int a, int b, int c)
	{
		return (mix(a,b,c)%100);//+50;
	}
	
	/**
	 * Based on an original suggestion on Robert Jenkin's part in 1997, I have done some research for a version of the integer hash function. This is my latest version as of January 2007. The specific value of the bit shifts are obtained from running the accompanied search program.
	 */
	public static int hash32shift(int x, int y, int z)
	{
	  int key = x+y+z;
	  key = ~key + (key << 15); // key = (key << 15) - key - 1;
	  key = key ^ (key >>> 12);
	  key = key + (key << 2);
	  key = key ^ (key >>> 4);
	  key = key * 2057; // key = (key + (key << 3)) + (key << 11);
	  key = key ^ (key >>> 16);
	  return Math.abs(key);
	}
	
}
