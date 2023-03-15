package com.github.cao.awa.trtr.block.example.model;

import com.github.cao.awa.trtr.block.TrtrBlocks;
import com.github.cao.awa.trtr.block.example.ExampleBlock;
import com.github.cao.awa.trtr.framework.data.gen.model.TrtrBlockModelProvider;
import com.github.cao.awa.trtr.identifier.namespane.Namespace;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.client.BlockStateModelGenerator;

public class ExampleModel extends TrtrBlockModelProvider {
    public ExampleModel(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        System.out.println("Custom model generator");
        blockStateModelGenerator.registerSimpleCubeAll(TrtrBlocks.get(ExampleBlock.IDENTIFIER));
        blockStateModelGenerator.registerParentedItemModel(TrtrBlocks.get(ExampleBlock.IDENTIFIER),
                                                           Namespace.subSpace(ExampleBlock.IDENTIFIER,
                                                                              "block"
                                                           )
        );
    }
}