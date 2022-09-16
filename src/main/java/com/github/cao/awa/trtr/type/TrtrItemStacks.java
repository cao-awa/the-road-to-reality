package com.github.cao.awa.trtr.type;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.item.*;

public class TrtrItemStacks {
    public static final ObjectArrayList<ItemStack> ORES = EntrustParser.operation(new ObjectArrayList<>(), stacks -> {
        stacks.add(TrtrBlocks.ACANTHITE_BLOCK.asItem().getDefaultStack());
        stacks.add(TrtrBlocks.DEEPSLATE_ACANTHITE_BLOCK.asItem().getDefaultStack());
        stacks.add(TrtrBlocks.BAUXITE_BLOCK.asItem().getDefaultStack());
        stacks.add(TrtrBlocks.DEEPSLATE_BAUXITE_BLOCK.asItem().getDefaultStack());
        stacks.add(TrtrBlocks.GALENA_BLOCK.asItem().getDefaultStack());
        stacks.add(TrtrBlocks.DEEPSLATE_GALENA_BLOCK.asItem().getDefaultStack());
        stacks.add(TrtrBlocks.ORTHOCLASE.asItem().getDefaultStack());
        stacks.add(TrtrBlocks.ALBITE_BLOCK.asItem().getDefaultStack());
        stacks.add(TrtrBlocks.ALUNITE_BLOCK.asItem().getDefaultStack());
    });

    public static final ObjectArrayList<ItemStack> CRUSHED_ORES = EntrustParser.operation(new ObjectArrayList<>(), stacks -> {
        stacks.add(TrtrItems.CRUSHED_ACANTHITE.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_STONE.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_GALENA.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_COAL.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_BAUXITE.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_HEMATITE.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_DEEPSLATE.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_DEEPSLATE_COAL.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_DEEPSLATE_BAUXITE.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_DEEPSLATE_GALENA.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_DEEPSLATE_ACANTHITE.getDefaultStack());
        stacks.add(TrtrItems.CRUSHED_DEEPSLATE_HEMATITE.getDefaultStack());
    });

    public static final ObjectArrayList<ItemStack> ORE_POWDERS = EntrustParser.operation(new ObjectArrayList<>(), stacks -> {
        stacks.add(TrtrItems.STONE_POWDER.getDefaultStack());
        stacks.add(TrtrItems.DEEPSLATE_POWDER.getDefaultStack());
        stacks.add(TrtrItems.ACANTHITE_POWDER.getDefaultStack());
        stacks.add(TrtrItems.BAUXITE_POWDER.getDefaultStack());
        stacks.add(TrtrItems.GALENA_POWDER.getDefaultStack());
        stacks.add(TrtrItems.HEMATITE_POWDER.getDefaultStack());
        stacks.add(TrtrItems.COAL_POWDER.getDefaultStack());
    });

    public static final ObjectArrayList<ItemStack> TOOLS = EntrustParser.operation(new ObjectArrayList<>(), stacks -> {
        stacks.add(TrtrItems.WOODEN_HAMMER.getDefaultStack());
        stacks.add(TrtrItems.STONE_HAMMER.getDefaultStack());
        stacks.add(TrtrItems.IRON_HAMMER.getDefaultStack());
        stacks.add(TrtrItems.THERMOMETER.getDefaultStack());
    });
}