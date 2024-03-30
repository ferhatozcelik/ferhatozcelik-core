package com.ferhatozcelik.core.update

import com.google.android.play.core.install.model.InstallStatus

abstract class AppUpdateInstallerListener {
    open fun onStatus(@InstallStatus status: Int) {}
    abstract fun onDownloadedButNotInstalled()
    abstract fun onFailure(e: Exception)
    abstract fun onNotUpdate()
    abstract fun onCancelled()
}