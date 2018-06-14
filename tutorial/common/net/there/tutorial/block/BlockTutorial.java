package net.there.tutorial.block;

import net.minecraft.block.material.Material;
import net.there.tutorial.Tutorial;
import net.there.tutorial.lib.Names;

public class BlockTutorial extends BlockBase {
	
	public BlockTutorial() {
		super();
	}
	
	public BlockTutorial(String name) {
		super(Material.GLASS, name);
	}
	
	@Override
	public String getUnlocalizedName() {
		return "tile." + Tutorial.RESOURCE_PREFIX + Names.TUTORIAL_BLOCK;//tile.tutorial.tutorial_block
	}
}
