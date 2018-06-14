package net.there.tutorial.entity;

import net.minecraft.util.ResourceLocation;
import net.there.tutorial.Tutorial;

public class EntityRegistry {
	public static void registerEntities() {
		net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(new ResourceLocation(Tutorial.MOD_ID + ":jelly_entity"), EntityJelly.class, "Jelly", -2, Tutorial.instance, 64, 1, true, 0x670072, 0xC117A5);
		net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(new ResourceLocation(Tutorial.MOD_ID + ":jelly_dart_entity"), EntityJellyDart.class, "JellyDart", -2, Tutorial.instance, 64, 1, true);
		}
}
