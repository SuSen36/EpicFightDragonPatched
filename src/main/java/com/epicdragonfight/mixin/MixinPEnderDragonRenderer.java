package com.epicdragonfight.mixin;

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
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.LightningRenderHelper;
import yesman.epicfight.client.renderer.patched.entity.PEnderDragonRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.DragonCrystalLinkPhase;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.PatchedPhases;

@OnlyIn(Dist.CLIENT)
@Mixin(value = PEnderDragonRenderer.class,remap = false)
public class MixinPEnderDragonRenderer extends PatchedEntityRenderer<EnderDragon, EnderDragonPatch, EnderDragonRenderer> {

    private static final ResourceLocation DRAGON_EYE_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    private static final ResourceLocation DRAGON_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    private static final ResourceLocation DRAGON_EXPLODING_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");


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
            VertexConsumer builder2 = buffer.getBuffer(RenderType.eyes(DRAGON_EYE_LOCATION));
            model.drawAnimatedModel(poseStack, builder2, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, this.getOverlayCoord(entityIn, entitypatch, partialTicks), poses);

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
@Shadow
    public void mulPoseStack(PoseStack matStack, Armature armature, EnderDragon entityIn, EnderDragonPatch entitypatch, float partialTicks) {
        OpenMatrix4f modelMatrix;
        if (entitypatch.isGroundPhase() && ((EnderDragon)entitypatch.getOriginal()).dragonDeathTime <= 0) {
            modelMatrix = entitypatch.getModelMatrix(partialTicks).scale(-1.0F, 1.0F, -1.0F);
        } else {
            float f = (float)entityIn.getLatencyPos(7, partialTicks)[0];
            float f1 = (float)(entityIn.getLatencyPos(5, partialTicks)[1] - entityIn.getLatencyPos(10, partialTicks)[1]);
            float f2 = ((EnderDragon)entitypatch.getOriginal()).dragonDeathTime > 0 ? 0.0F : Mth.rotWrap(entityIn.getLatencyPos(5, partialTicks)[0] - entityIn.getLatencyPos(10, partialTicks)[0]);
            modelMatrix = MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, f1, f1, f, f, partialTicks, 1.0F, 1.0F, 1.0F).rotateDeg(-f2 * 1.5F, Vec3f.Z_AXIS);
        }

        OpenMatrix4f transpose = (new OpenMatrix4f(modelMatrix)).transpose();
        MathUtils.translateStack(matStack, modelMatrix);
        MathUtils.rotateStack(matStack, transpose);
        MathUtils.scaleStack(matStack, transpose);
    }
@Shadow
    protected int getOverlayCoord(EnderDragon entity, EnderDragonPatch entitypatch, float partialTicks) {
        DragonPhaseInstance currentPhase = entity.getPhaseManager().getCurrentPhase();
        float chargingTick = 158.0F;
        float progression = currentPhase.getPhase() == PatchedPhases.CRYSTAL_LINK ? (chargingTick - (float)((DragonCrystalLinkPhase)currentPhase).getChargingCount()) / chargingTick : 0.0F;
        return OverlayTexture.pack(OverlayTexture.u(progression), OverlayTexture.v(entity.hurtTime > 5 || entity.deathTime > 0));
    }
}
