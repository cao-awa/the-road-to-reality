package com.github.cao.awa.trtr.mixin.item.stack;

import com.github.cao.awa.sinuatum.manipulate.Manipulate;
import com.github.cao.awa.trtr.TrtrMod;
import com.github.cao.awa.trtr.components.TrtrComponentType;
import com.github.cao.awa.trtr.datapack.inject.item.ItemInjecting;
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemComponentInjecting;
import net.minecraft.component.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {
    @Unique
    private static final Logger LOGGER = LogManager.getLogger("ItemStackInjector");

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract void applyComponentsFrom(ComponentMap components);

    @Shadow
    @Final
    ComponentMapImpl components;

    @Inject(
            method = "<init>(Lnet/minecraft/item/ItemConvertible;ILnet/minecraft/component/ComponentMapImpl;)V",
            at = @At("RETURN")
    )
    public void init(ItemConvertible item, int count, ComponentMapImpl components, CallbackInfo ci) {
        // Do not apply inject when inject manager aren't prepared.
        if (TrtrMod.itemInjectManager != null) {
            // Inject to current stack.
            for (ItemInjecting inject : TrtrMod.itemInjectManager.injects(getItem())) {
                for (ItemComponentInjecting<?> component : inject.getComponents()) {
                    ComponentType<?> type = component.type();

                    // Do not append the preset value when the component is present.
                    if (!this.components.contains(type)) {
                        Object value = component.value();

                        // Use to debug, trace inject details.
                        TrtrMod.debug(
                                "Injecting: '{}' as '{}' to item '{}'",
                                () -> type,
                                () -> value,
                                () -> Registries.ITEM.getId(getItem()),
                                LOGGER::info
                        );

                        // Append preset value to item.
                        this.components.set(type, Manipulate.cast(value));
                    }
                }
            }
        }
    }

    @Inject(
            method = "getTooltip",
            at = @At(value = "RETURN")
    )
    public void getTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        if (type.isCreative() || !contains(DataComponentTypes.HIDE_TOOLTIP)) {
            List<Text> tooltip = cir.getReturnValue();

            overallTrtrComponents(componentType -> {
                tooltip.add(Text.translatable(componentType + ": " + this.components.get(componentType)));
            });
        }
    }

    @Unique
    private void overallTrtrComponents(Consumer<TrtrComponentType<?>> action) {
        for (ComponentType<?> componentsType : this.components.getTypes()) {
            if (componentsType instanceof TrtrComponentType<?> trtrComponentType) {
                action.accept(trtrComponentType);
            }
        }
    }
}
