package org.afv.arqueoutils.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.text.object.AtlasTextObjectContents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.afv.arqueoutils.init.ModBlockEntities;
import org.afv.arqueoutils.util.SoundUtils;
import org.afv.arqueoutils.util.TextUtils;
import org.afv.arqueoutils.world.CTMState;

public class CoreBlock extends BlockWithEntity {

    public CoreBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(COMPLETED, false)
                .with(COLOR, CoreColor.WHITE));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    public static final BooleanProperty COMPLETED = BooleanProperty.of("completed");
    public static final EnumProperty<CoreColor> COLOR = EnumProperty.of("color", CoreColor.class);

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(COMPLETED, COLOR);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CoreBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            BlockHitResult hit
    ) {
        if (player.isSneaking()) {
            return ActionResult.PASS;
        }

        if (world.isClient()) return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(pos);

        if (!(be instanceof CoreBlockEntity core))
            return ActionResult.PASS;

        if (core.isCompleted())
            return ActionResult.PASS;

        ItemStack hand = player.getStackInHand(Hand.MAIN_HAND);
        Item required = core.getRequiredItem();

        CoreColor c = CoreColor.getById(core.getColorId());
        int woolColor = c == null ? 0xFFFFFF : c.getTextColor().getRgb();

        if (hand.isOf(required)) {
            hand.decrement(1);

            SoundUtils.sendSoundClient(player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1, 1.5f);
            MinecraftServer server = world.getServer();
            if (server != null) {
                CTMState ctmState = CTMState.get(server);
                CTMState.CoreColor coreCol = CTMState.CoreColor.getByName(core.getColorId());
                if (coreCol != null) {
                    ctmState.setPlayerForCore(coreCol, player);
                }
                TextUtils.broadcast(server, Text.empty()
                                .append(Text.literal("\n> ")
                                        .formatted(Formatting.AQUA)
                                        .formatted(Formatting.BOLD))
                                .append(TextUtils.getHeadText(player))
                                .append(" ")
                                .append(player.getName().copy()
                                        .formatted(Formatting.YELLOW))
                                .append(" ")
                                .append(Text.translatable("chat.arqueoutils.ctm.completed")
                                        .formatted(Formatting.WHITE))
                                .append(" ")
                                .append(Text.object(new AtlasTextObjectContents(Identifier.ofVanilla("blocks"), Identifier.ofVanilla("block/" + core.getColorId() + "_wool"))))
                                .append(" ")
                                .append(Text.translatable("block.minecraft." + core.getColorId() + "_wool")
                                        .withColor(woolColor))
                                .append("\n"),
                        false);
                for (World w : world.getServer().getWorlds()) {
                    for (PlayerEntity p : w.getPlayers()) {
                        SoundUtils.sendSoundClient(p, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1, 1);
                    }
                }
            }

            core.setCompleted(true);

            spawnParticles((ServerWorld) world, pos);

            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (world.isClient()) return null;

        return BlockWithEntity.validateTicker(
                type,
                ModBlockEntities.CTM_CORE,
                (world1, pos, state1, entity) -> {
                    if (entity instanceof CoreBlockEntity core) {
                        core.tick();
                    }
                }
        );
    }

    private void spawnParticles(ServerWorld world, BlockPos pos) {

        world.spawnParticles(
                ParticleTypes.END_ROD,
                pos.getX() + 0.5,
                pos.getY() + 3.0,
                pos.getZ() + 0.5,
                25,
                0.3, 0.3, 0.3,
                0.02
        );
    }
}