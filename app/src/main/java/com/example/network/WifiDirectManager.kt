package com.example.network

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.util.Log
import com.example.model.ConnectionType
import com.example.model.DevicePeer
import com.example.model.DeviceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WifiDirectManager(private val context: Context) {

  private val TAG = "WifiDirectManager"

  private val wifiP2pManager: WifiP2pManager? =
    context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager

  private var channel: WifiP2pManager.Channel? = null

  private val _isWifiP2pEnabled = MutableStateFlow(false)
  val isWifiP2pEnabled: StateFlow<Boolean> = _isWifiP2pEnabled.asStateFlow()

  private val _availablePeers = MutableStateFlow<List<DevicePeer>>(emptyList())
  val availablePeers: StateFlow<List<DevicePeer>> = _availablePeers.asStateFlow()

  private val _connectionInfo = MutableStateFlow<WifiP2pInfo?>(null)
  val connectionInfo: StateFlow<WifiP2pInfo?> = _connectionInfo.asStateFlow()

  private var receiver: BroadcastReceiver? = null

  init {
    try {
      if (wifiP2pManager != null) {
        channel = wifiP2pManager.initialize(context, context.mainLooper, null)
        registerBroadcastReceiver()
      }
    } catch (e: Exception) {
      Log.w(TAG, "Wi-Fi Direct initialization exception (emulator or unsupported): ${e.message}")
    }
  }

  private fun registerBroadcastReceiver() {
    val intentFilter = IntentFilter().apply {
      addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
      addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
      addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
      addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    receiver = object : BroadcastReceiver() {
      @SuppressLint("MissingPermission")
      override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
          WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
            val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
            _isWifiP2pEnabled.value = (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
          }

          WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
            channel?.let { ch ->
              try {
                wifiP2pManager?.requestPeers(ch) { peers ->
                  updatePeersList(peers)
                }
              } catch (e: SecurityException) {
                Log.w(TAG, "Missing permission for requestPeers: ${e.message}")
              }
            }
          }

          WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
            val networkInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO, NetworkInfo::class.java)
            } else {
              @Suppress("DEPRECATION")
              intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
            }

            if (networkInfo?.isConnected == true) {
              channel?.let { ch ->
                wifiP2pManager?.requestConnectionInfo(ch) { info ->
                  _connectionInfo.value = info
                }
              }
            } else {
              _connectionInfo.value = null
            }
          }

          WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
            // Local device info changed
          }
        }
      }
    }

    try {
      context.registerReceiver(receiver, intentFilter)
    } catch (e: Exception) {
      Log.w(TAG, "Failed to register Wi-Fi Direct receiver: ${e.message}")
    }
  }

  private fun updatePeersList(peers: WifiP2pDeviceList) {
    val mapped = peers.deviceList.map { device ->
      val devType = when {
        device.deviceName.contains("iPhone", ignoreCase = true) -> DeviceType.IPHONE
        device.deviceName.contains("iPad", ignoreCase = true) -> DeviceType.IPAD
        device.deviceName.contains("Tablet", ignoreCase = true) -> DeviceType.TABLET_ANDROID
        device.deviceName.contains("Mac", ignoreCase = true) -> DeviceType.MAC
        device.deviceName.contains("PC", ignoreCase = true) -> DeviceType.WINDOWS_PC
        else -> DeviceType.ANDROID
      }

      DevicePeer(
        id = device.deviceAddress,
        name = device.deviceName.ifBlank { "Flow Device (${device.deviceAddress.takeLast(4)})" },
        deviceType = devType,
        connectionType = ConnectionType.WIFI_DIRECT,
        ipAddress = "192.168.49.1",
        sessionPin = "741289"
      )
    }
    _availablePeers.value = mapped
  }

  @SuppressLint("MissingPermission")
  fun startPeerDiscovery(onSuccess: () -> Unit = {}, onFailure: (Int) -> Unit = {}) {
    channel?.let { ch ->
      try {
        wifiP2pManager?.discoverPeers(ch, object : WifiP2pManager.ActionListener {
          override fun onSuccess() {
            onSuccess()
          }

          override fun onFailure(reason: Int) {
            onFailure(reason)
          }
        })
      } catch (e: SecurityException) {
        onFailure(-1)
      }
    } ?: run {
      onFailure(-2)
    }
  }

  @SuppressLint("MissingPermission")
  fun createP2pGroup(onSuccess: () -> Unit = {}, onFailure: (Int) -> Unit = {}) {
    channel?.let { ch ->
      try {
        wifiP2pManager?.createGroup(ch, object : WifiP2pManager.ActionListener {
          override fun onSuccess() {
            onSuccess()
          }

          override fun onFailure(reason: Int) {
            onFailure(reason)
          }
        })
      } catch (e: SecurityException) {
        onFailure(-1)
      }
    }
  }

  @SuppressLint("MissingPermission")
  fun connectToDevice(deviceAddress: String, onSuccess: () -> Unit = {}, onFailure: (Int) -> Unit = {}) {
    channel?.let { ch ->
      val config = WifiP2pConfig().apply {
        this.deviceAddress = deviceAddress
      }
      try {
        wifiP2pManager?.connect(ch, config, object : WifiP2pManager.ActionListener {
          override fun onSuccess() {
            onSuccess()
          }

          override fun onFailure(reason: Int) {
            onFailure(reason)
          }
        })
      } catch (e: SecurityException) {
        onFailure(-1)
      }
    }
  }

  fun unregister() {
    receiver?.let {
      try {
        context.unregisterReceiver(it)
      } catch (e: Exception) {
        // Ignored
      }
    }
  }
}
