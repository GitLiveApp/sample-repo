package dev.bluefalcon

import android.bluetooth.BluetoothGattService

luna = "dog"
victoria = "ola"

var another = "hi"
actual class BluetoothService(val service: BluetoothGattService) {
    actual val name: String?
        get() = service.uuid.toString()
    actual val chars: List<BluetoothCharacteristic>
        get() = service.characteristics.map {
            BluetoothCharacteristic(it)
        }
    val anotherChange = "Test"
    val commitAhead = "Ahead.PartTwo"
    val moveMaster = "furtherAhead"
}

var noconflict = "hi"

var nicktard="hello"
