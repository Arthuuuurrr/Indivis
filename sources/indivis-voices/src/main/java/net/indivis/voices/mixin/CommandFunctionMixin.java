package net.indivis.voices.mixin;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import net.indivis.voices.compat.NativeSequenceGuard;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandFunction.class)
public interface CommandFunctionMixin {
    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static <T extends AbstractServerCommandSource<T>> void indivisVoices$guardDispatcher(
            Identifier id, CommandDispatcher<T> dispatcher, T source, List<String> lines,
            CallbackInfoReturnable<CommandFunction<T>> cir) {
        if (!NativeSequenceGuard.FUNCTION.equals(id.toString())) return;
        var guarded = NativeSequenceGuard.guard(id.toString(), lines);
        if (guarded != lines) {
            LoggerFactory.getLogger("indivis_voices").info(
                "Voices : garde anti-cascade du core 1.5.7 active (38 branches, une étape par appel)");
            // Re-entry is bounded: the guarded fingerprint is different and is never rewritten.
            cir.setReturnValue(CommandFunction.create(id, dispatcher, source, guarded));
        } else if (!NativeSequenceGuard.GUARDED_SHA256.equals(NativeSequenceGuard.fingerprint(lines))) {
            LoggerFactory.getLogger("indivis_voices").warn(
                "Voices : dispatcher non audité ({}), garde non appliquée ; conserver la recette texte",
                NativeSequenceGuard.fingerprint(lines));
        }
    }
}
