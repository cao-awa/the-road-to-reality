package com.github.cao.awa.trtr.item.spear;

import com.github.cao.awa.apricot.anntation.Auto;
import com.github.cao.awa.trtr.item.TrtrItem;
import net.minecraft.util.Identifier;

@Auto
public class FlintSpearItem extends TrtrItem {
    @Auto
    public static final Identifier IDENTIFIER = Identifier.tryParse("trtr:flint_spear");

    @Auto
    public FlintSpearItem(Settings settings) {
        super(settings);
    }
}