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

package org.jcrpg.threed.scene.config;

import java.util.HashMap;

import org.jcrpg.space.Side;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.jme.GeometryBatchHelper;
import org.jcrpg.threed.scene.model.LODModel;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.PartlyBillboardModel;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.TextureStateVegetationModel;
import org.jcrpg.threed.scene.side.RenderedClimateDependentSide;
import org.jcrpg.threed.scene.side.RenderedContinuousSide;
import org.jcrpg.threed.scene.side.RenderedHashAlteredSide;
import org.jcrpg.threed.scene.side.RenderedHashRotatedSide;
import org.jcrpg.threed.scene.side.RenderedSide;
import org.jcrpg.threed.scene.side.RenderedTopSide;
import org.jcrpg.world.ai.flora.ground.Grass;
import org.jcrpg.world.ai.flora.ground.JungleGround;
import org.jcrpg.world.ai.flora.ground.Sand;
import org.jcrpg.world.ai.flora.ground.Snow;
import org.jcrpg.world.ai.flora.middle.deciduous.GreenBush;
import org.jcrpg.world.ai.flora.middle.grass.Anathum;
import org.jcrpg.world.ai.flora.middle.mushroom.CaveMushroom;
import org.jcrpg.world.ai.flora.middle.mushroom.RedForestMushroom;
import org.jcrpg.world.ai.flora.middle.succulent.GreenFern;
import org.jcrpg.world.ai.flora.middle.succulent.JungleBush;
import org.jcrpg.world.ai.flora.tree.cactus.BigCactus;
import org.jcrpg.world.ai.flora.tree.deciduous.Acacia;
import org.jcrpg.world.ai.flora.tree.deciduous.CherryTree;
import org.jcrpg.world.ai.flora.tree.deciduous.OakTree;
import org.jcrpg.world.ai.flora.tree.palm.CoconutTree;
import org.jcrpg.world.ai.flora.tree.palm.JunglePalmTrees;
import org.jcrpg.world.ai.flora.tree.pine.GreatPineTree;
import org.jcrpg.world.ai.flora.tree.pine.GreenPineTree;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.MountainNew;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.sub.Cave;
import org.jcrpg.world.place.water.Lake;
import org.jcrpg.world.place.water.Ocean;
import org.jcrpg.world.place.water.River;

/**
 * Configures file level models to model types and them to Side Types of the cubes.
 * @author pali
 *
 */
public class SideTypeModels {

	public static Integer EMPTY_SIDE = J3DCore.EMPTY_SIDE; 
	public static float[][] TREE_LOD_DIST = J3DCore.TREE_LOD_DIST_LOW;
	public static float CUBE_EDGE_SIZE = J3DCore.CUBE_EDGE_SIZE;
	
	public SideTypeModels()
	{
		
	}
	
