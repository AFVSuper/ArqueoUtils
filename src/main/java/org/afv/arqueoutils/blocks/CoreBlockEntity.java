package org.afv.arqueoutils.blocks;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.afv.arqueoutils.init.ModBlockEntities;
import org.afv.arqueoutils.network.GlobalBWPayload;
import org.afv.arqueoutils.util.GlobalState;
import org.afv.arqueoutils.util.TextUtils;
import org.afv.arqueoutils.world.ArqueoWorldState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Set;
import java.util.UUID;

public class CoreBlockEntity extends BlockEntity {

    private final CoreServerData serverData = new CoreServerData();
    private boolean displaySpawned = false;

//    private ItemStack requiredItem = ItemStack.EMPTY;
//    private String colorId = "white";
//    private boolean completed = false;

    public CoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CTM_CORE, pos, state);
    }

    public boolean isCompleted() {
        return serverData.completed;
    }

    public void setCompleted(boolean value) {
        serverData.completed = value;
        if (world != null && !world.isClient()) {
            removeDisplay();
            spawnDisplay();
            BlockState state = world.getBlockState(getPos());
            world.setBlockState(getPos(),
                    state.with(CoreBlock.COMPLETED, true)
                            .with(CoreBlock.COLOR, CoreColor.valueOf(serverData.colorId.toUpperCase())),
                    3);
            if (world.getServer() != null) ArqueoWorldState.get(world.getServer()).incrementCores();
            checkForFilter(world.getServer());
        }
        markDirty();
    }

    public Item getRequiredItem() {
        return Registries.ITEM.get(Identifier.of(serverData.requiredItem));
    }

    public String getColorId() {
        return serverData.colorId;
    }

    public void setRequiredItem(Item item) {
        serverData.requiredItem = Registries.ITEM.getId(item).toString();
        markDirty();
    }

    public CoreServerData getData() {
        return serverData;
    }

    public static void checkForFilter(MinecraftServer server) {
        if (ArqueoWorldState.get(server).getCompletedCores() >= 16) {
            TextUtils.broadcast(server, Text.empty()
                            .append(Text.literal("\n> ")
                                    .formatted(Formatting.AQUA)
                                    .formatted(Formatting.BOLD))
                            .append(Text.translatable("chat.arqueoutils.ctm.all_completed.1")
                                    .formatted(Formatting.YELLOW))
                            .append(" ")
                            .append(Text.translatable("chat.arqueoutils.ctm.all_completed.2")
                                    .formatted(Formatting.GOLD)
                                    .formatted(Formatting.BOLD))
                            .append(Text.translatable("chat.arqueoutils.ctm.all_completed.3")
                                    .formatted(Formatting.YELLOW))
                            .append("\n")
                    , false);
            GlobalState.setBWEnabled(server.getSpawnWorld(), false);
            for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player instanceof ServerPlayerEntity player1) {
                    ServerPlayNetworking.send(player1, new GlobalBWPayload(false));
                }
            }
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putString("color", serverData.colorId);
        view.putString("required_item", serverData.requiredItem);
        view.putBoolean("completed", serverData.completed);

        // write UUID as string if not null
        if (serverData.displayEntity != null) {
            view.putString("display_entity", serverData.displayEntity.toString());
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        serverData.colorId = view.getString("color", "white");
        serverData.requiredItem = view.getString("required_item", "minecraft:air");
        serverData.completed = view.getBoolean("completed", false);

        // read UUID if present
        String uuidString = view.getString("display_entity", null);
        serverData.displayEntity = (uuidString != null) ? UUID.fromString(uuidString) : null;
    }

    private float spinAngle = 0f;   // rotation in radians

    public void tick() {
        if (world == null || world.isClient()) return;

        if (serverData.displayEntity == null) {
            spawnDisplay();
        } else {
            Entity e = world.getEntity(serverData.displayEntity);
            if (e instanceof DisplayEntity display) {

                double spinSpeed = 0.12; // en radianes

                spinAngle += (float) spinSpeed;
                if (spinAngle > 2 * Math.PI) spinAngle -= (float) (2 * Math.PI);

                double bounceSpeed = 0.06;       // en radianes
                double bounceAmplitude = 0.25;
                double bounceAngle = world.getTime() * bounceSpeed;
                float yOffset = (float)(Math.sin(bounceAngle) * bounceAmplitude);

                float scale = !serverData.completed && SCALE_DOWN.contains(serverData.requiredItem) ?
                        0.75f :
                        1f;

                display.setTransformation(new AffineTransformation(
                        new Vector3f(0f, yOffset, 0f),
                        new Quaternionf().rotateY(spinAngle),
                        new Vector3f(scale, scale, scale),
                        new Quaternionf()
                ));
            }
        }
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

//        if (!world.isClient() && !displaySpawned) {
//            spawnDisplay();
//            displaySpawned = true;
//        }
    }



    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        removeDisplay();
        super.onBlockReplaced(pos, oldState);
    }

    private static final Set<String> SCALE_DOWN = Set.of(
            "minecraft:creaking_heart",
            "minecraft:diamond_block",
            "minecraft:emerald_ore",
            "minecraft:ancient_debris"
    );

    private void spawnDisplay() {

        if (world == null || world.isClient()) return;

        ServerWorld serverWorld = (ServerWorld) world;

        removeDisplay();

        BlockPos pos = getPos().add(0, 4, 0);

        Entity display;

        if (!serverData.completed) {

            DisplayEntity.ItemDisplayEntity itemDisplay = EntityType.ITEM_DISPLAY.spawn(
                    serverWorld,
                    pos,
                    SpawnReason.TRIGGERED);

            if (itemDisplay != null) {
                float scale = !serverData.completed && SCALE_DOWN.contains(serverData.requiredItem) ?
                        0.75f :
                        1f;

                itemDisplay.setTransformation(new AffineTransformation(
                        new Vector3f(0.5f, 0.5f, 0.5f),          // translation: move origin to center
                        new Quaternionf(),                        // rotation: no rotation yet
                        new Vector3f(scale, scale, scale),                 // scale
                        new Quaternionf()                         // post-rotation: keep identity
                ));

                itemDisplay.setItemStack(new ItemStack(Registries.ITEM.get(
                        Identifier.of(serverData.requiredItem)
                )));
            }

            display = itemDisplay;

        } else {
            DisplayEntity.ItemDisplayEntity blockDisplay = EntityType.ITEM_DISPLAY.spawn(
                    serverWorld,
                    pos,
                    SpawnReason.TRIGGERED);

            if (blockDisplay != null) {
                // offset to center of the block (0.5, 0.5, 0.5)
                blockDisplay.setTransformation(new AffineTransformation(
                        new Vector3f(0.5f, 0.5f, 0.5f),          // translation: move origin to center
                        new Quaternionf(),                        // rotation: no rotation yet
                        new Vector3f(1f, 1f, 1f),                 // scale
                        new Quaternionf()                         // post-rotation: keep identity
                ));
                blockDisplay.setItemStack(new ItemStack(Registries.ITEM.get(
                        Identifier.of("minecraft", serverData.colorId + "_wool")
                )));
            }

            display = blockDisplay;
        }

        if (display != null) {
            display.setNoGravity(true);
            display.setYaw(0);
            serverData.displayEntity = display.getUuid();
        }

        markDirty();
    }

    private void removeDisplay() {
        if (serverData.displayEntity == null || world == null) return;

        Entity e = world.getEntity(serverData.displayEntity);
        if (e != null) e.discard();

        serverData.displayEntity = null;
        markDirty();
    }
}