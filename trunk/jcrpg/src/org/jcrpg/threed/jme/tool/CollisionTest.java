/*
 *  Copyright (C) 2008 Galun
 *
 *  This is free software; you can redistribute it and/or modify
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
package org.jcrpg.threed.jme.tool;

import com.jme.intersection.TriangleCollisionResults;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
/**
 * 
 * @author Galun @ World-of-mystery.de, GPLv3 licensed.
 * http://code.google.com/p/worldofmystery/source/browse/trunk/worldofmystery/src/de/wom/rpg/CollisionTest.java
 *
 */
public class CollisionTest {

        private Node collisionNode;
        private TriangleCollisionResults collisionResults;
        private int numberTriangleCollisions;
        private Vector3f collisionDirection;
        private int numberCollisionStructures;

        public CollisionTest(Node collisionNode) {
                this.collisionNode = collisionNode;
                collisionResults = new TriangleCollisionResults();
                collisionDirection = new Vector3f();
        }

        public void updateCollisionResults(Spatial node) {
                synchronized(collisionResults) {
                        collisionResults.clear();
                        node.calculateCollisions(collisionNode, collisionResults);
                        numberTriangleCollisions = 0;
                        numberCollisionStructures = collisionResults.getNumber();
                        collisionDirection.set(0, 0, 0);
                        for (int i = 0; i < numberCollisionStructures; i++) {
                                int collSize = collisionResults.getCollisionData(i).getSourceTris().size();
                                if (collSize > 0) {
                                        Geometry mesh = collisionResults.getCollisionData(i).getTargetMesh();
                                        // only a rough idea where the obstacle is
                                        collisionDirection.addLocal(node.getWorldTranslation().subtract(mesh.getWorldTranslation()));
                                        numberTriangleCollisions += collSize;
                                }
                        }
                }
        }

        public boolean isColliding() {
                return numberTriangleCollisions > 0;
        }

        public Vector3f getCollisionDirection() {
                return collisionDirection;
        }

        public int getNumberCollisionStructures() {
                return numberCollisionStructures;
        }

        public int getNumberTriangleCollisions() {
                return numberTriangleCollisions;
        }
}