	public void fillMap(HashMap<String,Integer> hmCubeSideSubTypeToRenderedSideId, HashMap<Integer,RenderedSide> hm3dTypeRenderedSide, boolean MIPMAP_TREES, boolean DETAILED_TREES, boolean BUMPED_GROUND, float RENDER_GRASS_DISTANCE, boolean LOD_VEGETATION)
	{
		// area subtype to 3d type mapping
		hmCubeSideSubTypeToRenderedSideId.put(Side.DEFAULT_SUBTYPE.id, EMPTY_SIDE);
		hmCubeSideSubTypeToRenderedSideId.put(World.SUBTYPE_OCEAN.id, new Integer(10));
		hmCubeSideSubTypeToRenderedSideId.put(World.SUBTYPE_GROUND.id, new Integer(21));
		hmCubeSideSubTypeToRenderedSideId.put(Plain.SUBTYPE_GROUND.id, EMPTY_SIDE); // no 3d object, flora ground will be rendered
		hmCubeSideSubTypeToRenderedSideId.put(Forest.SUBTYPE_FOREST.id, EMPTY_SIDE);
		hmCubeSideSubTypeToRenderedSideId.put(Lake.SUBTYPE_WATER.id, new Integer(10));
		hmCubeSideSubTypeToRenderedSideId.put(Lake.SUBTYPE_ROCKSIDE.id, new Integer(39));
		hmCubeSideSubTypeToRenderedSideId.put(Lake.SUBTYPE_ROCKBOTTOM.id, new Integer(38));
		hmCubeSideSubTypeToRenderedSideId.put(Lake.SUBTYPE_WATER_EMPTY.id, EMPTY_SIDE);
		hmCubeSideSubTypeToRenderedSideId.put(Ocean.SUBTYPE_WATER.id, new Integer(10));
		hmCubeSideSubTypeToRenderedSideId.put(Ocean.SUBTYPE_ROCKSIDE.id, new Integer(39));
		hmCubeSideSubTypeToRenderedSideId.put(Ocean.SUBTYPE_ROCKBOTTOM.id, new Integer(38));
		hmCubeSideSubTypeToRenderedSideId.put(Ocean.SUBTYPE_WATER_EMPTY.id, EMPTY_SIDE);
		hmCubeSideSubTypeToRenderedSideId.put(River.SUBTYPE_WATER.id, new Integer(10));
		hmCubeSideSubTypeToRenderedSideId.put(River.SUBTYPE_WATERFALL.id, new Integer(36));
		hmCubeSideSubTypeToRenderedSideId.put(River.SUBTYPE_INTERSECT.id, new Integer(27));
		hmCubeSideSubTypeToRenderedSideId.put(River.SUBTYPE_ROCKSIDE.id, new Integer(39));
		hmCubeSideSubTypeToRenderedSideId.put(River.SUBTYPE_ROCKBOTTOM.id, new Integer(38));
		hmCubeSideSubTypeToRenderedSideId.put(River.SUBTYPE_ROCKBOTTOM_STEEP.id, new Integer(38));
		hmCubeSideSubTypeToRenderedSideId.put(River.SUBTYPE_WATER_EMPTY.id, EMPTY_SIDE);
		hmCubeSideSubTypeToRenderedSideId.put(House.SUBTYPE_INTERNAL_CEILING.id, new Integer(7));
		hmCubeSideSubTypeToRenderedSideId.put(House.SUBTYPE_INTERNAL_GROUND.id, new Integer(29));
		hmCubeSideSubTypeToRenderedSideId.put(House.SUBTYPE_BOOKCASE.id, new Integer(28));
		hmCubeSideSubTypeToRenderedSideId.put(House.SUBTYPE_EXTERNAL_GROUND.id, new Integer(3));
		hmCubeSideSubTypeToRenderedSideId.put(House.SUBTYPE_EXTERNAL_DOOR.id, new Integer(5));
		hmCubeSideSubTypeToRenderedSideId.put(House.SUBTYPE_WINDOW.id, new Integer(6));
		hmCubeSideSubTypeToRenderedSideId.put(House.SUBTYPE_WALL.id, new Integer(1));
		hmCubeSideSubTypeToRenderedSideId.put(MountainNew.SUBTYPE_STEEP.id, 41); 
		hmCubeSideSubTypeToRenderedSideId.put(MountainNew.SUBTYPE_INTERSECT_EMPTY.id, EMPTY_SIDE); // No 3d object, it is just climbing side
		hmCubeSideSubTypeToRenderedSideId.put(MountainNew.SUBTYPE_ROCK_BLOCK.id, EMPTY_SIDE);//new Integer(13));
		hmCubeSideSubTypeToRenderedSideId.put(MountainNew.SUBTYPE_ROCK_BLOCK_VISIBLE.id, new Integer(13));//13));
		hmCubeSideSubTypeToRenderedSideId.put(MountainNew.SUBTYPE_ROCK_SIDE.id, new Integer(35));
		hmCubeSideSubTypeToRenderedSideId.put(MountainNew.SUBTYPE_GROUND.id, EMPTY_SIDE); // no 3d object, flora ground will be rendered
		hmCubeSideSubTypeToRenderedSideId.put(MountainNew.SUBTYPE_INTERSECT.id, new Integer(27));
		hmCubeSideSubTypeToRenderedSideId.put(MountainNew.SUBTYPE_CORNER.id, new Integer(40));
		hmCubeSideSubTypeToRenderedSideId.put(MountainNew.SUBTYPE_INTERSECT_BLOCK.id, EMPTY_SIDE);
		
		hmCubeSideSubTypeToRenderedSideId.put(Geography.SUBTYPE_STEEP.id, 41); // TODO create element for this !!! // no 3d object, flora ground will be rendered rotated!
		hmCubeSideSubTypeToRenderedSideId.put(Geography.SUBTYPE_INTERSECT_EMPTY.id, EMPTY_SIDE); // No 3d object, it is just climbing side
		hmCubeSideSubTypeToRenderedSideId.put(Geography.SUBTYPE_ROCK_BLOCK.id, EMPTY_SIDE);//new Integer(13));
		hmCubeSideSubTypeToRenderedSideId.put(Geography.SUBTYPE_ROCK_BLOCK_VISIBLE.id, new Integer(13));//13));
		hmCubeSideSubTypeToRenderedSideId.put(Geography.SUBTYPE_ROCK_SIDE.id, new Integer(35));
		hmCubeSideSubTypeToRenderedSideId.put(Geography.SUBTYPE_GROUND.id, EMPTY_SIDE); // no 3d object, flora ground will be rendered
		hmCubeSideSubTypeToRenderedSideId.put(Geography.SUBTYPE_INTERSECT.id, new Integer(27));
		hmCubeSideSubTypeToRenderedSideId.put(Geography.SUBTYPE_CORNER.id, new Integer(40));
		hmCubeSideSubTypeToRenderedSideId.put(Geography.SUBTYPE_INTERSECT_BLOCK.id, EMPTY_SIDE);
		
		hmCubeSideSubTypeToRenderedSideId.put(OakTree.SUBTYPE_TREE.id, new Integer(9));
		hmCubeSideSubTypeToRenderedSideId.put(CherryTree.SUBTYPE_TREE.id, new Integer(12));
		hmCubeSideSubTypeToRenderedSideId.put(GreenPineTree.SUBTYPE_TREE.id, new Integer(18));
		hmCubeSideSubTypeToRenderedSideId.put(GreatPineTree.SUBTYPE_TREE.id, new Integer(25));
		hmCubeSideSubTypeToRenderedSideId.put(GreenBush.SUBTYPE_BUSH.id, new Integer(19));
		hmCubeSideSubTypeToRenderedSideId.put(CoconutTree.SUBTYPE_TREE.id, new Integer(15));
		hmCubeSideSubTypeToRenderedSideId.put(Acacia.SUBTYPE_TREE.id, new Integer(20));
		hmCubeSideSubTypeToRenderedSideId.put(Grass.SUBTYPE_GRASS.id, new Integer(2));
		hmCubeSideSubTypeToRenderedSideId.put(Sand.SUBTYPE_SAND.id, new Integer(16));
		hmCubeSideSubTypeToRenderedSideId.put(Snow.SUBTYPE_SNOW.id, new Integer(17));
		hmCubeSideSubTypeToRenderedSideId.put(JungleGround.SUBTYPE_GROUND.id, new Integer(22));
		hmCubeSideSubTypeToRenderedSideId.put(BigCactus.SUBTYPE_CACTUS.id, new Integer(23));
		hmCubeSideSubTypeToRenderedSideId.put(JunglePalmTrees.SUBTYPE_TREE.id,  new Integer(24));//new Integer(24)); TODO quad model
		hmCubeSideSubTypeToRenderedSideId.put(GreenFern.SUBTYPE_BUSH.id, EMPTY_SIDE);//new Integer(26)); TODO, quad model?
		hmCubeSideSubTypeToRenderedSideId.put(JungleBush.SUBTYPE_BUSH.id, new Integer(30));
		hmCubeSideSubTypeToRenderedSideId.put(Cave.SUBTYPE_GROUND.id, new Integer(31));
		hmCubeSideSubTypeToRenderedSideId.put(Cave.SUBTYPE_BLOCK_GROUND.id, EMPTY_SIDE);
		hmCubeSideSubTypeToRenderedSideId.put(Cave.SUBTYPE_WALL.id, new Integer(32));
		hmCubeSideSubTypeToRenderedSideId.put(Cave.SUBTYPE_WALL_REVERSE.id, new Integer(35));
		hmCubeSideSubTypeToRenderedSideId.put(Cave.SUBTYPE_ENTRANCE.id, new Integer(33));
		hmCubeSideSubTypeToRenderedSideId.put(Cave.SUBTYPE_ROCK.id, new Integer(34));
		hmCubeSideSubTypeToRenderedSideId.put(Cave.SUBTYPE_BLOCK.id, EMPTY_SIDE);
		hmCubeSideSubTypeToRenderedSideId.put(Geography.SUBTYPE_ROCK_DOWNSIDE.id, 42);

		hmCubeSideSubTypeToRenderedSideId.put(RedForestMushroom.SUBTYPE_REDFORESTMUSHROOM.id, new Integer(43));
		hmCubeSideSubTypeToRenderedSideId.put(CaveMushroom.SUBTYPE_CAVEMUSHROOM.id, new Integer(44));
		
		hmCubeSideSubTypeToRenderedSideId.put(Anathum.SUBTYPE_ANATHUM.id, new Integer(45));

		
		PartlyBillboardModel cherry = new PartlyBillboardModel("pbm_cherry_0","models/tree/cherry_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"cher_1.png"},0,MIPMAP_TREES);
		cherry.shadowCaster = true;
		PartlyBillboardModel cherry_low = new PartlyBillboardModel("pbm_cherry_1","models/tree/cherry_bb1.obj",new String[]{"3"},new String[]{},new String[]{"cher_1.png"},1,MIPMAP_TREES);
		cherry_low.shadowCaster = true;
		//cherry_low.windAnimation = false;
		PartlyBillboardModel cherry_lowest = new PartlyBillboardModel("pbm_cherry_2","models/tree/cherry_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"cher_1.png"},2,MIPMAP_TREES);
		cherry_lowest.windAnimation = false;
		PartlyBillboardModel cherry_lowest_2 = new PartlyBillboardModel("pbm_cherry_3","models/tree/cherry_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"cher_1.png"},3,MIPMAP_TREES);
		cherry_lowest_2.windAnimation = false;

		PartlyBillboardModel acacia = new PartlyBillboardModel("pbm_acacia_0","models/tree/acacia_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1.png"},0,MIPMAP_TREES);
		acacia.shadowCaster = true;
		PartlyBillboardModel acacia_low = new PartlyBillboardModel("pbm_acacia_1","models/tree/acacia_bb1.obj",new String[]{"3"},new String[]{},new String[]{"acac_1.png"},1,MIPMAP_TREES);
		acacia_low.shadowCaster = true;
		//acacia_low.windAnimation = false;
		PartlyBillboardModel acacia_lowest = new PartlyBillboardModel("pbm_acacia_2","models/tree/acacia_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1.png"},2,MIPMAP_TREES);
		acacia_lowest.windAnimation = false;
		PartlyBillboardModel acacia_lowest_2 = new PartlyBillboardModel("pbm_acacia_3","models/tree/acacia_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1.png"},3,MIPMAP_TREES);
		acacia_lowest_2.windAnimation = false;

		PartlyBillboardModel bush = new PartlyBillboardModel("pbm_bush_0","models/bush/bush1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1.png"},0,MIPMAP_TREES);
		bush.quadXSizeMultiplier = 3.8f;
		bush.quadYSizeMultiplier = 3.2f;
		bush.shadowCaster = true;
		PartlyBillboardModel bush_low = new PartlyBillboardModel("pbm_bush_1","models/bush/bush1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1.png"},1,MIPMAP_TREES);
		bush_low.quadXSizeMultiplier = 3.3f;
		bush_low.quadYSizeMultiplier = 3.0f;
		bush_low.shadowCaster = true;
		PartlyBillboardModel bush_lowest = new PartlyBillboardModel("pbm_bush_2","models/bush/bush1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1_low.png"},2,MIPMAP_TREES);
		PartlyBillboardModel bush_lowest_2 = new PartlyBillboardModel("pbm_bush_3","models/bush/bush1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1_low.png"},3,MIPMAP_TREES);
		bush_lowest.quadXSizeMultiplier = 3.8f;
		bush_lowest.quadYSizeMultiplier = 3.5f;
		bush_lowest.windAnimation = false;
		bush_lowest_2.quadXSizeMultiplier = 3.8f;
		bush_lowest_2.quadYSizeMultiplier = 3.5f;
		bush_lowest_2.windAnimation = false;
		
