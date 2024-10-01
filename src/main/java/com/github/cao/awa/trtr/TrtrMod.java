package com.github.cao.awa.trtr;

import com.github.cao.awa.modmdo.annotation.platform.Client;
import com.github.cao.awa.modmdo.annotation.platform.Server;
import com.github.cao.awa.trtr.components.TrtrComponentTypes;
import com.github.cao.awa.trtr.datapack.inject.item.ItemPropertyInjectManager;
import com.github.cao.awa.trtr.framework.init.ClinitFramework;
import com.github.cao.awa.trtr.function.consumer.string.object.*;
import com.github.cao.awa.trtr.recipe.TrtrRecipeSerializer;
import com.github.cao.awa.trtr.recipe.TrtrRecipeType;
import net.fabricmc.api.ModInitializer;

import java.util.function.Supplier;

@Client
@Server
public class TrtrMod implements ModInitializer {
    public static boolean enableDebugs = true;
    public static final ClinitFramework CLINIT = new ClinitFramework();
    public static ItemPropertyInjectManager itemInjectManager;

    @Override
    public void onInitialize() {
        // Initialize recipes and recipe serializers.
        TrtrRecipeType.init();
        TrtrRecipeSerializer.clinit();

        // Initialize for item injecting.
        TrtrComponentTypes.init();

        // Initialize frameworks.
        CLINIT.work();
    }

    public static void debug(Runnable debugger) {
        if (enableDebugs) {
            debugger.run();
        }
    }

    public static void debug(String message, Supplier<Object> p1, StrObjConsumer1 debugger) {
        if (enableDebugs) {
            debugger.accept(message, p1.get());
        }
    }

    public static void debug(String message, Supplier<Object> p1, Supplier<Object> p2, StrObjConsumer2 debugger) {
        if (enableDebugs) {
            debugger.accept(message, p1.get(), p2.get());
        }
    }

    public static void debug(String message, Supplier<Object> p1, Supplier<Object> p2, Supplier<Object> p3, StrObjConsumer3 debugger) {
        if (enableDebugs) {
            debugger.accept(message, p1.get(), p2.get(), p3.get());
        }
    }

    public static void debug(String message, Supplier<Object> p1, Supplier<Object> p2, Supplier<Object> p3, Supplier<Object> p4, StrObjConsumer4 debugger) {
        if (enableDebugs) {
            debugger.accept(message, p1.get(), p2.get(), p3.get(), p4.get());
        }
    }

    public static void debug(String message, Supplier<Object> p1, Supplier<Object> p2, Supplier<Object> p3, Supplier<Object> p4, Supplier<Object> p5, StrObjConsumer5 debugger) {
        if (enableDebugs) {
            debugger.accept(message, p1.get(), p2.get(), p3.get(), p4.get(), p5.get());
        }
    }
}
