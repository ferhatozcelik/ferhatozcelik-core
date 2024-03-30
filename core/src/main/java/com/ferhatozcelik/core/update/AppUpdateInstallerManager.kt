package com.ferhatozcelik.core.update

import android.content.Intent
import com.google.android.gms.tasks.Task
import com.google.android.play.core.install.model.AppUpdateType

interface AppUpdateInstallerManager {
    fun startCheckUpdate()
    fun resumeCheckUpdate(@AppUpdateType updateType: Int)
    fun completeUpdate(): Task<Void>
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun addAppUpdateListener(listener: AppUpdateInstallerListener?)
}