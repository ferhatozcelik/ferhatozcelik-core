package com.ferhatozcelik.core.update

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * internal-app-sharing
 * @reference https://developer.android.com/guide/playcore/in-app-updates#internal-app-sharing
 */
class InAppUpdateInstallerManager(
    private val activity: Activity,
    private val appUpdateRequestCode: Int = APP_UPDATE_REQUEST_CODE
) : AppUpdateInstallerManager {

    private var listener: AppUpdateInstallerListener? = null

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(activity) }

    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    installState.installStatus() == InstallStatus.DOWNLOADED -> {
                        listener?.onDownloadedButNotInstalled()
                    }
                    installState.installStatus() == InstallStatus.INSTALLED -> {
                        appUpdateManager.unregisterListener(this)
                    }
                    else -> {
                        listener?.onStatus(installState.installStatus())
                    }
                }
            }
        }
    }

    override fun addAppUpdateListener(listener: AppUpdateInstallerListener?) {
        this.listener = listener
    }

    override fun completeUpdate() = appUpdateManager.completeUpdate()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == appUpdateRequestCode) {
            if (resultCode != Activity.RESULT_OK) {
                // App Update failed, please try again on the next app launch.
                listener?.onCancelled()
            }
        }
    }

    override fun startCheckUpdate() {

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnFailureListener {
            listener?.onFailure(it)
        }

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Request the update.
                try {
                    val installType = when {
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
                        else -> null
                    }

                    if (installType == AppUpdateType.FLEXIBLE) {
                        appUpdateManager.registerListener(appUpdatedListener)
                    }

                    installType?.let {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            installType,
                            activity,
                            appUpdateRequestCode
                        )
                    }
                } catch (e: IntentSender.SendIntentException) {
                    listener?.onFailure(e)
                }
            } else {
                listener?.onNotUpdate()
            }
        }
    }

    override fun resumeCheckUpdate(@AppUpdateType updateType: Int) {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                // If the update is downloaded but not installed, notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    listener?.onDownloadedButNotInstalled()
                }

                // Check if Immediate update is required
                try {

                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            updateType,
                            activity,
                            appUpdateRequestCode
                        )
                    }

                } catch (e: IntentSender.SendIntentException) {
                    listener?.onFailure(e)
                }

            }
    }

    companion object {
        const val APP_UPDATE_REQUEST_CODE = 1991
    }
}