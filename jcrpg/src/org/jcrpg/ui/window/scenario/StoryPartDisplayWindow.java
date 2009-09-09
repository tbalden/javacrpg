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

import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;

public class StoryPartDisplayWindow extends InputWindow {

	public ArrayList<Text> scrollingLines = new ArrayList<Text>();
	public int MAX_LINES_PER_BLOCK = 30;
	
	public int MAX_LETTERS_PER_LINE = 50;
	
	TextButton nextWindow = null;
	
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

		for (int i=0; i<MAX_LINES_PER_BLOCK; i++)
		{
			Text t = Text.createDefaultTextLabel("demo_line_"+i);
			t.setLocalTranslation(0.1f * width, height - i* height*0.05f, 0f);
			scrollingLines.add(t);
			windowNode.attachChild(t);
		}
		
    	nextWindow = new TextButton("next",this,windowNode, 0.50f, 0.810f, 0.08f, 0.06f,600f,"Press Enter...",false);
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

}
