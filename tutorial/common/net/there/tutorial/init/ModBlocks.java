package net.there.tutorial.init;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.there.tutorial.Tutorial;
import net.there.tutorial.block.BlockBase;
import net.there.tutorial.block.BlockTutorial;
import net.there.tutorial.lib.Names;

public class ModBlocks {
	
	public static BlockBase tutorialBlock;
	
	public static void init() {
		tutorialBlock = new BlockTutorial(Names.TUTORIAL_BLOCK).setCreativeTab(CreativeTabs.MATERIALS);
	}
	
	public static void register(IForgeRegistry<Block> registry){
		registry.registerAll(
				tutorialBlock
		);
	}
	
	public static void registerItemBlocks(IForgeRegistry<Item> registry) {
		registry.registerAll(
				tutorialBlock.createItemBlock()
		);
	}
	
	public static void registerModels() {
		tutorialBlock.registerItemModel(Item.getItemFromBlock(tutorialBlock));
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient(ItemModelMesher mesher) {
		
		Item item = Item.getItemFromBlock(tutorialBlock);
		ModelResourceLocation model = new ModelResourceLocation(Tutorial.RESOURCE_PREFIX + Names.TUTORIAL_BLOCK, "inventory");
		ModelLoader.registerItemVariants(item, model);
		mesher.register(item, 0, model);
	}

}
