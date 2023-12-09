package com.epicdragonfight.mixin;

import com.epicdragonfight.client.DragonFightRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.LightningRenderHelper;
import yesman.epicfight.client.renderer.patched.entity.PEnderDragonRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;

@OnlyIn(Dist.CLIENT)
@Mixin(value = PEnderDragonRenderer.class,remap = false)
public abstract class MixinPEnderDragonRenderer extends PatchedEntityRenderer<EnderDragon, EnderDragonPatch, EnderDragonRenderer> {

    @Shadow protected abstract int getOverlayCoord(EnderDragon entity, EnderDragonPatch entitypatch, float partialTicks);

    @Shadow @Final private static ResourceLocation DRAGON_LOCATION;
    @Shadow @Final private static ResourceLocation DRAGON_EXPLODING_LOCATION;
    private static final ResourceLocation DRAGON_EYE_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");


    @Overwrite
    public void render(EnderDragon entityIn, EnderDragonPatch entitypatch, EnderDragonRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
        ClientModel model = (ClientModel)entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT);
        Armature armature = model.getArmature();
        poseStack.pushPose();
        this.mulPoseStack(poseStack, armature, entityIn, entitypatch, partialTicks);
        OpenMatrix4f[] poses = this.getPoseMatrices(entitypatch, armature, partialTicks);
        float deathTimeProgression;
        VertexConsumer lightningBuffer;
        if (entityIn.dragonDeathTime > 0) {
            poseStack.translate(entityIn.getRandom().nextGaussian() * 0.08, 0.0, entityIn.getRandom().nextGaussian() * 0.08);
            deathTimeProgression = ((float)entityIn.dragonDeathTime + partialTicks) / 200.0F;
            lightningBuffer = buffer.getBuffer(EpicFightRenderTypes.dragonExplosionAlphaTriangles(DRAGON_EXPLODING_LOCATION));
            model.drawAnimatedModel(poseStack, lightningBuffer, packedLight, 1.0F, 1.0F, 1.0F, deathTimeProgression, OverlayTexture.NO_OVERLAY, poses);
            VertexConsumer builder2 = buffer.getBuffer(EpicFightRenderTypes.entityDecalTriangles(DRAGON_LOCATION));
            model.drawAnimatedModel(poseStack, builder2, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, this.getOverlayCoord(entityIn, entitypatch, partialTicks), poses);
        } else {
            VertexConsumer builder = buffer.getBuffer(EpicFightRenderTypes.animatedModel(DRAGON_LOCATION));
            model.drawAnimatedModel(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, this.getOverlayCoord(entityIn, entitypatch, partialTicks), poses);
            VertexConsumer builder2 = buffer.getBuffer(DragonFightRenderType.eyes(DRAGON_EYE_LOCATION));
            model.drawAnimatedModel(poseStack, builder2, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY,poses);
        }

        int density;
        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            Layer.Priority[] var18 = Layer.Priority.values();
            int var19 = var18.length;

            for(density = 0; density < var19; ++density) {
                Layer.Priority priority = var18[density];
                AnimationPlayer animPlayer = entitypatch.getClientAnimator().getCompositeLayer(priority).animationPlayer;
                float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
                animPlayer.getAnimation().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
            }
        }

        poseStack.popPose();
        if (entityIn.nearestCrystal != null) {
            deathTimeProgression = (float)(entityIn.nearestCrystal.getX() - Mth.lerp((double)partialTicks, entityIn.xo, entityIn.getX()));
            float y = (float)(entityIn.nearestCrystal.getY() - Mth.lerp((double)partialTicks, entityIn.yo, entityIn.getY()));
            float z = (float)(entityIn.nearestCrystal.getZ() - Mth.lerp((double)partialTicks, entityIn.zo, entityIn.getZ()));
            poseStack.pushPose();
            EnderDragonRenderer.renderCrystalBeams(deathTimeProgression, y + EndCrystalRenderer.getY(entityIn.nearestCrystal, partialTicks), z, partialTicks, entityIn.tickCount, poseStack, buffer, packedLight);
            poseStack.popPose();
        }

        if (entityIn.dragonDeathTime > 0) {
            deathTimeProgression = ((float)entityIn.dragonDeathTime + partialTicks) / 200.0F;
            lightningBuffer = buffer.getBuffer(RenderType.lightning());
            density = (int)((deathTimeProgression + deathTimeProgression * deathTimeProgression) / 2.0F * 60.0F);
            float f7 = Math.min(deathTimeProgression > 0.8F ? (deathTimeProgression - 0.8F) / 0.2F : 0.0F, 1.0F);
            poseStack.pushPose();
            LightningRenderHelper.renderCyclingLight(lightningBuffer, poseStack, 255, 0, 255, density, 1.0F, deathTimeProgression, f7);
            poseStack.popPose();
        }

    }

}