/*		PartlyBillboardModel fern = new PartlyBillboardModel("models/bush/fern.3ds",new String[]{"3"},new String[]{"2"},new String[]{"fern.png"},0,MIPMAP_TREES);
		fern.shadowCaster = true;
		PartlyBillboardModel fern_low = new PartlyBillboardModel("models/bush/fern.3ds",new String[]{"3"},new String[]{"2"},new String[]{"fern.png"},1,MIPMAP_TREES);
		fern_low.shadowCaster = true;
		PartlyBillboardModel fern_lowest = new PartlyBillboardModel("models/bush/fern.3ds",new String[]{"3"},new String[]{"2"},new String[]{"fern.png"},2,MIPMAP_TREES);
*/
		PartlyBillboardModel pine_high = new PartlyBillboardModel("pbm_pine_0","models/tree/pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_1.png"},0,MIPMAP_TREES);
		pine_high.quadXSizeMultiplier = 3.0f;
		pine_high.quadYSizeMultiplier = 3.5f;
		pine_high.shadowCaster = true;
		PartlyBillboardModel pine_low = new PartlyBillboardModel("pbm_pine_1","models/tree/pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_1.png"},1,MIPMAP_TREES);
		pine_low.quadXSizeMultiplier = 2f;
		pine_low.quadYSizeMultiplier = 3f;
		pine_low.shadowCaster = true;
		PartlyBillboardModel pine_lowest = new PartlyBillboardModel("pbm_pine_2","models/tree/pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_1.png"},2,MIPMAP_TREES);
		pine_lowest.quadXSizeMultiplier = 1.6f;
		pine_lowest.quadYSizeMultiplier = 3f;
		pine_lowest.windAnimation = false;
		PartlyBillboardModel pine_lowest_2 = new PartlyBillboardModel("pbm_pine_3","models/tree/pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_1.png"},3,MIPMAP_TREES);
		pine_lowest_2.quadXSizeMultiplier = 1.6f;
		pine_lowest_2.quadYSizeMultiplier = 3f;
		pine_lowest_2.windAnimation = false;

		PartlyBillboardModel great_pine_high = new PartlyBillboardModel("pbm_great_pine_0","models/tree/great_pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_2.png"},0,MIPMAP_TREES);
		great_pine_high.quadXSizeMultiplier = 2f;
		great_pine_high.quadYSizeMultiplier = 2f;
		great_pine_high.shadowCaster = true;
		PartlyBillboardModel great_pine_low = new PartlyBillboardModel("pbm_great_pine_1","models/tree/great_pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_2.png"},1,MIPMAP_TREES);
		great_pine_low.quadXSizeMultiplier = 2f;
		great_pine_low.quadYSizeMultiplier = 2.5f;
		great_pine_low.shadowCaster = true;
		PartlyBillboardModel great_pine_lowest = new PartlyBillboardModel("pbm_great_pine_2","models/tree/great_pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_2.png"},2,MIPMAP_TREES);
		great_pine_lowest.quadXSizeMultiplier = 1.3f;
		great_pine_lowest.quadYSizeMultiplier = 2.0f;
		great_pine_lowest.windAnimation = false;
		PartlyBillboardModel great_pine_lowest_2 = new PartlyBillboardModel("pbm_great_pine_3","models/tree/great_pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_2.png"},3,MIPMAP_TREES);
		great_pine_lowest_2.quadXSizeMultiplier = 1.3f;
		great_pine_lowest_2.quadYSizeMultiplier = 2.0f;
		great_pine_lowest_2.windAnimation = false;

		PartlyBillboardModel palm_high = new PartlyBillboardModel("pbm_palm_0","models/tree/great_succ_bb1.obj",new String[]{"3"},new String[]{},new String[]{"jung_succ_1.png"},0,MIPMAP_TREES);
		palm_high.quadXSizeMultiplier = 1.5f;
		palm_high.quadYSizeMultiplier = 2f;
		palm_high.shadowCaster = true;
		PartlyBillboardModel palm_low = new PartlyBillboardModel("pbm_palm_1","models/tree/great_succ_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"jung_succ_1.png"},1,MIPMAP_TREES);
		//palm_low.quadXSizeMultiplier = 2f;
		//palm_low.quadYSizeMultiplier = 2.5f;
		palm_low.shadowCaster = true;
		PartlyBillboardModel palm_lowest = new PartlyBillboardModel("pbm_palm_2","models/tree/great_succ_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"jung_succ_1.png"},2,MIPMAP_TREES);
		//palm_lowest.quadXSizeMultiplier = 1.6f;
		palm_lowest.quadXSizeMultiplier = 0.5f;
		palm_lowest.quadYSizeMultiplier = 0.7f;
		palm_lowest.windAnimation = false;
		PartlyBillboardModel palm_lowest_2 = new PartlyBillboardModel("pbm_palm_3","models/tree/great_succ_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"jung_succ_1.png"},3,MIPMAP_TREES);
		palm_lowest_2.quadXSizeMultiplier = 0.5f;
		palm_lowest_2.quadYSizeMultiplier = 0.5f;
		//palm_lowest_2.quadXSizeMultiplier = 1.6f;
		//palm_lowest_2.quadYSizeMultiplier = 2.5f;
		palm_lowest_2.windAnimation = false;

		
		PartlyBillboardModel coconut_high = new PartlyBillboardModel("pbm_coconut_0","models/tree/coconut_bb1.obj",new String[]{"3"},new String[]{},new String[]{"palm2.png"},0,MIPMAP_TREES);
		coconut_high.quadXSizeMultiplier = 4f;
		coconut_high.quadYSizeMultiplier = 4f;
		coconut_high.shadowCaster = true;
		coconut_high.cullNone = true;
		PartlyBillboardModel coconut_low = new PartlyBillboardModel("pbm_coconut_1","models/tree/coconut_bb1.obj",new String[]{"3"},new String[]{},new String[]{"palm2.png"},1,MIPMAP_TREES);
		coconut_low.quadXSizeMultiplier = 3f;
		coconut_low.quadYSizeMultiplier = 6f;
		coconut_low.shadowCaster = true;
		coconut_low.cullNone = true;
		PartlyBillboardModel coconut_lowest = new PartlyBillboardModel("pbm_coconut_2","models/tree/coconut_bb1.obj",new String[]{"3"},new String[]{},new String[]{"palm2.png"},2,MIPMAP_TREES);
		coconut_lowest.quadXSizeMultiplier = 1.8f;
		coconut_lowest.quadYSizeMultiplier = 1.7f;
		coconut_lowest.windAnimation = false;
		coconut_lowest.cullNone = true;
		PartlyBillboardModel coconut_lowest_2 = new PartlyBillboardModel("pbm_coconut_3","models/tree/coconut_bb1.obj",new String[]{"3"},new String[]{},new String[]{"palm2.png"},3,MIPMAP_TREES);
		coconut_lowest_2.quadXSizeMultiplier = 1.8f;
		coconut_lowest_2.quadYSizeMultiplier = 1.7f;
		coconut_lowest_2.windAnimation = false;
		coconut_lowest_2.cullNone = true;

		PartlyBillboardModel jungle_bush = new PartlyBillboardModel("pbm_jungle_bush_0","models/bush/bush1.3ds",new String[]{"3"},new String[]{"2"},new String[]{"palm2a.png"},0,MIPMAP_TREES);
		jungle_bush.quadXSizeMultiplier = 2f;
		jungle_bush.quadYSizeMultiplier = 2f;
		jungle_bush.shadowCaster = true;
		PartlyBillboardModel jungle_bush_low = new PartlyBillboardModel("pbm_jungle_bush_1","models/bush/bush1.3ds",new String[]{"3"},new String[]{"2"},new String[]{"palm2a.png"},1,MIPMAP_TREES);
		jungle_bush_low.quadXSizeMultiplier = 1.6f;
		jungle_bush_low.quadYSizeMultiplier = 1.6f;
		jungle_bush_low.shadowCaster = true;
		PartlyBillboardModel jungle_bush_lowest = new PartlyBillboardModel("pbm_jungle_bush_2","models/bush/bush1.3ds",new String[]{"3"},new String[]{"2"},new String[]{"palm2a.png"},2,MIPMAP_TREES);
		PartlyBillboardModel jungle_bush_lowest_2 = new PartlyBillboardModel("pbm_jungle_bush_3","models/bush/bush1.3ds",new String[]{"3"},new String[]{"2"},new String[]{"palm2a.png"},3,MIPMAP_TREES);
		jungle_bush_lowest.quadXSizeMultiplier = 3.5f;
		jungle_bush_lowest.quadYSizeMultiplier = 3.5f;
		jungle_bush_lowest.windAnimation = false;
		jungle_bush_lowest_2.quadXSizeMultiplier = 3.5f;
		jungle_bush_lowest_2.quadYSizeMultiplier = 3.5f;
		jungle_bush_lowest_2.windAnimation = false;
		
		SimpleModel pine = new SimpleModel("models/tree/pine.3ds",null,MIPMAP_TREES);
		pine.shadowCaster = true; pine.useClodMesh = true;
		SimpleModel great_pine = new SimpleModel("models/tree/great_pine.3ds",null,MIPMAP_TREES);
		great_pine.shadowCaster = true; great_pine.useClodMesh = true;
		SimpleModel palm = new SimpleModel("models/tree/coconut.3ds",null,MIPMAP_TREES);
		palm.shadowCaster = true; palm.useClodMesh = true; palm.cullNone = true;
		SimpleModel jungletrees_mult = new SimpleModel("models/tree/palm.3ds",null,MIPMAP_TREES);
		jungletrees_mult.shadowCaster = true; jungletrees_mult.useClodMesh = true; jungletrees_mult.cullNone = true;
		SimpleModel cactus = new SimpleModel("sides/cactus.obj",null,MIPMAP_TREES);
		cactus.shadowCaster = true; cactus.useClodMesh = false; cactus.cullNone = true;
		SimpleModel bush1 = new SimpleModel("models/bush/bush1.3ds",null,MIPMAP_TREES);
		bush1.shadowCaster = true; bush1.useClodMesh = true;
		SimpleModel fern1 = new SimpleModel("models/bush/fern.3ds",null,MIPMAP_TREES);
		fern1.shadowCaster = true; fern1.useClodMesh = true;

		TREE_LOD_DIST = J3DCore.TREE_LOD_DIST_LOW;
		if (DETAILED_TREES)
		{
			TREE_LOD_DIST = J3DCore.TREE_LOD_DIST_HIGH;
		}

		LODModel lod_cherry = new LODModel("cherry",new SimpleModel[]{cherry,cherry_low,cherry_lowest,cherry_lowest_2},TREE_LOD_DIST);
		lod_cherry.shadowCaster = true;
		LODModel lod_acacia = new LODModel("acacia",new SimpleModel[]{acacia,acacia_low,acacia_lowest,acacia_lowest_2},TREE_LOD_DIST);
		lod_acacia.shadowCaster = true;
		//LODModel lod_pine = new LODModel(new SimpleModel[]{pine},new float[][]{{0f,15f}});
		LODModel lod_pine = new LODModel("pine",new SimpleModel[]{pine_high,pine_high,pine_lowest,pine_lowest_2},TREE_LOD_DIST);
		lod_pine.shadowCaster = true;
		//LODModel lod_great_pine = new LODModel(new SimpleModel[]{great_pine},new float[][]{{0f,15f}});
		LODModel lod_great_pine = new LODModel("great_pine",new SimpleModel[]{great_pine_high,great_pine_high,great_pine_lowest,great_pine_lowest_2},TREE_LOD_DIST);
		lod_great_pine.shadowCaster = true;
		//LODModel lod_palm = new LODModel(new SimpleModel[]{palm},new float[][]{{0f,15f}});
		LODModel lod_palm = new LODModel("palm",new SimpleModel[]{coconut_high,coconut_high,coconut_lowest,coconut_lowest_2},TREE_LOD_DIST);
		lod_palm.shadowCaster = true;
		LODModel lod_jungletrees_mult = new LODModel("jungletrees_mult",new SimpleModel[]{palm_high,palm_high,palm_lowest_2,palm_lowest_2},TREE_LOD_DIST);
		lod_jungletrees_mult.shadowCaster = true;
		LODModel lod_cactus = new LODModel("cactus",new SimpleModel[]{cactus},new float[][]{{0f,15f}});
		lod_cactus.shadowCaster = true;
		LODModel lod_bush1 = new LODModel("bush1",new SimpleModel[]{bush,bush_low,bush_lowest,bush_lowest_2},TREE_LOD_DIST);
		lod_bush1.shadowCaster = true;
		LODModel lod_jungle_bush1 = new LODModel("jungle_bush1",new SimpleModel[]{jungle_bush,jungle_bush_low,jungle_bush_lowest,jungle_bush_lowest_2},TREE_LOD_DIST);
		lod_jungle_bush1.shadowCaster = true;
		LODModel lod_fern = new LODModel("fern",new SimpleModel[]{fern1},new float[][]{{0f,15f}});
		lod_fern.shadowCaster = true;

		TextureStateVegetationModel tsm_red_forest_mushroom = new TextureStateVegetationModel(new String[]{"red_mushroom.png"},0.55f,0.6f,2,1f);
		tsm_red_forest_mushroom.windAnimation = false;
		tsm_red_forest_mushroom.alwaysRenderBatch = true;
		TextureStateVegetationModel tsm_cave_mushroom = new TextureStateVegetationModel(new String[]{"cave_mushroom.png"},0.55f,0.4f,2,1f);
		tsm_cave_mushroom.windAnimation = false;
		tsm_cave_mushroom.alwaysRenderBatch = true;
		TextureStateVegetationModel tsm_grass_anathum = new TextureStateVegetationModel(new String[]{"anathum.png"},0.55f,0.6f,2,1f);
		tsm_cave_mushroom.alwaysRenderBatch = true;

		
		TextureStateVegetationModel tsm_cont_grass = new TextureStateVegetationModel(new String[]{"grass_aard.png"},0.55f,0.4f,3,0.7f);
		TextureStateVegetationModel tsm_cont_grass_flower = new TextureStateVegetationModel(new String[]{"grass1_flower_2.png"},0.7f,0.4f,2,1.0f);
		//,"grass1_flower.png","grass1_flower_2.png"
		LODModel lod_cont_grass_1 = new LODModel("cont_grass_1",new Model[]{tsm_cont_grass},new float[][]{{0f,RENDER_GRASS_DISTANCE}});
		lod_cont_grass_1.rotateOnSteep = true;
		
		TextureStateVegetationModel tsm_jung_grass = new TextureStateVegetationModel(new String[]{"jungle_foliage1.png"},0.5f,0.45f,3,0.7f);
		TextureStateVegetationModel tsm_jung_grass_flower = new TextureStateVegetationModel(new String[]{"jungle_foliage1_flower.png"},0.5f,0.45f,2,1.0f);
		LODModel lod_jung_grass_1 = new LODModel("jung_grass_1",new Model[]{tsm_jung_grass},new float[][]{{0f,RENDER_GRASS_DISTANCE}});
		lod_jung_grass_1.rotateOnSteep = true;

		// 3d type to file mapping		
		SimpleModel wall_thick = new SimpleModel("sides/wall_thick.3ds", null);
		wall_thick.shadowCaster = true;
		wall_thick.cullNone = true;
		wall_thick.farViewEnabled = true;
		SimpleModel wall_window = new SimpleModel("sides/wall_window.3ds", null);
		wall_window.shadowCaster = true;
		wall_window.cullNone = true;
		wall_window.farViewEnabled = true;
		SimpleModel wall_door = new SimpleModel("sides/door.3ds", null);
		wall_door.shadowCaster = true;
		wall_door.cullNone = true;
		wall_door.farViewEnabled = true;
		SimpleModel wall_door_wall = new SimpleModel("sides/wall_door.3ds", null);
		wall_door_wall.shadowCaster = true;
		wall_door_wall.cullNone = true;
		wall_door_wall.farViewEnabled = true;
		SimpleModel roof_side = new SimpleModel("sides/roof_side.3ds", null);
		roof_side.shadowCaster = true;
		roof_side.cullNone = true;
		roof_side.farViewEnabled = true;
		SimpleModel roof_corner = new SimpleModel("sides/roof_corner.3ds", null);
		roof_corner.shadowCaster = true;
		roof_corner.farViewEnabled = true;
		SimpleModel roof_opp = new SimpleModel("sides/roof_corner_opp.3ds", null);
		roof_opp.shadowCaster = true;
		roof_opp.farViewEnabled = true;
		SimpleModel roof_non = new SimpleModel("sides/roof_corner_non.3ds", null);
		roof_non.shadowCaster = true;
		roof_non.farViewEnabled = true;
		SimpleModel ceiling = new SimpleModel("sides/ceiling_pattern1.3ds",null);
		ceiling.shadowCaster = true;
		SimpleModel roof_top = new SimpleModel("sides/roof_top.3ds",null);
		roof_top.shadowCaster = true;
		roof_top.farViewEnabled = true;
		
		SimpleModel cave_rock = new SimpleModel("models/ground/cave_rock.obj", null);
		SimpleModel wall_cave = new SimpleModel("models/ground/wall_cave.obj", null);
		SimpleModel wall_cave_rev = new SimpleModel("models/ground/wall_cave_rev.obj", null);
		SimpleModel ground_cave = new SimpleModel("models/ground/ground_cave.obj", null);
		SimpleModel entrance_cave = new SimpleModel("models/ground/cave_entrance.obj", null);
		//SimpleModel entrance_cave = new SimpleModel("models/ground/ground_cave.obj", null);
		
		hm3dTypeRenderedSide.put(new Integer(1), new RenderedContinuousSide(
				new SimpleModel[]{wall_thick},
				new SimpleModel[]{roof_side},
				new SimpleModel[]{roof_corner},
				new SimpleModel[]{roof_opp},
				new SimpleModel[]{roof_non}
				));
		hm3dTypeRenderedSide.put(new Integer(5), new RenderedContinuousSide(
				new SimpleModel[]{wall_door,wall_door_wall},
				new SimpleModel[]{roof_side},
				new SimpleModel[]{roof_corner},
				new SimpleModel[]{roof_opp},
				new SimpleModel[]{roof_non}
				));
		hm3dTypeRenderedSide.put(new Integer(6), new RenderedContinuousSide(
				new SimpleModel[]{wall_window,new SimpleModel("sides/window1.3ds", null)},
				new SimpleModel[]{roof_side},
				new SimpleModel[]{roof_corner},
				new SimpleModel[]{roof_opp},
				new SimpleModel[]{roof_non}
				));


		hm3dTypeRenderedSide.put(new Integer(7), new RenderedTopSide(
				//new SimpleModel[]{},
				new SimpleModel[]{ceiling},
				new SimpleModel[]{roof_top}
				));
		
		int yCommon = 1;
		
		QuadModel qm_grass = new QuadModel("grass2.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE); qm_grass.rotateOnSteep = true; qm_grass.farViewEnabled = true;
		SimpleModel sm_grass = new SimpleModel("models/ground/ground_1.obj","grass2.jpg"); sm_grass.rotateOnSteep = true; sm_grass.yGeomBatchSize = yCommon; sm_grass.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass.farViewEnabled = true;
		SimpleModel sm_grass_2 = new SimpleModel("models/ground/ground_2.obj","grass2.jpg"); sm_grass_2.rotateOnSteep = true; sm_grass_2.yGeomBatchSize = yCommon; sm_grass_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass_2.farViewEnabled = true;
		SimpleModel sm_grass_3 = new SimpleModel("models/ground/ground_3.obj","grass2.jpg"); sm_grass_3.rotateOnSteep = true; sm_grass_3.yGeomBatchSize = yCommon; sm_grass_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass_3.farViewEnabled = true;
		SimpleModel sm_grass_steep = new SimpleModel("models/ground/ground_steep_1.obj","grass2.jpg"); sm_grass_steep.rotateOnSteep = true; sm_grass_steep.yGeomBatchSize = yCommon; sm_grass_steep.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass_steep.noSpecialSteepRotation = false; sm_grass_steep.farViewEnabled = true;
		SimpleModel sm_grass_steep_2 = new SimpleModel("models/ground/ground_steep_2.obj","grass2.jpg"); sm_grass_steep_2.rotateOnSteep = true; sm_grass_steep_2.yGeomBatchSize = yCommon; sm_grass_steep_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass_steep_2.noSpecialSteepRotation = false; sm_grass_steep_2.farViewEnabled = true;
		SimpleModel sm_grass_steep_3 = new SimpleModel("models/ground/ground_steep_3.obj","grass2.jpg"); sm_grass_steep_3.rotateOnSteep = true; sm_grass_steep_3.yGeomBatchSize = yCommon; sm_grass_steep_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass_steep_3.noSpecialSteepRotation = false; sm_grass_steep_3.farViewEnabled = true;

		QuadModel qm_rock_no_rot = new QuadModel("cave_wall.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE);
		QuadModel qm_cave_wall = new QuadModel("cave_wall.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE);
		
		QuadModel qm_cave_ground = new QuadModel("cave_ground.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE); qm_cave_ground.rotateOnSteep = true; qm_cave_ground.farViewEnabled = true;
		SimpleModel sm_cave_ground = new SimpleModel("models/ground/ground_1.obj","cave_ground.jpg"); sm_cave_ground.yGeomBatchSize = yCommon; sm_cave_ground.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_cave_ground.farViewEnabled = true;
		SimpleModel sm_cave_ground_2 = new SimpleModel("models/ground/ground_2.obj","cave_ground.jpg"); sm_cave_ground_2.yGeomBatchSize = yCommon; sm_cave_ground_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_cave_ground_2.farViewEnabled = true;
		SimpleModel sm_cave_ground_3 = new SimpleModel("models/ground/ground_3.obj","cave_ground.jpg"); sm_cave_ground_3.yGeomBatchSize = yCommon; sm_cave_ground_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_cave_ground_3.farViewEnabled = true;
		
		SimpleModel sm_river_bottom = new SimpleModel("models/ground/ground_2.obj","cave_ground.jpg"); sm_river_bottom.yGeomBatchSize = yCommon; sm_river_bottom.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_river_bottom.rotateOnSteep = true; sm_river_bottom.farViewEnabled = true;
		SimpleModel sm_river_side_norot = new SimpleModel("models/ground/ground_1.obj","cave_ground.jpg"); sm_river_side_norot.yGeomBatchSize = yCommon; sm_river_side_norot.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_river_side_norot.rotateOnSteep = false; sm_river_side_norot.farViewEnabled = true;
		SimpleModel sm_water_rock_side = new SimpleModel("models/ground/water_rock_side.obj","cave_ground.jpg"); sm_river_bottom.yGeomBatchSize = yCommon; sm_river_bottom.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_river_bottom.rotateOnSteep = true; sm_river_bottom.farViewEnabled = true;

		LODModel lod_cave_wall = new LODModel("cave_wall",new Model[]{wall_cave,qm_cave_wall},TREE_LOD_DIST);
		lod_jungle_bush1.shadowCaster = false;
		
		SimpleModel sm_road_stone = new SimpleModel("models/ground/road_stone_1.3ds",null); sm_road_stone.rotateOnSteep = true; sm_road_stone.farViewEnabled = true;
		//QuadModel qm_road_stone = new QuadModel("stone.jpg","stone_bump.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE,true); qm_grass.rotateOnSteep = true;
		QuadModel qm_road_stone = new QuadModel("stone.jpg","NormalMap.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE,false); qm_road_stone.rotateOnSteep = true; qm_road_stone.farViewEnabled = true;

		SimpleModel sm_house_wood = new SimpleModel("models/ground/house_wood.3ds",null); sm_house_wood.rotateOnSteep = true; sm_house_wood.farViewEnabled = true;
		QuadModel qm_house_wood = new QuadModel("grndwnot.jpg"); qm_house_wood.rotateOnSteep = true; qm_house_wood.rotateOnSteep = true; qm_house_wood.farViewEnabled = true;

		QuadModel qm_desert = new QuadModel("sand2.jpg"); qm_desert.rotateOnSteep = true; qm_desert.farViewEnabled = true;
		SimpleModel sm_desert = new SimpleModel("models/ground/ground_1.obj","sand2.jpg"); sm_desert.rotateOnSteep = true; sm_desert.yGeomBatchSize = yCommon; sm_desert.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert.farViewEnabled = true;
		SimpleModel sm_desert_2 = new SimpleModel("models/ground/ground_2.obj","sand2.jpg"); sm_desert_2.rotateOnSteep = true; sm_desert_2.yGeomBatchSize = yCommon; sm_desert_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert_2.farViewEnabled = true;
		SimpleModel sm_desert_3 = new SimpleModel("models/ground/ground_3.obj","sand2.jpg"); sm_desert_3.rotateOnSteep = true; sm_desert_3.yGeomBatchSize = yCommon; sm_desert_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert_3.farViewEnabled = true;
		SimpleModel sm_desert_steep = new SimpleModel("models/ground/ground_steep_1.obj","sand2.jpg"); sm_desert_steep.rotateOnSteep = true; sm_desert_steep.yGeomBatchSize = yCommon; sm_desert_steep.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert_steep.noSpecialSteepRotation = false; sm_desert_steep.farViewEnabled = true;
		SimpleModel sm_desert_steep_2 = new SimpleModel("models/ground/ground_steep_2.obj","sand2.jpg"); sm_desert_steep_2.rotateOnSteep = true; sm_desert_steep_2.yGeomBatchSize = yCommon; sm_desert_steep_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert_steep_2.noSpecialSteepRotation = false; sm_desert_steep_2.farViewEnabled = true;
		SimpleModel sm_desert_steep_3 = new SimpleModel("models/ground/ground_steep_3.obj","sand2.jpg"); sm_desert_steep_3.rotateOnSteep = true; sm_desert_steep_3.yGeomBatchSize = yCommon; sm_desert_steep_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert_steep_3.noSpecialSteepRotation = false; sm_desert_steep_3.farViewEnabled = true;
		
		QuadModel qm_arctic = new QuadModel("snow1.jpg"); qm_arctic.rotateOnSteep = true; qm_arctic.farViewEnabled = true;
		SimpleModel sm_arctic = new SimpleModel("models/ground/ground_1.obj","snow1.jpg"); sm_arctic.rotateOnSteep = true; sm_arctic.yGeomBatchSize = yCommon; sm_arctic.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_arctic.farViewEnabled = true;
		SimpleModel sm_arctic_2 = new SimpleModel("models/ground/ground_2.obj","snow1.jpg"); sm_arctic_2.rotateOnSteep = true; sm_arctic_2.yGeomBatchSize = yCommon; sm_arctic_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_arctic_2.farViewEnabled = true;
		SimpleModel sm_arctic_3 = new SimpleModel("models/ground/ground_3.obj","snow1.jpg"); sm_arctic_3.rotateOnSteep = true; sm_arctic_3.yGeomBatchSize = yCommon; sm_arctic_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_arctic_3.farViewEnabled = true;
		SimpleModel sm_arctic_steep = new SimpleModel("models/ground/ground_steep_1.obj","snow1.jpg"); sm_arctic_steep.rotateOnSteep = true; sm_arctic_steep.yGeomBatchSize = yCommon; sm_arctic_steep.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_arctic_steep.noSpecialSteepRotation = false; sm_arctic_steep.farViewEnabled = true;
		SimpleModel sm_arctic_steep_2 = new SimpleModel("models/ground/ground_steep_2.obj","snow1.jpg"); sm_arctic_steep_2.rotateOnSteep = true; sm_arctic_steep_2.yGeomBatchSize = yCommon; sm_arctic_steep_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_arctic_steep_2.noSpecialSteepRotation = false; sm_arctic_steep_2.farViewEnabled = true;
		SimpleModel sm_arctic_steep_3 = new SimpleModel("models/ground/ground_steep_3.obj","snow1.jpg"); sm_arctic_steep_3.rotateOnSteep = true; sm_arctic_steep_3.yGeomBatchSize = yCommon; sm_arctic_steep_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_arctic_steep_3.noSpecialSteepRotation = false; sm_arctic_steep_3.farViewEnabled = true;
		
		QuadModel qm_jungle = new QuadModel("jungle.jpg"); qm_jungle.rotateOnSteep = true; qm_jungle.farViewEnabled = true;
		SimpleModel sm_jungle = new SimpleModel("models/ground/ground_1.obj","jungle.jpg"); sm_jungle.rotateOnSteep = true; sm_jungle.yGeomBatchSize = yCommon; sm_jungle.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle.farViewEnabled = true;
		SimpleModel sm_jungle_2 = new SimpleModel("models/ground/ground_2.obj","jungle.jpg"); sm_jungle_2.rotateOnSteep = true; sm_jungle_2.yGeomBatchSize = yCommon; sm_jungle_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle_2.farViewEnabled = true;
		SimpleModel sm_jungle_3 = new SimpleModel("models/ground/ground_3.obj","jungle.jpg"); sm_jungle_3.rotateOnSteep = true; sm_jungle_3.yGeomBatchSize = yCommon; sm_jungle_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle_3.farViewEnabled = true;
		SimpleModel sm_jungle_steep = new SimpleModel("models/ground/ground_steep_1.obj","jungle.jpg"); sm_jungle_steep.rotateOnSteep = true; sm_jungle_steep.yGeomBatchSize = yCommon; sm_jungle_steep.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle_steep.noSpecialSteepRotation = false; sm_jungle_steep.farViewEnabled = true;
		SimpleModel sm_jungle_steep_2 = new SimpleModel("models/ground/ground_steep_2.obj","jungle.jpg"); sm_jungle_steep_2.rotateOnSteep = true; sm_jungle_steep_2.yGeomBatchSize = yCommon; sm_jungle_steep_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle_steep_2.noSpecialSteepRotation = false; sm_jungle_steep_2.farViewEnabled = true;
		SimpleModel sm_jungle_steep_3 = new SimpleModel("models/ground/ground_steep_3.obj","jungle.jpg"); sm_jungle_steep_3.rotateOnSteep = true; sm_jungle_steep_3.yGeomBatchSize = yCommon; sm_jungle_steep_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle_steep_3.noSpecialSteepRotation = false; sm_jungle_steep_3.farViewEnabled = true;

		SimpleModel sm_female = new SimpleModel("models/fauna/fem.obj",null); sm_female.rotateOnSteep = true;
		sm_female.cullNone = true; sm_female.batchEnabled = false;
		SimpleModel sm_male = new SimpleModel("models/fauna/male.obj",null); sm_male.rotateOnSteep = true;
		sm_male.cullNone = true; sm_male.batchEnabled = false;
		SimpleModel sm_wolf = new SimpleModel("models/fauna/wolf.obj",null); sm_wolf.rotateOnSteep = true;
		sm_wolf.cullNone = true; sm_wolf.batchEnabled = false;
		SimpleModel sm_warthog = new SimpleModel("models/fauna/warthog_model.obj",null); sm_warthog.rotateOnSteep = true;
		sm_warthog.cullNone = true; sm_warthog.batchEnabled = false;
		SimpleModel sm_fox = new SimpleModel("models/fauna/redfox.obj",null); sm_fox.rotateOnSteep = true;
		sm_fox.cullNone = true; sm_fox.batchEnabled = false;
		SimpleModel sm_gorilla = new SimpleModel("models/fauna/gorilla_texture.obj",null); sm_gorilla.rotateOnSteep = true;
		sm_gorilla.cullNone = true; sm_gorilla.batchEnabled = false; sm_gorilla.shadowCaster = true;
		
		if (RENDER_GRASS_DISTANCE>0) 
		{
			if (BUMPED_GROUND)
				hm3dTypeRenderedSide.put(new Integer(2), new RenderedHashAlteredSide(new Model[]{tsm_cont_grass,tsm_cont_grass_flower}, new Model[][]{{sm_grass,sm_grass_2,sm_grass_3,sm_grass_3,sm_grass_3}}, new Model[][]{{sm_grass_steep,sm_grass_steep_2,sm_grass_steep_3,sm_grass_steep_3}}));//lod_cont_grass_1}));
			else
				hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide(new Model[]{qm_grass,tsm_cont_grass,tsm_cont_grass_flower}));//lod_cont_grass_1}));
		} else
		{
			if (BUMPED_GROUND)
				hm3dTypeRenderedSide.put(new Integer(2), new RenderedHashAlteredSide(new Model[]{}, new Model[][]{{sm_grass,sm_grass_2,sm_grass_3,sm_grass_3,sm_grass_3}}, new Model[][]{{sm_grass_steep,sm_grass_steep_2,sm_grass_steep_3,sm_grass_steep_3}}));//lod_cont_grass_1}));
			else
				hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide(new Model[]{qm_grass}));
		}
		
		hm3dTypeRenderedSide.put(new Integer(3), new RenderedSide(new Model[]{qm_road_stone}));
		//hm3dTypeRenderedSide.put(new Integer(3), new RenderedHashAlteredSide(new Model[]{qm_road_stone}, new Model[][]{{sm_wolf,sm_warthog,sm_gorilla,sm_fox}}));
		hm3dTypeRenderedSide.put(new Integer(29), new RenderedSide(new Model[]{qm_house_wood}));
		hm3dTypeRenderedSide.put(new Integer(4), new RenderedSide("sides/ceiling_pattern1.3ds",null));
		//hm3dTypeRenderedSide.put(new Integer(16), new RenderedSide(new Model[]{sm_desert}));
		if (BUMPED_GROUND) 
		{
			hm3dTypeRenderedSide.put(new Integer(16), new RenderedHashAlteredSide(new Model[]{},new Model[][]{{sm_desert,sm_desert_2,sm_desert_3,sm_desert_3,sm_desert_3}},new Model[][]{{sm_desert_steep,sm_desert_steep_2,sm_desert_steep_3,sm_desert_steep_3}}));
		} else
		{
			hm3dTypeRenderedSide.put(new Integer(16), new RenderedSide(new Model[]{qm_desert}));
		}
		if (BUMPED_GROUND) 
		{
			hm3dTypeRenderedSide.put(new Integer(17), new RenderedHashAlteredSide(new Model[]{},new Model[][]{{sm_arctic,sm_arctic_2,sm_arctic_3,sm_arctic_3,sm_arctic_3}},new Model[][]{{sm_arctic_steep,sm_arctic_steep_2,sm_arctic_steep_3,sm_arctic_steep_3}}));
		} else
		{
			hm3dTypeRenderedSide.put(new Integer(17), new RenderedSide(new Model[]{qm_arctic}));
		}
		hm3dTypeRenderedSide.put(new Integer(21), new RenderedSide(new Model[]{sm_grass}));
		hm3dTypeRenderedSide.put(new Integer(35), new RenderedSide(new Model[]{qm_rock_no_rot}));
		
		if (RENDER_GRASS_DISTANCE>0) 
		{
			if (BUMPED_GROUND)
				hm3dTypeRenderedSide.put(new Integer(22), new RenderedHashAlteredSide(new Model[]{tsm_jung_grass, tsm_jung_grass_flower}, new Model[][]{{sm_jungle,sm_jungle_2,sm_jungle_3,sm_jungle_3,sm_jungle_3}},new Model[][]{{sm_jungle_steep,sm_jungle_steep_2,sm_jungle_steep_3,sm_jungle_steep_3}}));//lod_cont_grass_1}));
			else
				hm3dTypeRenderedSide.put(new Integer(22), new RenderedSide(new Model[]{qm_jungle,tsm_jung_grass, tsm_jung_grass_flower}));
		}
		else 
		{
			if (BUMPED_GROUND)
				hm3dTypeRenderedSide.put(new Integer(22), new RenderedHashAlteredSide(new Model[]{}, new Model[][]{{sm_jungle,sm_jungle_2,sm_jungle_3,sm_jungle_3,sm_jungle_3}},new Model[][]{{sm_jungle_steep,sm_jungle_steep_2,sm_jungle_steep_3,sm_jungle_steep_3}}));//lod_cont_grass_1}));
			else
				hm3dTypeRenderedSide.put(new Integer(22), new RenderedSide(new Model[]{qm_jungle}));
		}
		
		hm3dTypeRenderedSide.put(new Integer(8), new RenderedSide("sides/fence.3ds",null));
		
		
		// lod vegetations
		hm3dTypeRenderedSide.put(new Integer(9), new RenderedHashRotatedSide(new Model[]{lod_cherry})); // oak TODO!
		hm3dTypeRenderedSide.put(new Integer(12), new RenderedHashRotatedSide(new Model[]{lod_cherry}));
		hm3dTypeRenderedSide.put(new Integer(15), new RenderedHashRotatedSide(new Model[]{lod_palm}));
		hm3dTypeRenderedSide.put(new Integer(18), new RenderedHashRotatedSide(new Model[]{lod_pine}));
		hm3dTypeRenderedSide.put(new Integer(19), new RenderedHashRotatedSide(new Model[]{lod_bush1})); 
		hm3dTypeRenderedSide.put(new Integer(20), new RenderedHashRotatedSide(new Model[]{lod_acacia}));
		hm3dTypeRenderedSide.put(new Integer(23), new RenderedHashRotatedSide(new Model[]{lod_cactus}));
		hm3dTypeRenderedSide.put(new Integer(24), new RenderedHashRotatedSide(new Model[]{lod_jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(25), new RenderedHashRotatedSide(new Model[]{lod_great_pine}));
		hm3dTypeRenderedSide.put(new Integer(26), new RenderedHashRotatedSide(new Model[]{lod_fern}));
		hm3dTypeRenderedSide.put(new Integer(30), new RenderedHashRotatedSide(new Model[]{lod_jungle_bush1}));


		if (!LOD_VEGETATION)
		{
			// no lod version
			hm3dTypeRenderedSide.put(new Integer(9), new RenderedHashRotatedSide(new Model[]{cherry})); // oak TODO!
			hm3dTypeRenderedSide.put(new Integer(12), new RenderedHashRotatedSide(new Model[]{cherry}));
			hm3dTypeRenderedSide.put(new Integer(15), new RenderedHashRotatedSide(new Model[]{coconut_high}));
			hm3dTypeRenderedSide.put(new Integer(18), new RenderedHashRotatedSide(new Model[]{pine_high}));
			hm3dTypeRenderedSide.put(new Integer(19), new RenderedHashRotatedSide(new Model[]{bush})); 
			hm3dTypeRenderedSide.put(new Integer(20), new RenderedHashRotatedSide(new Model[]{acacia}));
			hm3dTypeRenderedSide.put(new Integer(23), new RenderedHashRotatedSide(new Model[]{cactus}));
			hm3dTypeRenderedSide.put(new Integer(24), new RenderedHashRotatedSide(new Model[]{palm_high}));
			hm3dTypeRenderedSide.put(new Integer(25), new RenderedHashRotatedSide(new Model[]{great_pine_high}));
			hm3dTypeRenderedSide.put(new Integer(26), new RenderedHashRotatedSide(new Model[]{fern1}));
			hm3dTypeRenderedSide.put(new Integer(30), new RenderedHashRotatedSide(new Model[]{jungle_bush}));
		}
		
		/*hm3dTypeRenderedSide.put(new Integer(9), new RenderedHashRotatedSide(new Model[]{jungletrees_mult})); // oak TODO!
		hm3dTypeRenderedSide.put(new Integer(12), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(15), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(18), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(19), new RenderedHashRotatedSide(new Model[]{bush1})); 
		hm3dTypeRenderedSide.put(new Integer(20), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(23), new RenderedHashRotatedSide(new Model[]{cactus}));
		hm3dTypeRenderedSide.put(new Integer(24), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(25), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(26), new RenderedHashRotatedSide(new Model[]{fern1}));
		hm3dTypeRenderedSide.put(new Integer(30), new RenderedHashRotatedSide(new Model[]{bush1}));*/
		
		QuadModel qm_water = new QuadModel("water1.jpg"); qm_water.rotateOnSteep = true; qm_water.waterQuad = true; qm_water.farViewEnabled = true;
		QuadModel qm_waterfall = new QuadModel("water_fall1.jpg"); qm_waterfall.rotateOnSteep = true; qm_waterfall.waterQuad = false; qm_waterfall.farViewEnabled = true;
		hm3dTypeRenderedSide.put(new Integer(10), new RenderedSide(new Model[]{qm_water}));
		//hm3dTypeRenderedSide.put(new Integer(11), new RenderedSide("models/ground/hill_side.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(13), new RenderedSide("models/ground/mountain_rock.obj",null));
		
		// climate dependent
		SimpleModel sm_intersect = new SimpleModel("models/ground/hillintersect.obj",null); sm_intersect.farViewEnabled = true;
		SimpleModel sm_intersect_desert = new SimpleModel("models/ground/hillintersect.obj","sand2.jpg"); sm_intersect_desert.farViewEnabled = true;
		SimpleModel sm_intersect_continental = new SimpleModel("models/ground/hillintersect.obj","grass2.jpg"); sm_intersect_continental.farViewEnabled = true;
		SimpleModel sm_intersect_arctic = new SimpleModel("models/ground/hillintersect.obj","snow1.jpg"); sm_intersect_arctic.farViewEnabled = true;
		SimpleModel sm_intersect_tropical = new SimpleModel("models/ground/hillintersect.obj","jungle.jpg"); sm_intersect_tropical.farViewEnabled = true;
		HashMap<String, Model[]> dependentModels = new HashMap<String, Model[]>();
		dependentModels.put(Desert.DESERT_ID, new Model[]{sm_intersect_desert});
		dependentModels.put(Continental.CONTINENTAL_ID, new Model[]{sm_intersect_continental});
		dependentModels.put(Arctic.ARCTIC_ID, new Model[]{sm_intersect_arctic});
		dependentModels.put(Tropical.TROPICAL_ID, new Model[]{sm_intersect_tropical});
		hm3dTypeRenderedSide.put(new Integer(27), new RenderedClimateDependentSide(new Model[]{}, new Model[]{sm_intersect},dependentModels));
		
		SimpleModel sm_bookcase = new SimpleModel("models/inside/furniture/bookcase.3ds",null);
		sm_bookcase.batchEnabled = false;
		hm3dTypeRenderedSide.put(new Integer(28), new RenderedSide(new Model[]{sm_bookcase}));
		
		// climate dependent
		SimpleModel sm_rockcorner = new SimpleModel("models/ground/rockcorner.obj",null); sm_rockcorner.farViewEnabled = true;
		SimpleModel sm_rockcorner_desert = new SimpleModel("models/ground/rockcorner.obj","sand2.jpg"); sm_rockcorner_desert.farViewEnabled = true;
		SimpleModel sm_rockcorner_continental = new SimpleModel("models/ground/rockcorner.obj","grass2.jpg"); sm_rockcorner_continental.farViewEnabled = true;
		SimpleModel sm_rockcorner_arctic = new SimpleModel("models/ground/rockcorner.obj","snow1.jpg"); sm_rockcorner_arctic.farViewEnabled = true;
		SimpleModel sm_rockcorner_jungle = new SimpleModel("models/ground/rockcorner.obj","jungle.jpg"); sm_rockcorner_jungle.farViewEnabled = true;
		dependentModels = new HashMap<String, Model[]>();
		dependentModels.put(Desert.DESERT_ID, new Model[]{sm_rockcorner_desert});
		dependentModels.put(Continental.CONTINENTAL_ID, new Model[]{sm_rockcorner_continental});
		dependentModels.put(Arctic.ARCTIC_ID, new Model[]{sm_rockcorner_arctic});
		dependentModels.put(Tropical.TROPICAL_ID, new Model[]{sm_rockcorner_jungle});
		hm3dTypeRenderedSide.put(new Integer(40), new RenderedClimateDependentSide(new Model[]{}, new Model[]{sm_rockcorner},dependentModels));
		
		SimpleModel sm_rocksteep = new SimpleModel("models/ground/rocksteep.obj",null); sm_rocksteep.farViewEnabled = true;
		SimpleModel sm_rocksteep_desert = new SimpleModel("models/ground/rocksteep.obj","sand2.jpg"); sm_rocksteep_desert.farViewEnabled = true;
		SimpleModel sm_rocksteep_continental = new SimpleModel("models/ground/rocksteep.obj","grass2.jpg"); sm_rocksteep_continental.farViewEnabled = true;
		SimpleModel sm_rocksteep_arctic = new SimpleModel("models/ground/rocksteep.obj","snow1.jpg"); sm_rocksteep_arctic.farViewEnabled = true;
		SimpleModel sm_rocksteep_jungle = new SimpleModel("models/ground/rocksteep.obj","jungle.jpg"); sm_rocksteep_jungle.farViewEnabled = true;
		dependentModels = new HashMap<String, Model[]>();
		dependentModels.put(Desert.DESERT_ID, new Model[]{sm_rocksteep_desert});
		dependentModels.put(Continental.CONTINENTAL_ID, new Model[]{sm_rocksteep_continental});
		dependentModels.put(Arctic.ARCTIC_ID, new Model[]{sm_rocksteep_arctic});
		dependentModels.put(Tropical.TROPICAL_ID, new Model[]{sm_rocksteep_jungle});
		hm3dTypeRenderedSide.put(new Integer(41), new RenderedClimateDependentSide(new Model[]{}, new Model[]{sm_rocksteep},dependentModels));
		
		hm3dTypeRenderedSide.put(new Integer(31), new RenderedHashAlteredSide(new Model[]{},new Model[][]{{sm_cave_ground,sm_cave_ground_2,sm_cave_ground_3,sm_cave_ground_3,sm_cave_ground_3}}));//ground_cave}));
		//hm3dTypeRenderedSide.put(new Integer(32), new RenderedSide(new Model[]{qm_cave_wall}));
		hm3dTypeRenderedSide.put(new Integer(32), new RenderedSide(new Model[]{wall_cave}));//lod_cave_wall}));
		hm3dTypeRenderedSide.put(new Integer(33), new RenderedSide(new Model[]{entrance_cave}));
		hm3dTypeRenderedSide.put(new Integer(34), new RenderedHashRotatedSide(new Model[]{cave_rock},true));
		hm3dTypeRenderedSide.put(new Integer(35), new RenderedSide(new Model[]{wall_cave_rev}));//lod_cave_wall}));*/
		hm3dTypeRenderedSide.put(new Integer(36), new RenderedSide(new Model[]{qm_waterfall}));
		hm3dTypeRenderedSide.put(new Integer(37), new RenderedSide(new Model[]{sm_river_side_norot}));
		hm3dTypeRenderedSide.put(new Integer(38), new RenderedSide(new Model[]{sm_river_bottom}));
		hm3dTypeRenderedSide.put(new Integer(39), new RenderedSide(new Model[]{sm_water_rock_side}));

		// climate dependent
		SimpleModel sm_rock_downside = new SimpleModel("models/ground/ground_farview.obj",null); sm_rock_downside.farViewEnabled = true;
		SimpleModel sm_rock_downside_desert = new SimpleModel("models/ground/ground_farview.obj","sand2.jpg"); sm_rock_downside_desert.farViewEnabled = true;
		SimpleModel sm_rock_downside_continental = new SimpleModel("models/ground/ground_farview.obj","grass2.jpg"); sm_rock_downside_continental.farViewEnabled = true;
		SimpleModel sm_rock_downside_arctic = new SimpleModel("models/ground/ground_farview.obj","snow1.jpg"); sm_rock_downside_arctic.farViewEnabled = true;
		SimpleModel sm_rock_downside_tropical = new SimpleModel("models/ground/ground_farview.obj","jungle.jpg"); sm_rock_downside_tropical.farViewEnabled = true;
		dependentModels = new HashMap<String, Model[]>();
		dependentModels.put(Desert.DESERT_ID, new Model[]{sm_rock_downside_desert});
		dependentModels.put(Continental.CONTINENTAL_ID, new Model[]{sm_rock_downside_continental});
		dependentModels.put(Arctic.ARCTIC_ID, new Model[]{sm_rock_downside_arctic});
		dependentModels.put(Tropical.TROPICAL_ID, new Model[]{sm_rock_downside_tropical});
		hm3dTypeRenderedSide.put(new Integer(42), new RenderedClimateDependentSide(new Model[]{}, new Model[]{sm_rock_downside},dependentModels));

		// mushrooms
		hm3dTypeRenderedSide.put(new Integer(43), new RenderedSide(new Model[]{tsm_red_forest_mushroom}));
		hm3dTypeRenderedSide.put(new Integer(44), new RenderedSide(new Model[]{tsm_cave_mushroom}));

		hm3dTypeRenderedSide.put(new Integer(45), new RenderedSide(new Model[]{tsm_grass_anathum}));
// NEXT ID = 45
		
	}
	
	
}
