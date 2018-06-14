package net.there.tutorial.init;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.there.tutorial.Tutorial;
import net.there.tutorial.item.ItemBase;
import net.there.tutorial.item.ItemJellyDart;
import net.there.tutorial.lib.Names;

public class ModItems {
	
	public static ItemBase jellyDartItem;
	
	public static void init() {
		jellyDartItem = new ItemJellyDart().setCreativeTab(CreativeTabs.MATERIALS);
	}
	
	public static void register(IForgeRegistry<Item> registry) {
		registry.registerAll(
				jellyDartItem
		);	
	}
	
	public static void registerModels() {
		jellyDartItem.registerItemModel();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient(ItemModelMesher mesher) {
		ModelResourceLocation model = new ModelResourceLocation(Tutorial.RESOURCE_PREFIX + Names.JELLY_DART_ITEM, "inventory");
		ModelLoader.registerItemVariants(jellyDartItem, model);
		mesher.register(jellyDartItem, 0, model);
	}

}
