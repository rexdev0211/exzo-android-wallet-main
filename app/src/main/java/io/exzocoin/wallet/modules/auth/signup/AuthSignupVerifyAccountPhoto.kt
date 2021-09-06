package io.exzocoin.wallet.modules.auth.signup

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.zxing.MultiFormatReader
import com.google.zxing.client.android.DecodeFormatManager
import com.google.zxing.client.android.DecodeHintManager
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.camera.CameraSettings
import io.exzocoin.core.findNavController
import io.exzocoin.core.helpers.HudHelper
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.core.utils.ModuleField
import kotlinx.android.synthetic.main.fragment_auth_signup_swap.*
import kotlinx.android.synthetic.main.fragment_auth_success_email.*
import kotlinx.android.synthetic.main.fragment_auth_success_email.btnNext
import kotlinx.android.synthetic.main.fragment_auth_verify_take_photo.*
import kotlinx.android.synthetic.main.fragment_slide_intro.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class AuthSignupVerifyAccountPhoto : BaseFragment(), EasyPermissions.PermissionCallbacks {

    private val callback = BarcodeCallback {
        photoView.pause()
        //slow down fast transition to new window
        Handler(Looper.getMainLooper()).postDelayed({
            onScan(it.text)
        }, 1000)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_verify_take_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraButton.setOnClickListener {
            findNavController().navigate(R.id.authSignupVerifyAccountSuccess, null, null)
        }

        initializeFromIntent()
        openCameraWithPermission()
    }

    fun takePhoto() {
    }

    override fun onPause() {
        super.onPause()
        photoView.pause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (perms.contains( Manifest.permission.CAMERA)) {
            photoView.resume()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        HudHelper.showErrorMessage(this.requireActivity().findViewById(android.R.id.content), R.string.ScanQr_NoCameraPermission)
    }

    @AfterPermissionGranted(REQUEST_CAMERA_PERMISSION)
    private fun openCameraWithPermission() {
        val perms = arrayOf(Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(this.requireActivity(), *perms)) {
            photoView.resume()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.ScanQr_PleaseGrantCameraPermission),
                    REQUEST_CAMERA_PERMISSION, *perms)
        }
    }

    private fun onScan(address: String?) {
        this.requireActivity().setResult(RESULT_OK, Intent().apply {
            putExtra(ModuleField.SCAN_ADDRESS, address)
        })
    }

    private fun initializeFromIntent() {
        // Scan the formats the intent requested, and return the result to the calling activity.
        val settings = CameraSettings()
        photoView.cameraSettings = settings
    }
    companion object {
        const val TITLE_KEY = "title_key"
        const val DESCRIPTION_KEY = "description_key"

        private const val REQUEST_CAMERA_PERMISSION = 1

        @JvmStatic
        fun newInstance(titleResId: Int?, descriptionResId: Int) =
                AuthSignupVerifyAccountPhoto().apply {
                    arguments = Bundle(2).apply {
                        titleResId?.let { putInt(TITLE_KEY, it) }
                        putInt(DESCRIPTION_KEY, descriptionResId)
                    }
                }
    }
}
