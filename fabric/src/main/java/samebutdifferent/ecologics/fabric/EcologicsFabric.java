package samebutdifferent.ecologics.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import samebutdifferent.ecologics.Ecologics;
import net.fabricmc.api.ModInitializer;
import samebutdifferent.ecologics.registry.ModBlocks;
import samebutdifferent.ecologics.registry.ModEntityTypes;
import samebutdifferent.ecologics.registry.fabric.ModConfigFabric;

import java.util.HashMap;
import java.util.Map;

public class EcologicsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoConfig.register(ModConfigFabric.class, GsonConfigSerializer::new);
        Ecologics.init();
        registerEntityAttributes();
        addFeatures();
        replaceFeatures();
        addSpawns();
        Ecologics.commonSetup();
    }

    public void registerEntityAttributes() {
        Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> attributes = new HashMap<>();
        Ecologics.registerEntityAttributes(attributes);
        attributes.forEach(FabricDefaultAttributeRegistry::register);
    }

    public void addFeatures() {
        ModConfigFabric config = AutoConfig.getConfigHolder(ModConfigFabric.class).getConfig();
        if (config.beach.generateCoconutTrees) {
            BiomeModifications.addFeature(
                    (biomeSelector) -> biomeSelector.getBiomeKey().equals(Biomes.BEACH),
                    GenerationStep.Decoration.VEGETAL_DECORATION,
                    getPlacedFeatureKey("trees_beach")
            );
        }
        if (config.beach.generateSeashells) {
            BiomeModifications.addFeature(
                    (biomeSelector) -> biomeSelector.getBiomeKey().equals(Biomes.BEACH),
                    GenerationStep.Decoration.VEGETAL_DECORATION,
                    getPlacedFeatureKey("seashell")
            );
        }
        if (config.snowy.generateThinIcePatches) {
            BiomeModifications.addFeature(
                    (biomeSelector) -> biomeSelector.getBiomeKey().equals(Biomes.FROZEN_RIVER) || biomeSelector.getBiomeKey().equals(Biomes.FROZEN_OCEAN) || biomeSelector.getBiomeKey().equals(Biomes.SNOWY_PLAINS),
                    GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
                    getPlacedFeatureKey("thin_ice_patch")
            );
        }
        if (config.desert.generatePricklyPears) {
            BiomeModifications.addFeature(
                    (biomeSelector) -> biomeSelector.getBiomeKey().equals(Biomes.DESERT),
                    GenerationStep.Decoration.VEGETAL_DECORATION,
                    getPlacedFeatureKey("prickly_pear")
            );
        }
    }

    public void replaceFeatures() {
        ModConfigFabric config = AutoConfig.getConfigHolder(ModConfigFabric.class).getConfig();
        BiomeModifications.create(new ResourceLocation(Ecologics.MOD_ID, "remove_oak_trees")).add(ModificationPhase.REPLACEMENTS, biomeSelectionContext -> (biomeSelectionContext.getBiomeKey().equals(Biomes.PLAINS)), (c) -> {
            if (config.plains.generateWalnutTrees) {
                c.getGenerationSettings().removeBuiltInFeature(VegetationPlacements.TREES_PLAINS.value());
                c.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getPlacedFeatureKey("trees_walnut"));
            }
        });
    }

    public void addSpawns() {
        ModConfigFabric config = AutoConfig.getConfigHolder(ModConfigFabric.class).getConfig();
        if (config.snowy.spawnPenguins) {
            BiomeModifications.addSpawn(
                    (biomeSelector) -> biomeSelector.getBiomeKey().equals(Biomes.FROZEN_RIVER) || biomeSelector.getBiomeKey().equals(Biomes.FROZEN_OCEAN) || biomeSelector.getBiomeKey().equals(Biomes.SNOWY_PLAINS),
                    MobCategory.CREATURE,
                    ModEntityTypes.PENGUIN.get(),
                    2,
                    4,
                    5
            );
        }
        if (config.desert.spawnCamels) {
            BiomeModifications.addSpawn(
                    (biomeSelector) -> biomeSelector.getBiomeKey().equals(Biomes.DESERT),
                    MobCategory.CREATURE,
                    ModEntityTypes.CAMEL.get(),
                    1,
                    1,
                    1
            );
        }
        if (config.plains.spawnSquirrels) {
            BiomeModifications.addSpawn(
                    (biomeSelector) -> biomeSelector.getBiomeKey().equals(Biomes.PLAINS),
                    MobCategory.CREATURE,
                    ModEntityTypes.SQUIRREL.get(),
                    10,
                    2,
                    3
            );
        }
    }

    private ResourceKey<PlacedFeature> getPlacedFeatureKey(String key) {
        return ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(Ecologics.MOD_ID, key));
    }
}