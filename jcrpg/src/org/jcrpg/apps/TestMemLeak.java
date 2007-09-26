/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.apps;

import java.io.File;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jmex.model.util.ModelLoader;

public class TestMemLeak extends SimpleGame {

	public static void main(String[] args) {
		try {
			
			TestMemLeak app = new TestMemLeak();
	        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
	        app.start();
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void simpleInitGame() {
		org.lwjgl.input.Mouse.setGrabbed(false);   
		try {
			Node n = ModelLoader.loadModel(new File("./data/models/acacia_bb.3ds"));
			System.out.println(".!.");
			for (int i=0; i<10000; i++)
			{
				//if (i%100==0) Thread.sleep(100);
				System.out.println(".!.");
				SharedNode sn = new SharedNode("i"+i,n);
				rootNode.attachChild(sn);
				if (i%20==0) rootNode.detachAllChildren();
				if (i%100==0) System.gc();
			}
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
