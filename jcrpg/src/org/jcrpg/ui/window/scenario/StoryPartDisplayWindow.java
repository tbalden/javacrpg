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

package org.jcrpg.ui.window.scenario;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jcrpg.game.scenario.element.StoryPart;
import org.jcrpg.game.scenario.element.StoryPart.Block;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.text.Text;
import org.jcrpg.ui.window.InputWindow;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.TextButton;

import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;

public class StoryPartDisplayWindow extends InputWindow implements Runnable{

	public ArrayList<Text> scrollingLines = new ArrayList<Text>();
	public int MAX_LINES_PER_BLOCK = 30;
	
	public int MAX_LETTERS_PER_LINE = 50;
	
	TextButton nextWindow = null;
	
	public class ScrollTextNode extends Node
	{
		private float fliedTime= 0f;
		private float maxTime = 1.5f;
		private boolean flying = false;
		private float speed = 0.1f;
		
		public ArrayList<Runnable> onFinish = new ArrayList<Runnable>();
		
		public ArrayList<Text> texts = new ArrayList<Text>();
		
		@Override
		public void updateGeometricState(float time, boolean initiator) {
			if (flying && fliedTime<maxTime) {
				localTranslation.addLocal(0f, speed*time / ((fliedTime+maxTime/1.7f)/maxTime), 0f);
				fliedTime+=1f*time;
				for (Text t:texts)
				{
					float y = Math.min(1f,(t.getWorldTranslation().y/core.getDisplay().getHeight())*8f) ;
					t.getTextColor().b = y;
					t.getTextColor().r = y;
					t.getTextColor().g = y;
					t.getTextColor().a = y;
				}
			} else
			if (flying && fliedTime>=maxTime)
			{
				//this.removeFromParent();
				if (onFinish!=null)
				{
					for (Runnable r:onFinish)
					{
						r.run();
					}
				}
				return;
			}
			super.updateGeometricState(time, initiator);
		}
		
		public boolean isFinishedPlaying()
		{
			if (fliedTime>=maxTime)
			{
				return true;
			}
			return false;
		}
		public void startFlying()
		{
			flying = true;
		}
		public void startFlying(float speed, float maxTime)
		{
			this.speed = speed;
			this.maxTime = maxTime;
			flying = true;
		}		
	}

	ScrollTextNode scrollNode = new ScrollTextNode();

	public StoryPartDisplayWindow(UIBase base) {
		super(base);
		try {
			Quad q = loadImageToQuad("./data/ui/dark_mask.png", core.getDisplay().getWidth(), core.getDisplay().getHeight(), 
        			core.getDisplay().getWidth() / 2, core.getDisplay().getHeight() / 2);
        	q.setRenderState(base.hud.hudAS);
        	windowNode.attachChild(q);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		int height = DisplaySystem.getDisplaySystem().getHeight();
		int width = DisplaySystem.getDisplaySystem().getWidth();

		scrollNode.onFinish.add(this);
		
		for (int i=0; i<MAX_LINES_PER_BLOCK; i++)
		{
			Text t = Text.createDefaultTextLabel("demo_line_"+i);
			t.setLocalTranslation(0.15f * width, height - i* height*0.05f, 0f);
			scrollingLines.add(t);
			scrollNode.attachChild(t);
			scrollNode.texts.add(t);
		}
		windowNode.attachChild(scrollNode);
		
    	nextWindow = new TextButton("next",this,windowNode, 0.50f, 0.870f, 0.08f, 0.06f,500f,"Press Enter...",false);
    	addInput(nextWindow);
		nextWindow.activate();
	}
	
	public StoryPart currentStory = null;
	
	
	public int blocksLeft = 0;
	public int currentBlock = 0;
	
	public void playStoryPart(StoryPart part)
	{
		toggle();
		currentStory = part;
		blocksLeft = currentStory.blocks.size()-1;
		currentBlock = 0;
		playBlock(currentBlock);
	}
	
	public String[] wrapText(String text)
	{
		int wrapLimit = MAX_LETTERS_PER_LINE;
		StringTokenizer st = new StringTokenizer(text," ");
		
		ArrayList<String> lines = new ArrayList<String>();
		String currentLine = "";
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (currentLine.length()+token.length()>wrapLimit)
			{
				if (currentLine.length()<wrapLimit*0.3f)
				{
					String firstPart = token.substring(0,(int)(wrapLimit*0.7f));
					currentLine=currentLine+" "+firstPart;
					lines.add(currentLine);
					token = token.substring((int)(wrapLimit*0.7f));
				} else
				{
					lines.add(currentLine);
				}
				currentLine = "";
			}
			currentLine+=" "+token;
			while
			(currentLine.indexOf("\\n")!=-1)
			{
				lines.add(currentLine.substring(0,currentLine.indexOf("\\n")));
				try {
					currentLine = currentLine.substring(currentLine.indexOf("\\n")+2);
				} catch (Exception ex)
				{
					currentLine = "";
				}
			}
		}
		lines.add(currentLine);
		return lines.toArray(new String[0]);
	}
	
	private void playBlock(int block)
	{
		nextWindow.baseNode.removeFromParent();
		Block blockInstance = currentStory.blocks.get(block);
		String text = blockInstance.text;
		//core.uiBase.hud.mainBox.addEntry("I: "+text);
		String[] lines = wrapText(text);
		int counter = 0;
		for (String line:lines)
		{
			if (scrollingLines.size()>counter)
			{
				scrollingLines.get(counter++).print(line);
			}
		}
		scrollNode.setLocalTranslation(0f, 0-core.getDisplay().getHeight(), 0f);
		scrollNode.startFlying(20.0f, 28f);
		scrollNode.updateRenderState();
	}
	

	@Override
	public boolean inputEntered(InputBase base, String message) {
		return false;
	}

	@Override
	public boolean inputLeft(InputBase base, String message) {
		return false;
	}

	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (blocksLeft==0)
		{
			toggle();
			currentStory.finishedPlaying();
		}
		else
		{
			blocksLeft--;
			currentBlock++;
			playBlock(currentBlock);
		}
		
		return false;
	}

	public void run() {
		nextWindow.reattach();
	}

}
