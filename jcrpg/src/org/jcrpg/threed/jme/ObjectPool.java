/**
 *
 * This is a modified version of the following material:
 *  
 * Copyright (c) 2008-2009 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package org.jcrpg.threed.jme;

import java.util.ArrayList;
import java.util.List;

import com.jme.util.export.Savable;

/**
 * Simple Object pool for use with our Math Library to help reduce object creation during calculations. This class uses
 * a ThreadLocal pool of objects to allow for fast multi-threaded use.
 * 
 * @param <T>
 *            the type.
 */
public abstract class ObjectPool<T extends Savable> {
    private final ThreadLocal<List<T>> _pool = new ThreadLocal<List<T>>() {
        @Override
        protected List<T> initialValue() {
            return new ArrayList<T>(_maxSize);
        }
    };

    public final int getSize()
    {
    	return _pool.get().size();
    }
    private final int _maxSize;

    protected ObjectPool(final int maxSize) {
        _maxSize = maxSize;
    }

    protected abstract T newInstance();

    public final T fetch() {
        final List<T> objects = _pool.get();
        /*if (objects.isEmpty())
        {
        	return newInstance();
        } else
        {
        	T r= objects.remove(objects.size() - 1);
        	System.out.println("GETTING FROM POOL "+r.getClass());
        	return r;
        }*/
        return objects.isEmpty() ? newInstance() : objects.remove(objects.size() - 1);
    }

    public final void release(final T object) {
        if (object == null) {
            throw new RuntimeException("Should not release null objects into ObjectPool.");
        }

        final List<T> objects = _pool.get();
        if (objects.size() < _maxSize) {
            reset(object);
            objects.add(object);
        }
    }

    protected void reset(final T object) {}
    
    public int newCreation = 0;

    public static <T extends Savable> ObjectPool<T> create(final Class<T> clazz, final int maxSize) {
        return new ObjectPool<T>(maxSize) {
            @Override
            protected T newInstance() {
                try {
                	newCreation++;
                    return clazz.newInstance();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
