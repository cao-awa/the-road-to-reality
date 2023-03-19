package com.github.cao.awa.trtr.block.ore.nuclear.uranium.pitchblende;

import com.github.cao.awa.apricot.anntation.Auto;
import com.github.cao.awa.trtr.annotation.data.gen.DataGen;
import com.github.cao.awa.trtr.annotation.mine.PickaxeMining;
import com.github.cao.awa.trtr.block.TrtrBlock;
import com.github.cao.awa.trtr.data.gen.loot.GenericBlockLootProvider;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;

@Auto
@PickaxeMining(MiningLevels.STONE)
public class Pitchblende extends TrtrBlock {
    @Auto
    public static final Identifier IDENTIFIER = Identifier.of("trtr",
                                                              "pitchblende"
    );

    @Auto
    public static final FabricBlockSettings SETTINGS = FabricBlockSettings.of(Material.STONE,
                                                                              MapColor.GRAY
                                                                          )
                                                                          .requiresTool()
                                                                          .strength(1.5F,
                                                                                    6.0F
                                                                          );

    @Auto
    public static BlockItem ITEM;

    @DataGen
    public static GenericBlockLootProvider LOOT;

    @Auto
    public Pitchblende(Settings settings) {
        super(settings);
    }
}