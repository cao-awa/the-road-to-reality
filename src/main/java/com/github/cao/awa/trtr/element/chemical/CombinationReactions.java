package com.github.cao.awa.trtr.element.chemical;

import com.github.cao.awa.trtr.element.chemical.reaction.combination.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;

public class CombinationReactions {
    public static Map<ChemicalElement, CombinationReaction> reactions = new Object2ObjectOpenHashMap<>();

    public static void initialize() {
//        reactions.put(ChemicalElements.ELEMENT_CARBON, new CarbonCombinationReaction());
    }
}
