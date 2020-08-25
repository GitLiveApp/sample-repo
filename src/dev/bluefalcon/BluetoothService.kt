package dev.bluefalcon

import android.bluetooth.BluetoothGattService

actual class BluetoothService(val service: BluetoothGattService) {
    actual val name: String?
        get() = service.uuid.toString()
    actual val chars: List<BluetoothCharacteristic>
        get() = service.characteristics.map {
            BluetoothCharacteristic(it)
        }
    val anotherChange = "Test"
    val commitAhead = "Ahead.PartTwo"
}