package net.there.tutorial;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.there.tutorial.init.ModBlocks;
import net.there.tutorial.init.ModItems;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		
		ModBlocks.init();
		ModItems.init();
	}
	
	public void init(FMLInitializationEvent event) {
		
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public void registerItemRenderer(Item item, int meta, String id) {
		
	}
}
