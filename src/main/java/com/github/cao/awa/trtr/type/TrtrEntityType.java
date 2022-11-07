package com.github.cao.awa.trtr.type;

import com.github.cao.awa.trtr.transmission.gearwheel.test.*;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.util.registry.Registry;

public class TrtrEntityType<T extends Entity> extends EntityType<T> {

    //    public static final EntityType<ElectricWire> ELECTRIC_WIRE;
    public static final EntityType<GearWheelEntity>         GEARWHEEL = register("trtr:gearwheel", EntityType.Builder.create(GearWheelEntity::new, SpawnGroup.MISC).setDimensions(0.35F, 0.6F).maxTrackingRange(8).trackingTickInterval(2));

    public TrtrEntityType(EntityFactory<T> factory, SpawnGroup spawnGroup, boolean saveable, boolean summonable, boolean fireImmune, boolean spawnableFarFromPlayer, ImmutableSet<Block> canSpawnInside, EntityDimensions dimensions, int maxTrackDistance, int trackTickInterval) {
        super(factory, spawnGroup, saveable, summonable, fireImmune, spawnableFarFromPlayer, canSpawnInside, dimensions, maxTrackDistance, trackTickInterval);
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return Registry.register(Registry.ENTITY_TYPE, id, type.build(id));
    }

    public static void initialize() {

    }
}

