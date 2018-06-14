package net.there.tutorial.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.there.tutorial.entity.EntityJelly;
import net.there.tutorial.model.ModelJelly;

@SideOnly(Side.CLIENT)
public class RenderJelly extends RenderLiving<EntityJelly>
{
    private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation("tutorial:textures/entity/jelly_entity.png");

    public RenderJelly(RenderManager p_i47193_1_)
    {
        super(p_i47193_1_, new ModelJelly(), 0.25f);
        //this.addLayer(new LayerSlimeGel(this));
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityJelly entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.shadowSize = 0.25F * (float)entity.getSlimeSize();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(EntityJelly entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.999F;
        GlStateManager.scale(0.999F, 0.999F, 0.999F);
        float f1 = (float)entitylivingbaseIn.getSlimeSize();
        float f2 = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GlStateManager.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityJelly entity)
    {
        return SLIME_TEXTURES;
    }

    
}