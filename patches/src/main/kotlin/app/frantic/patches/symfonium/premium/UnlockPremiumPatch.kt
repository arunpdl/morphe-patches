package app.frantic.patches.symfonium.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.frantic.patches.symfonium.shared.Constants.COMPATIBILITY_SYMFONIUM

@Suppress("unused")
val unlockSymfoniumPatch = bytecodePatch(
    name = "Unlock Symfonium",
    description = "Unlocks the full version of Symfonium by forcing the license state to premium.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SYMFONIUM)

    execute {
        // Symfonium's trial is fully functional (playback included); only a paywall
        // gates the app once the trial ends. The whole app reads its license status
        // from a single state object held in a StateFlow. Rather than mutating Firebase
        // remote config (which broke playback in the prior binarymend patch, issue #31),
        // force the premium flag at the one place every state object is constructed:
        //   p1 = a (status code) -> 0  (valid)
        //   p2 = b (isPremium)   -> 1  (true)
        // The constructor's own `iput`s then store these, so every state — initial and
        // every copy() — reports premium. Nothing else (playback, remote config) is touched.
        LicenseStateConstructorFingerprint.method.addInstructions(
            0,
            """
                const/4 p1, 0x0
                const/4 p2, 0x1
            """,
        )
    }
}
