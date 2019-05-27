package com.netchar.common.utils

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class WeakRef<TObject>(obj: TObject? = null) : ReadWriteProperty<Any?, TObject?> {

    private var weakReference: WeakReference<TObject>? = obj?.let { WeakReference(it) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): TObject? {
        return weakReference?.get()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: TObject?) {
        weakReference = value?.let { WeakReference(it) }
    }
}

fun <TObject> weak(obj: TObject? = null) = WeakRef(obj)