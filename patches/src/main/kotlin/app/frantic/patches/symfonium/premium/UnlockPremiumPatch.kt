package app.frantic.patches.symfonium.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.patch.bytecodePatch
import app.frantic.patches.symfonium.shared.Constants.COMPATIBILITY_SYMFONIUM
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

private const val TRUE_BOOLEAN_RETURN = """
    sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
    return-object v0
"""

@Suppress("unused")
val unlockSymfoniumPatch = bytecodePatch(
    name = "Unlock Symfonium",
    description = "Unlocks Symfonium and neutralizes the anti-tamper check that breaks playback on patched builds.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SYMFONIUM)

    execute {
        // 1. Unlock premium. Symfonium's whole app reads its license status from one
        //    state object (Lia8;) held in a StateFlow. Every instance — initial and every
        //    copy() — is built through this constructor, so forcing the premium flag (b)
        //    true and the status code (a) to the valid value (0) here unlocks everything.
        //      p1 = a (status) -> 0,  p2 = b (isPremium) -> 1
        LicenseStateConstructorFingerprint.method.addInstructions(
            0,
            """
                const/4 p1, 0x0
                const/4 p2, 0x1
            """,
        )

        // 2. Defeat anti-tamper. `Lc41;->b:Ljava/lang/Boolean;` is the "is genuine" verdict,
        //    derived from the APK signing-certificate hash. A Morphe-patched APK is re-signed,
        //    so it goes FALSE and the enforcement coroutines sabotage playback (plays a moment
        //    then pauses / "Error playing media"). This is why both this and the prior author's
        //    premium-only patch broke playback. Force the flag TRUE everywhere it is produced.
        GenuineGetterFingerprint.method.addInstructions(0, TRUE_BOOLEAN_RETURN)

        listOf(GenuineSetterMainFingerprint, GenuineSetterAltFingerprint).forEach { fp ->
            fp.method.apply {
                // Find `sput-object vX, Lc41;->b:...` and overwrite vX with TRUE just before it.
                val idx = instructions.indexOfFirst { insn ->
                    insn.opcode == Opcode.SPUT_OBJECT &&
                        ((insn as ReferenceInstruction).reference as FieldReference).let {
                            it.definingClass == "Lc41;" && it.name == "b"
                        }
                }
                require(idx >= 0) { "genuine-flag sput not found in $name" }
                val register = (getInstruction(idx) as OneRegisterInstruction).registerA
                addInstructions(
                    idx,
                    "sget-object v$register, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;",
                )
            }
        }
    }
}
