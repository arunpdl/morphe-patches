package app.frantic.patches.symfonium.premium

import app.morphe.patcher.Fingerprint

// The license-state holder (obfuscated `Lia8;` in 14.1.0): a Kotlin data class
//   (a:Int, b:Boolean, c:String, d:Boolean, e:Lya8;, f:Lwp8;)
// where `b` is the "is licensed / premium" flag and `a` is a status code (0 == valid).
// Every state object — the initial one and every copy() — is built through this
// constructor, so it is the single chokepoint for forcing premium.
object LicenseStateConstructorFingerprint : Fingerprint(
    definingClass = "Lia8;",
    name = "<init>",
    returnType = "V",
    parameters = listOf("I", "Z", "Ljava/lang/String;", "Z", "Lya8;", "Lwp8;"),
)

// Anti-tamper "is genuine" verdict. `Lc41;->b:Ljava/lang/Boolean;` is a cached flag
// computed from the APK signing-certificate hash. A Morphe-patched APK is re-signed,
// so the flag becomes FALSE, and the enforcement coroutines (pg3/qg3) then sabotage
// playback. Two methods write the flag; forcing both to store TRUE keeps readers genuine.
object GenuineGetterFingerprint : Fingerprint(
    definingClass = "Lc41;",
    name = "O",
    returnType = "Ljava/lang/Boolean;",
    parameters = emptyList(),
)

object GenuineSetterMainFingerprint : Fingerprint(
    definingClass = "Lc41;",
    name = "f0",
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Lapp/symfonik/Application;", "Lxo1;"),
)

object GenuineSetterAltFingerprint : Fingerprint(
    definingClass = "Lyv1;",
    name = "u",
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Ljava/lang/Object;"),
)
