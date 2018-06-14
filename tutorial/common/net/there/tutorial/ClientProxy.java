package net.there.tutorial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.there.tutorial.entity.EntityRegistry;
import net.there.tutorial.init.ModBlocks;
import net.there.tutorial.init.ModItems;
import net.there.tutorial.render.RenderRegistry;

public class ClientProxy extends CommonProxy{
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		EntityRegistry.registerEntities();
		RenderRegistry.registerEntityRenderers();
		//RenderingRegistry.registerEntityRenderingHandler(EntityJellyDart.class, new EntityJellyDartFactory());
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		ModItems.initClient(mesher);
		ModBlocks.initClient(mesher);
		

	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Tutorial.MOD_ID + ":" + id, "inventory"));
	}
}
