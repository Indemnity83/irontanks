package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Guards that a placed tank can be mined back into the player's inventory.
 *
 * <p>Dropping is entirely data-driven, so the failure mode is a missing file rather than bad logic:
 * {@code BlockBehaviour.Properties.setId} derives each block's drop table key as
 * {@code irontanks:blocks/<name>}, and an unresolved key silently yields an empty table. Tanks are
 * also registered {@code requiresCorrectToolForDrops()}, which suppresses drops unless the block is
 * in a {@code mineable/*} tag a real tool matches. Both files live in the shared {@code /resources}
 * data pack, so this test reads them straight off disk.
 */
class TankBlockDropsTest {

    private static final String NAMESPACE = "irontanks";

    private static final Path RESOURCES = resourcesRoot();

    private static final Path MINEABLE_PICKAXE = RESOURCES.resolve("data/minecraft/tags/block/mineable/pickaxe.json");

    @ParameterizedTest
    @EnumSource(TankTier.class)
    void everyTankDropsItselfWhenMined(TankTier tier) {
        Path table = RESOURCES.resolve("data/" + NAMESPACE + "/loot_table/blocks/" + blockName(tier) + ".json");
        assertThat(table).as("loot table for %s tank", tier).isRegularFile();

        JsonObject json = readJson(table);
        assertThat(json.get("type").getAsString()).isEqualTo("minecraft:block");
        assertThat(droppedItems(json)).as("items dropped by the %s tank", tier).containsExactly(blockId(tier));
    }

    @ParameterizedTest
    @EnumSource(TankTier.class)
    void everyTankIsMineableWithAPickaxe(TankTier tier) {
        // Without this the requiresCorrectToolForDrops() flag means no tool ever counts as correct,
        // so the loot table above never rolls.
        assertThat(MINEABLE_PICKAXE).isRegularFile();
        assertThat(tagValues(MINEABLE_PICKAXE))
                .as("minecraft:mineable/pickaxe entries")
                .contains(blockId(tier));
    }

    @Test
    void theMineableTagOnlyAddsTanks() {
        assertThat(tagValues(MINEABLE_PICKAXE))
                .allSatisfy(
                        value -> assertThat(value).startsWith(NAMESPACE + ":").endsWith("_tank"));
    }

    /** The registry path both the block and its item use, e.g. {@code stainlesssteel_tank}. */
    private static String blockName(TankTier tier) {
        return tier.name().toLowerCase(Locale.ROOT) + "_tank";
    }

    private static String blockId(TankTier tier) {
        return NAMESPACE + ":" + blockName(tier);
    }

    /** Every {@code minecraft:item} entry name across all pools of a block loot table. */
    private static Stream<String> droppedItems(JsonObject table) {
        return array(table, "pools")
                .flatMap(pool -> array(pool.getAsJsonObject(), "entries"))
                .map(JsonElement::getAsJsonObject)
                .filter(entry -> "minecraft:item".equals(entry.get("type").getAsString()))
                .map(entry -> entry.get("name").getAsString());
    }

    private static List<String> tagValues(Path tag) {
        return array(readJson(tag), "values").map(JsonElement::getAsString).collect(Collectors.toList());
    }

    private static Stream<JsonElement> array(JsonObject owner, String member) {
        JsonElement element = owner.get(member);
        return element == null ? Stream.empty() : element.getAsJsonArray().asList().stream();
    }

    private static JsonObject readJson(Path path) {
        try {
            return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
        } catch (IOException e) {
            throw new UncheckedIOException("cannot read " + path, e);
        }
    }

    /**
     * The shared data pack at the repo root. Gradle passes it explicitly; the walk-up fallback keeps
     * the test runnable from an IDE, where the working directory is less predictable.
     */
    private static Path resourcesRoot() {
        String configured = System.getProperty("irontanks.resources.dir");
        if (configured != null) {
            return Path.of(configured);
        }
        for (Path dir = Path.of("").toAbsolutePath(); dir != null; dir = dir.getParent()) {
            Path candidate = dir.resolve("resources");
            if (Files.isDirectory(candidate.resolve("data/" + NAMESPACE))) {
                return candidate;
            }
        }
        throw new IllegalStateException("cannot locate the shared /resources data pack");
    }
}
