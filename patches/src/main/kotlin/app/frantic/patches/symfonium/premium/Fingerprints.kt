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
