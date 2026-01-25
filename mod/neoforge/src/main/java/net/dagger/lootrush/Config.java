package net.dagger.lootrush;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BANNED_ITEMS = BUILDER
            .comment("List of items that should not appear in the game.",
                    "Use exact item ids (e.g. minecraft:bedrock) or regex with 'REGEX:' prefix.")
            .defineList("banned-items", Arrays.asList(
                    "minecraft:structure_block",
                    "minecraft:jigsaw",
                    "minecraft:command_block",
                    "minecraft:repeating_command_block",
                    "minecraft:chain_command_block",
                    "minecraft:barrier",
                    "minecraft:debug_stick",
                    "minecraft:knowledge_book",
                    "minecraft:light",
                    "minecraft:bundle",
                    "minecraft:bedrock",
                    "minecraft:budding_amethyst",
                    "minecraft:command_block_minecart",
                    "minecraft:spawner",
                    "minecraft:reinforced_deepslate",
                    "minecraft:structure_void",
                    "minecraft:end_portal_frame",
                    "minecraft:end_portal",
                    "minecraft:end_gateway",
                    "minecraft:nether_portal",
                    "minecraft:farmland",
                    "minecraft:dirt_path",
                    "minecraft:vault",
                    "minecraft:crimson_nylium",
                    "minecraft:warped_nylium",
                    "minecraft:suspicious_sand",
                    "minecraft:suspicious_gravel",
                    "minecraft:ice",
                    "minecraft:packed_ice",
                    "minecraft:blue_ice",
                    "minecraft:frosted_ice",
                    "minecraft:bee_nest",
                    "minecraft:large_amethyst_bud",
                    "minecraft:medium_amethyst_bud",
                    "minecraft:small_amethyst_bud",
                    "minecraft:amethyst_cluster",
                    "minecraft:brown_mushroom_block",
                    "minecraft:red_mushroom_block",
                    "minecraft:mushroom_stem",
                    "minecraft:grass_block",
                    "minecraft:mycelium",
                    "minecraft:podzol",
                    "minecraft:snow_block",
                    "minecraft:tube_coral_block",
                    "minecraft:brain_coral_block",
                    "minecraft:bubble_coral_block",
                    "minecraft:fire_coral_block",
                    "minecraft:horn_coral_block",
                    "minecraft:dead_tube_coral_block",
                    "minecraft:dead_brain_coral_block",
                    "minecraft:dead_bubble_coral_block",
                    "minecraft:dead_fire_coral_block",
                    "minecraft:dead_horn_coral_block",
                    "minecraft:sculk",
                    "minecraft:sculk_sensor",
                    "minecraft:calibrated_sculk_sensor",
                    "minecraft:sculk_shrieker",
                    "minecraft:sculk_vein",
                    "minecraft:sculk_catalyst",
                    "REGEX:^minecraft:music_disc.*",
                    "REGEX:^minecraft:disc_fragment.*",
                    "REGEX:.*_spawn_egg",
                    "REGEX:.*_ore",
                    "REGEX:^minecraft:infested_.*",
                    "REGEX:.*portal.*",
                    "REGEX:.*command_block.*",
                    "REGEX:.*structure.*",
                    "REGEX:.*barrier.*",
                    "REGEX:.*vault.*",
                    "REGEX:^minecraft:sculk.*",
                    "REGEX:.*debug_stick.*",
                    "REGEX:.*reinforced_deepslate.*",
                    "REGEX:.*budding_amethyst.*",
                    "REGEX:.*spawner.*",
                    "REGEX:.*suspicious.*",
                    "REGEX:.*sherd.*",
                    "REGEX:.*coral_block.*",
                    "REGEX:.*mushroom_block.*",
                    "REGEX:.*mushroom_stem.*",
                    "REGEX:.*amethyst_bud.*"
            ), (java.util.function.Supplier<String>) null, obj -> obj instanceof String);

    public static final ModConfigSpec.IntValue SWAP_INTERVAL_SECONDS = BUILDER
            .comment("Interval in seconds between player swaps")
            .defineInRange("swap-interval-seconds", 300, 10, 3600);

    public static final ModConfigSpec.IntValue SCATTER_MIN_COORD = BUILDER
            .comment("Minimum coordinate for scatter teleport")
            .defineInRange("scatter-min-coord", 10000, 0, 1000000);

    public static final ModConfigSpec.IntValue SCATTER_MAX_COORD = BUILDER
            .comment("Maximum coordinate for scatter teleport")
            .defineInRange("scatter-max-coord", 100000, 1000, 1000000);

    static final ModConfigSpec SPEC = BUILDER.build();
}
