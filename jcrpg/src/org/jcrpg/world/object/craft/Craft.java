/*
*   This file is part of jClassicRPG.
*	Copyright (C) 2008 Illes Pal Zoltan
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/                                                                       

package org.jcrpg.world.object.craft;

import java.util.HashMap;

import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.object.Obj;
import org.jcrpg.world.object.RawMaterial;

public abstract class Craft extends Obj {
	
	public abstract HashMap<Class<? extends SkillBase>,Integer> getCraftSkillNeeds();
	
	public abstract HashMap<Class<? extends RawMaterial>, Integer> getMaterialNeeds();

}
