package io.github.pvzjre.api.zombie.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.pvzjre.api.zombie.ZombieHooks;
import io.github.pvzjre.portable.lawn.Board;

/**
 * Dispatches a loaded board's "the zombies are in place" pass to {@link ZombieHooks}.
 *
 * <p>A mid-level save is restored by {@code Board::loadGame} (Board.java), which never goes through
 * {@code Zombie::ZombieInitialize}: this injection is what lets a registered zombie's spawn callback
 * run again after the load, because the save carries the zombie's reanimations but not the per-track
 * art a mod painted on them.
 */
@Mixin(Board.class)
public abstract class BoardMixin {

    /**
     * Applies the registered zombies' {@code onSpawned} callbacks at the end of a successful
     * {@code Board::loadGame}. A failed load returns {@code false} before it has restored anything, so
     * the dispatch is skipped then.
     */
    @Inject(method = "loadGame(Ljava/lang/String;)Z", at = @At("RETURN"))
    private void fabricpvz$onGameLoaded(String theFileName, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            ZombieHooks.onRestored((Board) (Object) this);
        }
    }
}
