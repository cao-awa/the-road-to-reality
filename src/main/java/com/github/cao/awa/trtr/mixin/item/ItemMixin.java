package com.github.cao.awa.trtr.mixin.item;

import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipe;
import com.github.cao.awa.trtr.recipe.handcraft.result.HandcraftRecipeResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    private static final Logger LOGGER = LogManager.getLogger("TrtrHandcrafting");

    @Inject(
            method = "use",
            at = @At("HEAD")
    )
    public void handcraftTick(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        HandcraftRecipe.canCraft(world, user).ifPresent(recipe -> {
            if (!world.isClient()) {
                // Do not stratup twice.
                if (user.getMainHandStack() != null && hand == Hand.OFF_HAND) {
                    return;
                }

                // Startup the crafting.
                if (user.getMainHandStack() != null) {
                    user.setCurrentHand(Hand.MAIN_HAND);
                } else if (user.getOffHandStack() != null) {
                    user.setCurrentHand(Hand.OFF_HAND);
                } else {
                    // Doesn't taking any items in hand, craft will always failure.
                    return;
                }

                // Init crafting input.
                recipe.tickCraft(
                        world,
                        user,
                        results -> {
                            // Should not resulting in here, handcraft cannot finish in 0 ticks.
                            LOGGER.warn(
                                    new Throwable("The handcrafting finishing unexpectedly at startup (0 usage ticks)").fillInStackTrace()
                            );
                        }
                );
            }
        });
    }

    @Inject(
            method = "usageTick",
            at = @At("HEAD")
    )
    public void usageTicking(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci) {
        if (user instanceof PlayerEntity player) {
            // Keeping crafts.
            HandcraftRecipe.canCraft(world, player).ifPresent(recipe -> {
                if (!world.isClient()) {
                    recipe.tickCraft(
                            world,
                            player,
                            results -> {
                                // Feedback the craft result to player.
                                for (HandcraftRecipeResult result : results) {
                                    player.giveItemStack(result.result(player));
                                }
                            }
                    );
                }
            });
        }
    }

    @Inject(
            method = "onStoppedUsing",
            at = @At("HEAD")
    )
    public void stopUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (!world.isClient() && user instanceof PlayerEntity player) {
            // Stop the crafts when stopped using item.
            HandcraftRecipe.stopCraft(world, player);
        }
    }
}
