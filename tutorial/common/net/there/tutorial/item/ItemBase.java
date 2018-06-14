package net.there.tutorial.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.there.tutorial.Tutorial;

public class ItemBase extends Item {
	
	protected String name;
	
	public ItemBase() {
		name = "_item";
	}
	
	public ItemBase(String name){
		
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(name);		
	}
	
	public void registerItemModel() {
		Tutorial.proxy.registerItemRenderer(this, 0, name);
		System.out.println("Registered Item Model");
	}
	
	@Override
	public ItemBase setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}
}
