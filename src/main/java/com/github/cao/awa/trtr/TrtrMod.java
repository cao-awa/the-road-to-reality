package com.github.cao.awa.trtr;

import com.github.cao.awa.modmdo.annotation.platform.Client;
import com.github.cao.awa.modmdo.annotation.platform.Server;
import com.github.cao.awa.trtr.recipe.TrtrRecipeSerializer;
import com.github.cao.awa.trtr.recipe.TrtrRecipeType;
import net.fabricmc.api.ModInitializer;
@Client
@Server
public class TrtrMod implements ModInitializer {
    @Override
    public void onInitialize() {
        TrtrRecipeType.init();
        TrtrRecipeSerializer.init();
    }
}
