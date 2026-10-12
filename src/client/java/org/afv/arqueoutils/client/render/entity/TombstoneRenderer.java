package org.afv.arqueoutils.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.afv.arqueoutils.client.render.state.TombstoneRenderState;
import org.afv.arqueoutils.entity.TombstoneEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class TombstoneRenderer extends EntityRenderer<TombstoneEntity, TombstoneRenderState> {
    private final Vector3f POS_Y = new Vector3f(0, 1, 0);

    private final PlayerEntityModel headModel;

    public TombstoneRenderer(EntityRendererFactory.Context context) {
        super(context);
        // simple humanoid head model (vanilla PlayerEntityModel)
        this.headModel = new PlayerEntityModel(context.getPart(EntityModelLayers.PLAYER), false);
    }

    @Override
    public TombstoneRenderState createRenderState() {
        return new TombstoneRenderState();
    }

    @Override
    public void updateRenderState(TombstoneEntity tombstone, TombstoneRenderState renderState, float partialTick) {
        super.updateRenderState(tombstone, renderState, partialTick);
        renderState.tombstone = tombstone;
    }

    @Override
    public void render(TombstoneRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        matrices.push();

        // center in the entity
        matrices.translate(0, 0.375, 0); // 0.75/2

        // scale down to fit 0.75x0.75
//        float scale = 0.75f / 1.8f; // 1.8 is player height approx
//        matrices.scale(scale, scale, scale);
//
//        // rotate toward nearest player yaw
//        MinecraftClient client = MinecraftClient.getInstance();
//        assert client.world != null;
//        PlayerEntity nearest = client.world.getClosestPlayer(state.x, state.y, state.z, 16, false);
//        float yaw = nearest != null ? nearest.getYaw() : 0;
//        matrices.multiply(fromAxisAngle(POS_Y, -yaw));
//
//        // apply red outline if glowing
//        // always glowing
//        // red
//        state.outlineColor = 0xFF0000;
//
//        // render the head
//        if (state.tombstone != null) {
//            DataTracker dataTracker = state.tombstone.getDataTracker(); // your stored GameProfile
//
//            String name = dataTracker.get(OWNER_NAME);
//            String value = dataTracker.get(OWNER_SKIN_VALUE);
//            String signature = dataTracker.get(OWNER_SKIN_SIGNATURE);
//
//            if (name != null) {
//                // Always create a new GameProfile for rendering
//                GameProfile renderProfile = new GameProfile(UUID.randomUUID(), name);
//
//                // Only add textures if we have both value and signature
//                if (value != null && !value.isEmpty() && signature != null && !signature.isEmpty()) {
//                    // Create a brand new mutable PropertyMap
//                    PropertyMap mutableMap = PropertyMap.EMPTY;
//                    mutableMap.put("textures", new Property("textures", value, signature));
//
//                    // Assign it to a new GameProfile safely
//                    renderProfile = new GameProfile(renderProfile.id(), renderProfile.name());
//                    renderProfile.properties().putAll(mutableMap);
//                }
//
//                // Get skin textures safely
//                SkinTextures textures = DefaultSkinHelper.getSkinTextures(renderProfile);
//                Identifier skinId = textures.body().id();
//
//                // Render the head
//                queue.submitModelPart(headModel.head, matrices, RenderLayers.entityCutoutNoCull(skinId),
//                        state.light, OverlayTexture.DEFAULT_UV, null,
//                        false, false, -1, null, 0xFF0000);
//            }
//
//// get cached or default skin synchronously
//
//
//        }
        matrices.pop();

        super.render(state, matrices, queue, cameraState);
    }

    public static Quaternionf fromAxisAngle(Vector3f axis, float angleDeg) {
        Quaternionf q = new Quaternionf();
        q.fromAxisAngleDeg(axis, angleDeg); // JOML helper
        return q;
    }
}