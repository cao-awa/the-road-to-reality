package com.github.cao.awa.trtr.ore.lead.galena.crushed;

import com.github.cao.awa.trtr.ref.item.*;
import com.github.cao.awa.trtr.ref.item.trtr.*;
import com.github.cao.awa.trtr.type.*;
import com.github.zhuaidadaya.rikaishinikui.handler.rage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.rage.table.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;

import java.util.*;

public class CrushedGalena extends TrtrItem implements Hammerable {
    public static final Identifier IDENTIFIER = new Identifier("trtr:crushed_galena");

    public CrushedGalena(Settings settings) {
        super(settings);
    }

    public static Item register() {
        Settings settings = new Settings();
        CrushedGalena galena = new CrushedGalena(settings);
        Registry.register(Registry.ITEM, IDENTIFIER, galena);
        return galena;
    }

    @Override
    public RageTable<Item, NumberRage<Item>> products() {
        return TrtrHammerableProducts.GALENA_POWDER;
    }

    @Override
    public Set<Item> prototypes() {
        return Set.of(TrtrBlocks.GALENA_BLOCK.asItem());
    }
}