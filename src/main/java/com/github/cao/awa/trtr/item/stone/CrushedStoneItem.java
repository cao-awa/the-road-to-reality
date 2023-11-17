package com.github.cao.awa.trtr.item.stone;

import com.github.cao.awa.apricot.anntation.Auto;
import com.github.cao.awa.trtr.item.handcraft.CraftingItem;
import net.minecraft.util.Identifier;

@Auto
public class CrushedStoneItem extends CraftingItem {
    @Auto
    public static final Identifier IDENTIFIER = Identifier.tryParse("trtr:crushed_stone");

    @Auto
    public CrushedStoneItem(Settings settings) {
        super(settings);
    }
}
