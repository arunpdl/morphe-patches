package app.frantic.patches.symfonium.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    val COMPATIBILITY_SYMFONIUM = Compatibility(
        name = "Symfonium",
        packageName = "app.symfonik.music.player",
        apkFileType = ApkFileType.APK,
        appIconColor = 0xFF6F00,
        targets = listOf(
            AppTarget(version = "14.1.0"),
        )
    )
}
