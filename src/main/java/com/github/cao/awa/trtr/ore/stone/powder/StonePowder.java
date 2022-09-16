package com.github.cao.awa.trtr.ore.stone.powder;

import com.github.cao.awa.trtr.ref.item.trtr.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;

public class StonePowder extends TrtrItem {
    public static final Identifier IDENTIFIER = new Identifier("trtr:stone_powder");

    public StonePowder(Settings settings) {
        super(settings);
    }

    public static Item register() {
        Settings settings = new Settings();
        StonePowder stonePowder = new StonePowder(settings);
        Registry.register(Registry.ITEM, IDENTIFIER, stonePowder);
        return stonePowder;
    }
}