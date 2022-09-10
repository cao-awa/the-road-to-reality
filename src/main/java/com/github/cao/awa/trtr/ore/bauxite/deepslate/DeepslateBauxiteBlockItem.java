package com.github.cao.awa.trtr.ore.bauxite.deepslate;

import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.util.registry.*;

public class DeepslateBauxiteBlockItem extends BlockItem {
    public DeepslateBauxiteBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public static void register(Block block) {
        Settings settings = new Settings();
        DeepslateBauxiteBlockItem panels = new DeepslateBauxiteBlockItem(block, settings);
        Registry.register(Registry.ITEM, DeepslateBauxiteBlock.IDENTIFIER, panels);
    }
}