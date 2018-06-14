package net.there.tutorial;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.there.tutorial.entity.EntityRegistry;
import net.there.tutorial.init.ModBlocks;
import net.there.tutorial.init.ModItems;
import net.there.tutorial.render.RenderRegistry;

@Mod(modid = Tutorial.MOD_ID, name = Tutorial.MOD_NAME, version = Tutorial.VERSION, dependencies = Tutorial.DEPENDENCIES)
public class Tutorial {
	
	public static final String MOD_ID = "tutorial";
	public static final String MOD_NAME = "My Tutorial";
	public static final String VERSION = "@VERSION@";
	public static final String DEPENDENCIES = "required-after:forge@[13.23.3.2655,)";
	public static final String RESOURCE_PREFIX = MOD_ID.toLowerCase() + ":";
	
	public static Random random = new Random();
	
	@Instance(MOD_ID)
	public static Tutorial instance;
	
	@SidedProxy(clientSide = "net.there.tutorial.ClientProxy", serverSide = "net.there.tutorial.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	
	@Mod.EventBusSubscriber
	public static class RegistrationHandler{
		
		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event) {
			ModBlocks.register(event.getRegistry());
		}
		
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event){
			ModItems.register(event.getRegistry());
			ModBlocks.registerItemBlocks(event.getRegistry());
		}
	}
}
