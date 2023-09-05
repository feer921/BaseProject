package common.base.utils

import android.content.SharedPreferences
import android.os.Parcelable
import com.tencent.mmkv.MMKV
import common.base.mvx.v.defNonSyncLazy

/**
 ******************(^_^)***********************<br>
 * Author: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/7/3<br>
 * Time: 20:02<br>
 * <P>DESC:
 * 简单持久化数据存储访问者
 * </p>
 * ******************(^_^)***********************
 */
open class PrefersVisitor(protected val theSp: SharedPreferences) {

    companion object Helper {

        /**
         * 如果当前的持久化的数据需要与用户绑定
         */
        var mCurUserInfo: String = ""

        private val mCommonVisitor: PrefersVisitor by defNonSyncLazy {
            PrefersVisitor(MMKV.defaultMMKV())
        }

        @JvmStatic
        fun put(key: String, defValue: Any): Boolean = mCommonVisitor.putValue(key, defValue)

        /**
         * 缓存和当前登录用户相关的数据
         */
        @JvmStatic
        fun putOfUser(key: String, defValue: Any): Boolean = mCommonVisitor.putValue("${mCurUserInfo}_$key", defValue)

        @JvmStatic
        fun <T> get(key: String, defValue: Any): T = mCommonVisitor.getValue(key, defValue) as T

        /**
         * 获取和当前登录用户相关的数据
         */
        @JvmStatic
        fun <T> getOfUser(key: String, defValue: Any): T = mCommonVisitor.getValue("${mCurUserInfo}_$key", defValue) as T

        @JvmStatic
        fun getMmkv() = mCommonVisitor.asMmkv()
    }

    /**
     * 缓存 [key] 与对应的 [value]
     */
    open fun putValue(key: String, value: Any): Boolean {
        var edit: SharedPreferences.Editor? = null
        val mmkv = asMmkv()
        val isMmkv = mmkv != null
        if (!isMmkv) {
            edit = theSp.edit()
        }
        val isOptSuc = when (value) {
            is String -> {
                mmkv?.encode(key, value) ?: edit!!.putString(key, value).commit()
            }

            is Long -> {
                mmkv?.encode(key, value) ?: edit!!.putLong(key, value).commit()
            }

            is Int -> {
                mmkv?.encode(key, value) ?: edit!!.putInt(key, value).commit()
            }

            is Boolean -> {
                mmkv?.encode(key, value) ?: edit!!.putBoolean(key, value).commit()
            }

            is Float -> {
                mmkv?.encode(key, value) ?: edit!!.putFloat(key, value).commit()
            }

            is Double -> {
                mmkv?.encode(key, value.toFloat()) ?: edit!!.putFloat(key, value.toFloat()).commit()
            }

            is Parcelable -> {
                asMmkv()?.encode(key, value) ?: false
            }

            is MutableSet<*> -> {
                mmkv?.encode(key, value as MutableSet<String>) ?: edit!!.putStringSet(
                    key,
                    value as MutableSet<String>
                ).commit()
            }

            is ByteArray -> {
                mmkv?.encode(key, value) ?: false
            }

            else -> {
                false
            }
        }
        return isOptSuc
    }

    open fun <T> getValue(key: String, defValue: T): T {
        val mmkv = asMmkv()
        when (defValue) {
            is String -> {
                val r = mmkv?.decodeString(key, defValue) ?: theSp.getString(
                    key,
                    defValue
                )
                return r as T
            }

            is Long -> {
                val r =
                    mmkv?.decodeLong(key, defValue) ?: theSp.getLong(key, defValue)
                return r as T
            }

            is Int -> {
                val r = mmkv?.decodeInt(key, defValue) ?: theSp.getInt(key, defValue)
                return r as T
            }

            is Boolean -> {
                val r = mmkv?.decodeBool(key, defValue) ?: theSp.getBoolean(
                    key,
                    defValue
                )
                return r as T
            }

            is Float -> {
                val r =
                    mmkv?.decodeFloat(key, defValue) ?: theSp.getFloat(key, defValue)
                return r as T
            }

            is Double -> {
                val r = mmkv?.decodeFloat(key, defValue.toFloat()) ?: theSp.getFloat(
                    key,
                    defValue.toFloat()
                )
                return r.toDouble() as T
            }

            is Parcelable -> {
                //to do : 这里统一不了
            }

            is MutableSet<*> -> {
                val r = mmkv?.decodeStringSet(key, defValue as MutableSet<String>)
                    ?: theSp.getStringSet(key, defValue as MutableSet<String>)
                return r as T
            }

            is ByteArray -> {
                val r = mmkv?.decodeBytes(key, defValue)
                if (r != null) {
                    return r as T
                }
            }
        }
        throw IllegalArgumentException("$defValue is not match the support data type")
    }

//    open fun <T> getValue(key: String,defValue: T?,valueType: Class<T>) {
//
//    }

    /**
     * 当前的 [SharedPreferences] 是否就是 MMKV
     */
    open fun asMmkv(): MMKV? {
        if (theSp is MMKV) {
            return theSp
        }
        return null
    }

}