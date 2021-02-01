package com.example.test

import android.os.Binder
import android.os.Environment
import android.util.Log

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

object LogUtil {

    private val isOutputLog = true
    private val isFileLog = false
    private val TAG = "by_debug"
    private val LOG_FOLDER_NAME = "testFileLog"

    private val LOG_FILE_SIZE_LIMIT = 512 * 1024
    private val LOG_FILE_MAX_COUNT = 2
    private val LOG_FILE_NAME = "FileLog%g.txt"
    private val formatter = SimpleDateFormat("MM-dd HH:mm:ss.SSS: ", Locale.getDefault())
    private val date = Date()
    private var logger: Logger? = null
    private var fileHandler: FileHandler? = null

    fun appendLog(text: String) {
        val logFile = File(Environment.getExternalStorageDirectory().toString() +
                File.separator +
                LOG_FOLDER_NAME +
                File.separator +
                "fileLog.txt")
        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    init {
        try {
            fileHandler = FileHandler(Environment.getExternalStorageDirectory().toString()
                    + File.separator +
                    LOG_FILE_NAME, LOG_FILE_SIZE_LIMIT, LOG_FILE_MAX_COUNT, true)

            fileHandler!!.formatter = object : Formatter() {
                override fun format(r: LogRecord): String {
                    date.time = System.currentTimeMillis()

                    val ret = StringBuilder(80)
                    ret.append(formatter.format(date))
                    ret.append(r.message)
                    return ret.toString()
                }
            }

            logger = Logger.getLogger(LogUtil::class.java.name)
            logger!!.addHandler(fileHandler)
            logger!!.level = Level.ALL
            logger!!.useParentHandlers = false
            Log.d(TAG, "init success")
        } catch (e: IOException) {
            Log.d(TAG, "init failure")
        }
    }

    fun d(tag: String, msg: String) {
        if (isOutputLog) {
            Log.d(tag, msg)

            if (logger != null && isFileLog) {
                logger!!.log(Level.INFO, String.format("V/%s(%d): %s\n", tag, Binder.getCallingPid(), msg))
            }
        }
    }

    fun d(msg: String) {
        if (isOutputLog) {
            Log.d(TAG, msg)

            if (logger != null && isFileLog) {
                logger!!.log(Level.INFO, String.format("V/%s(%d): %s\n", TAG, Binder.getCallingPid(), msg))
            }
        }
    }

    fun i(tag: String, msg: String) {
        if (isOutputLog) {
            Log.i(tag, msg)

            if (logger != null && isFileLog) {
                logger!!.log(Level.INFO, String.format("V/%s(%d): %s\n", tag, Binder.getCallingPid(), msg))
            }
        }
    }

    fun i(msg: String) {
        if (isOutputLog) {
            Log.i(TAG, msg)

            if (logger != null && isFileLog) {
                logger!!.log(Level.INFO, String.format("V/%s(%d): %s\n", TAG, Binder.getCallingPid(), msg))
            }
        }
    }

    fun v(tag: String, msg: String) {
        if (isOutputLog) {
            Log.v(tag, msg)

            if (logger != null && isFileLog) {
                logger!!.log(Level.INFO, String.format("V/%s(%d): %s\n", tag, Binder.getCallingPid(), msg))
            }
        }
    }

    fun v(msg: String) {
        if (isOutputLog) {
            Log.v(TAG, msg)

            if (logger != null && isFileLog) {
                logger!!.log(Level.INFO, String.format("V/%s(%d): %s\n", TAG, Binder.getCallingPid(), msg))
            }
        }
    }

    fun w(tag: String, msg: String) {
        if (isOutputLog) {
            Log.w(tag, msg)

            if (logger != null && isFileLog) {
                logger!!.log(Level.INFO, String.format("V/%s(%d): %s\n", tag, Binder.getCallingPid(), msg))
            }
        }
    }

    fun w(msg: String) {
        if (isOutputLog) {
            Log.w(TAG, msg)

            if (logger != null && isFileLog) {
                logger!!.log(Level.INFO, String.format("V/%s(%d): %s\n", TAG, Binder.getCallingPid(), msg))
            }
        }
    }

    fun e(tag: String, msg: String) {
        if (isOutputLog) {
            Log.e(tag, msg)

            if (logger != null && isFileLog) {
                logger!!.log(Level.INFO, String.format("V/%s(%d): %s\n", tag, Binder.getCallingPid(), msg))
            }
        }
    }

    fun e(msg: String) {
        if (isOutputLog) {
            Log.e(TAG, msg)

            if (logger != null && isFileLog) {
                logger!!.log(Level.INFO, String.format("V/%s(%d): %s\n", TAG, Binder.getCallingPid(), msg))
            }
        }
    }
}
