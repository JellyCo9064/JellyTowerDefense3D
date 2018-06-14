package net.there.tutorial.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.there.tutorial.entity.EntityJelly;
import net.there.tutorial.entity.EntityJellyDart;
import net.there.tutorial.item.ItemJellyDart;

public class RenderRegistry {
	
	public static void registerEntityRenderers() {
		
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(EntityJellyDart.class, new IRenderFactory<EntityJellyDart>() {

			@Override
			public Render<? super EntityJellyDart> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderSnowball(manager, new ItemJellyDart(), Minecraft.getMinecraft().getRenderItem());
			}
			
		});	
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(EntityJelly.class, new IRenderFactory<EntityJelly>() {

			@Override
			public Render<? super EntityJelly> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderJelly(manager);
			}
			
		});
	}
}

