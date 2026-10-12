package org.afv.arqueoutils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import net.minecraft.world.rule.GameRules;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.network.OpenArqueoMenuPayload;
import org.afv.arqueoutils.util.DisguiseManager;
import org.afv.arqueoutils.util.TextUtils;
import org.afv.arqueoutils.world.ArqueoWorldState;
import org.afv.arqueoutils.world.CTMState;
import org.afv.arqueoutils.world.PlaytimeState;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ArqueoUtilsCommand {
    private final static HashMap<ServerPlayerEntity, Pair<ServerWorld, Vec3d>> LOCATION_STORAGE = new HashMap<>();
    public static boolean isPreDay0 = false;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("arqueoutils")
                .requires(source -> {
                    if (source.getEntity() == null) return true; // Console / non-player command source
                    return source.getEntity().getName().getString().equals("AFVSuper");
                })
                .then(CommandManager.literal("menu")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player == null) {
                                context.getSource().sendMessage(Text.literal("Only players can run this command!"));
                                return 1;
                            }
                            ServerPlayNetworking.send(player, new OpenArqueoMenuPayload());
                            return 1;
                        })
                )
                .then(CommandManager.literal("playtimes")
                        .executes(ArqueoUtilsCommand::executePlaytimes)
                )
                .then(CommandManager.literal("v-spec")
                        .executes(ArqueoUtilsCommand::executeVanishSpectator))
                .then(CommandManager.literal("say")
                        .then(CommandManager.argument("msg", StringArgumentType.string())
                                .executes(ArqueoUtilsCommand::executeSay)
                        )
                )
                .then(CommandManager.literal("first-day")
                        .then(CommandManager.literal("pre-day0")
                                .executes(context -> {
                                    MinecraftServer server = context.getSource().getServer();
                                    if (server != null) {
                                        ServerWorld overworld = server.getOverworld();
                                        WorldBorder border = overworld.getWorldBorder();
                                        server.setDifficulty(Difficulty.PEACEFUL, true);
                                        border.setCenter(0.0, 0.0);
                                        border.setSize(20);
                                        border.setWarningBlocks(0);

                                        GameRules gameRules = overworld.getGameRules();
                                        gameRules.setValue(GameRules.ADVANCE_TIME, false, server);
                                        gameRules.setValue(GameRules.ADVANCE_WEATHER, false, server);
                                        gameRules.setValue(GameRules.SPAWN_PHANTOMS, false, server);
                                        gameRules.setValue(GameRules.DO_MOB_SPAWNING, false, server);
                                        gameRules.setValue(GameRules.PVP, false, server);
                                        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                            player.changeGameMode(GameMode.ADVENTURE);
                                        }
                                    }
                                    isPreDay0 = true;
                                    return 1;
                                }))
                        .then(CommandManager.literal("start-day0")
                                .executes(context -> {
                                    MinecraftServer server = context.getSource().getServer();
                                    isPreDay0 = false;
                                    if (server != null) {
                                        ServerWorld overworld = server.getOverworld();
                                        WorldBorder border = overworld.getWorldBorder();

                                        server.setDifficulty(Difficulty.HARD, true);

                                        GameRules gameRules = overworld.getGameRules();
                                        gameRules.setValue(GameRules.ADVANCE_TIME, true, server);
                                        gameRules.setValue(GameRules.ADVANCE_WEATHER, true, server);
                                        gameRules.setValue(GameRules.SPAWN_PHANTOMS, true, server);
                                        gameRules.setValue(GameRules.DO_MOB_SPAWNING, true, server);
                                        gameRules.setValue(GameRules.PLAYERS_SLEEPING_PERCENTAGE, 0, server);
                                        gameRules.setValue(GameRules.PVP, true, server);

                                        border.setCenter(0.0, 0.0);
                                        border.interpolateSize(20, 500, 200, overworld.getTime());

                                        PlaytimeState playtimeState = PlaytimeState.get(server);
                                        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                            player.changeGameMode(GameMode.SURVIVAL);
                                            playtimeState.setPlaytimeTicks(player.getUuid(), 0);
                                        }

                                        ScheduledExecutorService scheduler =
                                                Executors.newSingleThreadScheduledExecutor();

                                        scheduler.schedule(() -> border.setSize(5.999997E7F), 10, TimeUnit.SECONDS);
                                    }

                                    return 1;
                                }))
                        .then(CommandManager.literal("locate-event")
                                .then(CommandManager.argument("structure", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            builder.suggest("all");
                                            builder.suggest("shipwreck");
                                            builder.suggest("armadillo");
                                            builder.suggest("ocean_ruins");
                                            builder.suggest("ancient_city");
                                            builder.suggest("trial_chambers");
                                            builder.suggest("bastion");
                                            builder.suggest("fortress");
                                            return builder.buildFuture();
                                        })
                                        .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                                .executes(ArqueoUtilsCommand::executeCTMLocate)
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("debug")
                        .then(CommandManager.literal("cores-get")
                                .executes(context -> {
                                    int cores = ArqueoWorldState.get(context.getSource().getServer()).getCompletedCores();
                                    context.getSource().sendMessage(Text.literal(
                                            cores + " cores have been completed"
                                    ));
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("cores-reset")
                                .executes(context -> {
                                    ArqueoWorldState.get(context.getSource().getServer()).setCompletedCores(0);
                                    CTMState state = CTMState.get(context.getSource().getServer());
                                    state.clearPlayerMap();
                                    state.setWitherSkeletonObtained(false);
                                    state.setAncientCityChestsOpened(0);
                                    if (context.getSource().getEntity() instanceof ServerPlayerEntity player) {
                                        player.getStatHandler().setStat(player, Stats.KILLED.getOrCreateStat(EntityType.WITHER_SKELETON), 38);
                                    }
                                    context.getSource().sendMessage(Text.literal(
                                            "Core count was successfully reset"
                                    ));
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("place-monument")
                                .executes(ArqueoUtilsCommand::executePlaceMonument)
                        )
                )
        );
    }

    private static int executeVanishSpectator(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendMessage(Text.literal("Only players can run this command!"));
            return 0;
        }
        boolean disguised = DisguiseManager.isDisguised(context.getSource().getServer(), player.getUuid());
        if (disguised) {
            ServerWorld world = LOCATION_STORAGE.get(player).getLeft();
            Vec3d loc = LOCATION_STORAGE.get(player).getRight();
            if (loc != null && world != null) player.teleport(world, loc.x, loc.y, loc.z, Set.of(), 0, 0, false);
            DisguiseManager.setDisguised(player, false, true);
        } else {
            DisguiseManager.setDisguised(player, true, true);
            LOCATION_STORAGE.put(player, new Pair<>(player.getEntityWorld(), player.getEntityPos()));
        }
        player.changeGameMode(disguised ? GameMode.SURVIVAL : GameMode.SPECTATOR);
        return 1;
    }

    private static int executePlaytimes(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        source.sendMessage(Text.literal("\n"));
        source.sendMessage(Text.translatable("command.arqueoutils.playtimes.title")
                .formatted(Formatting.DARK_AQUA)
                .formatted(Formatting.BOLD));
        source.sendMessage(Text.empty());

        MinecraftServer server = context.getSource().getServer();

        List<Pair<UUID, Long>> times = PlaytimeState.get(server).getSortedTimes();

        int top = 1;
        for (Pair<UUID, Long> pair : times) {
            Optional<PlayerConfigEntry> profile = server.getApiServices().nameToIdCache().getByUuid(pair.getLeft());
            String name = profile.map(PlayerConfigEntry::name).orElse("Unknown");
            long ticks = pair.getRight();
            long seconds = Math.floorDiv(ticks, 20);
            TextColor numColor = TextColor.fromFormatting(Formatting.DARK_GRAY);
            switch (top) {
                case 1 -> numColor = TextColor.fromRgb(0xF9B200);
                case 2 -> numColor = TextColor.fromRgb(0xB4C0D8);
                case 3 -> numColor = TextColor.fromRgb(0xAD581D);
                default -> {}
            }
            if (numColor == null) continue;
            source.sendMessage(Text.empty()
                    .append(Text.literal(top + " ")
                            .withColor(numColor.getRgb())
                            .formatted(Formatting.BOLD))
                    .append(Text.literal("> ")
                            .formatted(Formatting.GRAY)
                            .formatted(Formatting.BOLD))
                    .append(TextUtils.getHeadText(pair.getLeft()))
                    .append(" ")
                    .append(Text.literal(name)
                            .formatted(Formatting.YELLOW))
                    .append(Text.literal(" - ")
                            .formatted(Formatting.GRAY))
                    .append(Text.literal(TextUtils.formatTime(seconds))
                            .formatted(Formatting.AQUA))
            );
            ++top;
        }
        source.sendMessage(Text.literal("\n"));
        return 1;
    }

    private static int executePlaceMonument(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null || !(player.getEntityWorld() instanceof ServerWorld world)) return 0;

        ChunkPos chunkPos = player.getChunkPos();

        int chunkStartX = chunkPos.getStartX();
        int chunkStartZ = chunkPos.getStartZ();
        int y = player.getBlockY();

        BlockPos placementPos = new BlockPos(
                chunkStartX - 2,
                y - 2,
                chunkStartZ - 2
        );

        MinecraftServer server = world.getServer();

        if (server == null) return 0;
        StructureTemplateManager manager = server.getStructureTemplateManager();
        Identifier structureId = Identifier.of(ArqueoUtils.ModID, "monument");
        Optional<StructureTemplate> optional = manager.getTemplate(structureId);

        if (optional.isPresent()) {
            StructureTemplate template = optional.get();

            StructurePlacementData placementData = new StructurePlacementData()
                    .setIgnoreEntities(false);

            template.place(
                    world,
                    placementPos,
                    placementPos,
                    placementData,
                    world.getRandom(),
                    2
            );
        }

        return 1;
    }

    private static int executeCTMLocate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String string = StringArgumentType.getString(context, "structure");
        for (Entity entity : EntityArgumentType.getEntities(context, "targets")) {
            if (!(entity instanceof ServerPlayerEntity player)) continue;
            CTMState state = CTMState.get(context.getSource().getServer());
            Map<CTMState.CoreColor, String> map = state.getPlayerMap();
            ServerWorld world = player.getEntityWorld();
            MutableText text = Text.empty()
                    .append(Text.literal("\nArqueo-CTM Locator\n\n")
                            .formatted(Formatting.AQUA)
                            .formatted(Formatting.BOLD)
                            .formatted(Formatting.UNDERLINE));
            if (string.equalsIgnoreCase("shipwreck") && !map.containsKey(CTMState.CoreColor.YELLOW) && world.getRegistryKey() == World.OVERWORLD) {
                BlockPos pos = world.locateStructure(
                        StructureTags.SHIPWRECK,
                        player.getBlockPos(),
                        100,   // radius in chunks
                        false  // skip explored chunks
                );

                if (pos != null) {
                    BlockPos playerPos = player.getBlockPos();
                    int x = pos.getX();
                    int z = pos.getZ();
                    double distance = playerPos.getSquaredDistance(pos);
                    distance = Math.ceil(Math.sqrt(distance));
                    text.append(Text.literal("Shipwreck: ").formatted(Formatting.BLUE));
                    text.append(Text.literal("("+x+","+z+") "));
                    text.append(Text.literal("["+((long) distance)+" blocks]\n").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
                }
            }
            if ((string.equalsIgnoreCase("all") || string.equalsIgnoreCase("armadillo")) && !map.containsKey(CTMState.CoreColor.LIME) && world.getRegistryKey() == World.OVERWORLD) {
                Registry<Biome> biomeRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.BIOME);

                RegistryEntryList<Biome> biomes = RegistryEntryList.of(
                        biomeRegistry.getOrThrow(BiomeKeys.SAVANNA),
                        biomeRegistry.getOrThrow(BiomeKeys.SAVANNA_PLATEAU),
                        biomeRegistry.getOrThrow(BiomeKeys.WINDSWEPT_SAVANNA),
                        biomeRegistry.getOrThrow(BiomeKeys.BADLANDS),
                        biomeRegistry.getOrThrow(BiomeKeys.ERODED_BADLANDS),
                        biomeRegistry.getOrThrow(BiomeKeys.WOODED_BADLANDS)
                );

                com.mojang.datafixers.util.Pair<BlockPos, RegistryEntry<Biome>> pair = world.locateBiome(
                        biomes::contains,
                        player.getBlockPos(),
                        6400,
                        32,
                        64
                );

                if (pair != null) {
                    BlockPos pos = pair.getFirst();
                    RegistryEntry<Biome> biome = pair.getSecond();

                    Identifier id = biome.getKey().orElseThrow().getValue();
                    Text prefix;
                    if (id.getPath().contains("savanna")) {
                        prefix = Text.literal("Savanna: ").withColor(0xba9154);
                    } else if (id.getPath().contains("badlands")) {
                        prefix = Text.literal("Badlands: ").withColor(0xd9664a);
                    } else {
                        prefix = Text.literal("Armadillo: ").withColor(0xc9714f);
                    }
                    if (pos != null) {
                        BlockPos playerPos = player.getBlockPos();
                        int x = pos.getX();
                        int z = pos.getZ();
                        double distance = playerPos.getSquaredDistance(pos);
                        distance = Math.ceil(Math.sqrt(distance));
                        text.append(prefix);
                        text.append(Text.literal("("+x+","+z+") "));
                        text.append(Text.literal("["+((long) distance)+" blocks]\n").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
                    }
                }
            }
            if ((string.equalsIgnoreCase("all") || string.equalsIgnoreCase("ocean_ruins")) && !map.containsKey(CTMState.CoreColor.CYAN) && world.getRegistryKey() == World.OVERWORLD) {
                BlockPos pos = world.locateStructure(
                        StructureTags.OCEAN_RUIN,
                        player.getBlockPos(),
                        100,   // radius in chunks
                        false  // skip explored chunks
                );

                if (pos != null) {
                    BlockPos playerPos = player.getBlockPos();
                    int x = pos.getX();
                    int z = pos.getZ();
                    double distance = playerPos.getSquaredDistance(pos);
                    distance = Math.ceil(Math.sqrt(distance));
                    text.append(Text.literal("Ocean Ruin: ").formatted(Formatting.GRAY));
                    text.append(Text.literal("("+x+","+z+") "));
                    text.append(Text.literal("["+((long) distance)+" blocks]\n").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
                }
            }
            if ((string.equalsIgnoreCase("all") || string.equalsIgnoreCase("ancient_city")) && !map.containsKey(CTMState.CoreColor.BLUE) && world.getRegistryKey() == World.OVERWORLD) {
                Registry<Structure> registry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);

                RegistryEntry<Structure> ancientCity = registry.getOrThrow(StructureKeys.ANCIENT_CITY);

                RegistryEntryList<Structure> list = RegistryEntryList.of(ancientCity);

                com.mojang.datafixers.util.Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager()
                        .getChunkGenerator()
                        .locateStructure(world, list, player.getBlockPos(), 100, false);

                if (pair == null) break;
                BlockPos pos = pair.getFirst();
                if (pos != null) {
                    BlockPos playerPos = player.getBlockPos();
                    int x = pos.getX();
                    int z = pos.getZ();
                    double distance = playerPos.getSquaredDistance(pos);
                    distance = Math.ceil(Math.sqrt(distance));
                    text.append(Text.literal("Ancient City: ").formatted(Formatting.DARK_AQUA));
                    text.append(Text.literal("("+x+","+z+") "));
                    text.append(Text.literal("["+((long) distance)+" blocks]\n").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
                }
            }
            if ((string.equalsIgnoreCase("all") || string.equalsIgnoreCase("trial_chambers")) && !map.containsKey(CTMState.CoreColor.GREEN) && world.getRegistryKey() == World.OVERWORLD) {
                Registry<Structure> registry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);

                RegistryEntry<Structure> trialChamber = registry.getOrThrow(StructureKeys.TRIAL_CHAMBERS);

                RegistryEntryList<Structure> list = RegistryEntryList.of(trialChamber);

                com.mojang.datafixers.util.Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager()
                        .getChunkGenerator()
                        .locateStructure(world, list, player.getBlockPos(), 100, false);

                if (pair == null) break;
                BlockPos pos = pair.getFirst();
                if (pos != null) {
                    BlockPos playerPos = player.getBlockPos();
                    int x = pos.getX();
                    int z = pos.getZ();
                    double distance = playerPos.getSquaredDistance(pos);
                    distance = Math.ceil(Math.sqrt(distance));
                    text.append(Text.literal("Trial Chamber: ").withColor(0xf58a3d));
                    text.append(Text.literal("("+x+","+z+") "));
                    text.append(Text.literal("["+((long) distance)+" blocks]\n").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
                }
            }
            if ((string.equalsIgnoreCase("all") || string.equalsIgnoreCase("bastion")) && !map.containsKey(CTMState.CoreColor.RED) && world.getRegistryKey() == World.NETHER) {
                Registry<Structure> registry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);

                RegistryEntry<Structure> bastion = registry.getOrThrow(StructureKeys.BASTION_REMNANT);

                RegistryEntryList<Structure> list = RegistryEntryList.of(bastion);

                com.mojang.datafixers.util.Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager()
                        .getChunkGenerator()
                        .locateStructure(world, list, player.getBlockPos(), 100, false);

                if (pair == null) break;
                BlockPos pos = pair.getFirst();
                if (pos != null) {
                    BlockPos playerPos = player.getBlockPos();
                    int x = pos.getX();
                    int z = pos.getZ();
                    double distance = playerPos.getSquaredDistance(pos);
                    distance = Math.ceil(Math.sqrt(distance));
                    text.append(Text.literal("Bastion Remnant: ").formatted(Formatting.YELLOW));
                    text.append(Text.literal("("+x+","+z+") "));
                    text.append(Text.literal("["+((long) distance)+" blocks]\n").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
                }
            }
            if ((string.equalsIgnoreCase("all") || string.equalsIgnoreCase("fortress")) && !map.containsKey(CTMState.CoreColor.BLACK) && world.getRegistryKey() == World.NETHER) {
                Registry<Structure> registry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);

                RegistryEntry<Structure> fortress = registry.getOrThrow(StructureKeys.FORTRESS);

                RegistryEntryList<Structure> list = RegistryEntryList.of(fortress);

                com.mojang.datafixers.util.Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager()
                        .getChunkGenerator()
                        .locateStructure(world, list, player.getBlockPos(), 100, false);

                if (pair == null) break;
                BlockPos pos = pair.getFirst();
                if (pos != null) {
                    BlockPos playerPos = player.getBlockPos();
                    int x = pos.getX();
                    int z = pos.getZ();
                    double distance = playerPos.getSquaredDistance(pos);
                    distance = Math.ceil(Math.sqrt(distance));
                    text.append(Text.literal("Nether Fortress: ").formatted(Formatting.RED));
                    text.append(Text.literal("("+x+","+z+") "));
                    text.append(Text.literal("["+((long) distance)+" blocks]\n").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
                }
            }
            player.sendMessage(text);
            player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1, 1);
        }
        return 1;
    }

    private static int executeSay(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getServer();
        Team team = server.getScoreboard().getTeam("AFVSuper");
        if (team == null) return 1;
        MutableText text = Text.empty();
        text.append(TextUtils.getHeadText(UUID.fromString("dc77f06f-b955-4fdb-9d69-6fa4c86c9b63")));
        text.append(Text.literal(" <"));
        text.append(team.getPrefix());
        text.append(Text.literal("AFVSuper"));
        text.append(team.getSuffix());
        text.append(Text.literal("> "));
        text.append(Text.literal(StringArgumentType.getString(context, "msg")));
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(text);
        }
        return 1;
    }
}
