package com.github.cao.awa.trtr.type;

import com.github.cao.awa.trtr.air.*;
import com.github.cao.awa.trtr.cooking.container.pan.*;
import com.github.cao.awa.trtr.cooking.container.pot.*;
import com.github.cao.awa.trtr.mud.blower.*;
import com.github.cao.awa.trtr.mud.stove.*;
import com.github.cao.awa.trtr.ore.nuclear.uranium.*;
import com.github.cao.awa.trtr.power.photovoltaic.panels.*;
import com.github.cao.awa.trtr.ref.block.air.vanilla.*;
import com.github.cao.awa.trtr.ref.block.iron.*;
import com.github.cao.awa.trtr.ref.block.trtr.*;
import com.github.cao.awa.trtr.ref.block.trtr.slab.*;
import com.github.cao.awa.trtr.transmission.gearwheel.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.google.common.collect.*;
import com.mojang.datafixers.types.*;
import com.mojang.logging.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.datafixer.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;

import java.util.*;

import static com.github.cao.awa.trtr.type.TrtrBlocks.*;

public class TrtrBlockEntityType<T extends BlockEntity> extends BlockEntityType<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockEntityType.BlockEntityFactory<? extends T> factory;
    private final Set<Block> blocks;

    public TrtrBlockEntityType(BlockEntityType.BlockEntityFactory<? extends T> factory, Set<Block> blocks, Type<?> type) {
        super(
                factory,
                blocks,
                type
        );
        this.factory = factory;
        this.blocks = blocks;
    }

    public static void initialize() {

    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, Builder<T> builder) {
        if (builder.blocks()
                   .isEmpty()) {
            LOGGER.warn(
                    "Block entity type {} requires at least one valid block to be defined!",
                    id
            );
        }

        Type<?> type = Util.getChoiceType(
                TypeReferences.BLOCK_ENTITY,
                id
        );
        return Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                id,
                builder.build(type)
        );
    }

    @Nullable
    public T instantiate(BlockPos pos, BlockState state) {
        return this.factory.create(
                pos,
                state
        );
    }

    public boolean supports(BlockState state) {
        return this.blocks.contains(state.getBlock());
    }

    @Nullable
    public T get(BlockView world, BlockPos pos) {
        T blockEntity = (T) world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.getType() == this ? blockEntity : null;
    }

    @FunctionalInterface
    private interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

    public record Builder<T extends BlockEntity>(BlockEntityType.BlockEntityFactory<? extends T> factory, Set<Block> blocks) {
        public static <T extends BlockEntity> Builder<T> create(BlockEntityType.BlockEntityFactory<? extends T> factory, Block... blocks) {
            return new Builder<>(
                    factory,
                    ImmutableSet.copyOf(blocks)
            );
        }

        public BlockEntityType<T> build(Type<?> type) {
            return new BlockEntityType<>(
                    this.factory,
                    this.blocks,
                    type
            );
        }
    }

    public static final BlockEntityType<PhotovoltaicPanelsBlockEntity> PHOTOVOLTAIC_PANELS = create(
            "trtr:photovoltaic_panels",
            Builder.create(
                    PhotovoltaicPanelsBlockEntity::new,
                    TrtrBlocks.PHOTOVOLTAIC_PANELS
            )
    );


    public static final BlockEntityType<WaterVaporBlockEntity> WATER_VAPOR = create(
            "trtr:water_vapor",
            Builder.create(
                    WaterVaporBlockEntity::new,
                    WATER_VAPOR_BLOCk
            )
    );


    public static final BlockEntityType<GearWheelBlockEntity> GEAR_WHEEL = create(
            "trtr:gear_wheel",
            Builder.create(
                    GearWheelBlockEntity::new,
                    GEAR_WHEEL_BLOCK
            )
    );


    public static final BlockEntityType<UraniumBlockEntity> URANIUM = create(
            "trtr:pitchblende",
            Builder.create(
                    UraniumBlockEntity::new,
                    PITCHBLENDE_BLOCK,
                    DEEPSLATE_PITCHBLENDE_BLOCK,
                    CARNOTITE_BLOCK,
                    DEEPSLATE_CARNOTITE_BLOCK,
                    AUTUNITE_BLOCK,
                    DEEPSLATE_AUTUNITE_BLOCK
            )
    );


    public static final BlockEntityType<PotBlockEntity> POT = create(
            "trtr:pot",
            Builder.create(
                    PotBlockEntity::new,
                    TrtrBlocks.POT
            )
    );


    public static final BlockEntityType<PanBlockEntity> PAN = create(
            "trtr:pan",
            Builder.create(
                    PanBlockEntity::new,
                    TrtrBlocks.PAN
            )
    );


    public static final BlockEntityType<AirBlockEntity> AIR = create(
            "trtr:air",
            Builder.create(
                    AirBlockEntity::new,
                    TEST_AIR
            )
    );


    public static final BlockEntityType<AirBlockEntity> PEBBLE = create(
            "trtr:pebble",
            Builder.create(
                    AirBlockEntity::new,
                    LOOSE_PEBBLE_BLOCK
            )
    );
    public static final BlockEntityType<TrtrOreBlockEntity> ORE = create(
            "trtr:ore",
            Builder.create(
                    TrtrOreBlockEntity::new,
                    EntrustEnvironment.operation(
                            new Block[TrtrOreBlock.ORES.size()],
                            array -> {
                                for (int i = 0; i < array.length; i++) {
                                    array[i] = TrtrOreBlock.ORES.get(i);
                                }
                            }
                    )
            )
    );
    public static final BlockEntityType<MudStoveBlockEntity> MUD_STOVE = create(
            "trtr:mud_stove",
            Builder.create(
                    MudStoveBlockEntity::new,
                    TrtrBlocks.MUD_STOVE
            )
    );

    public static final BlockEntityType<MudBlowerBlockEntity> MUD_BLOWER = create(
            "trtr:mud_blower",
            Builder.create(
                    MudBlowerBlockEntity::new,
                    TrtrBlocks.MUD_BLOWER
            )
    );

    public static final BlockEntityType<TrtrConventionalSlabEntity> SLAB_ENTITY = create(
            "trtr:slab",
            Builder.create(
                    TrtrConventionalSlabEntity::new,
                    EntrustEnvironment.operation(
                            new Block[TrtrSlabBlock.SLABS.size()],
                            array -> {
                                for (int i = 0; i < array.length; i++) {
                                    array[i] = TrtrSlabBlock.SLABS.get(i);
                                }
                            }
                    )
            )
    );

    public static final BlockEntityType<IronBlockEntity> IRON_BLOCK = create(
            "minecraft:iron",
            Builder.create(
                    IronBlockEntity::new,
                    Blocks.IRON_BLOCK
            )
    );
}

