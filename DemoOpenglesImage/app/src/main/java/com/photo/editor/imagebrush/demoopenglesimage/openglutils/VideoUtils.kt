package com.photo.editor.imagebrush.demoopenglesimage.openglutils

import android.content.Context
import android.graphics.*
import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaCodecInfo
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.os.Environment
import android.util.Log
import android.view.Surface
import com.photo.editor.imagebrush.demoopenglesimage.R
import java.io.File
import java.io.IOException


class VideoUtils(context: Context) {
    private val VERBOSE = false
    /**
     * Thuộc tính của file xuất video/avc dạng video
     */
    private val MIME_TYPE = "video/avc"

    /**
     * Bitrate của video
     */
    private val BIT_RATE = 2000000
    /**
     * Số lượng frame trên 1 giây, càng lớn sẽ càng mượt nhưng mắt thường khó cảm nhận được hết, với các video HD  hiện tại trên Youtube đang ở mức 28
     */
    private val FRAMES_PER_SECOND = 30
    private val IFRAME_INTERVAL = 5

    /** Khai báo width, height của video, các bạn có thể thay đổi thành video HD tuỳ ý muốn  */

    private val VIDEO_WIDTH = 480
    private val VIDEO_HEIGHT = 480

    // "live" state during recording
    private var mBufferInfo: MediaCodec.BufferInfo? = null
    private var mEncoder: MediaCodec? = null
    private var mMuxer: MediaMuxer? = null
     var mInputSurface: Surface? = null
    private var mTrackIndex: Int = 0
    private var mMuxerStarted: Boolean = false
    private var mFakePts: Long = 0

    private val context: Context
    private var output: File?
     var bitmap: Bitmap? = null
    /**
     * Vì ở đây ta sử dụng bitmap để vẽ, vì vậy cần có thuộc tính FILTER_BITMAP_FLAG để khi zoom ảnh không bị vỡ hoặc nhoè ảnh
     */
    private val paint = Paint(FILTER_BITMAP_FLAG)

    /**
     * Có thể định nghĩa trước số frame, ví dụ muốn tạo video 5giây, số frame = FRAMES_PER_SECOND * 5;
     */
    private var maxFrame: Int = 0
    private var currentZoom = 1.0f

    init {
        this.context = context
        try {
            output = File(Environment.getExternalStorageDirectory().absolutePath, "skew123.mp4")
            prepareEncoder(output!!)
        } catch (e: IOException) {
            e.printStackTrace()
            output = null
        }

    }

    fun prepare() {
        /** Load hình ảnh lên cho việc zoom ảnh  */
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)
        currentZoom = 1.0f
    }

    fun makeVideo(): String {
        try {
            /** Tạo ra video có thời lượng là 5giây  */
            maxFrame = 150
            for (i in 0 until maxFrame) {
                // chuẩn bị cho việc vẽ lên surface
//                drainEncoder(false)
                // Tạo ra từng frame trên surface
                generateFrame(i)
                /** Tính toán percent exported, để có thể đưa ra dialog thông báo cho người dùng, cho họ biết còn cần phải chờ bao lâu nữa  */
                val percent = 100.0f * i / maxFrame.toFloat()
                Log.d("DEBUG",percent.toString())
            }

//            drainEncoder(true)
        } finally {
            releaseEncoder()
        }

        return output!!.getAbsolutePath()
    }

    /**
     * Prepares the video encoder, muxer, and an input surface.
     */
    @Throws(IOException::class)
    private fun prepareEncoder(outputFile: File) {
        mBufferInfo = MediaCodec.BufferInfo()

        val format = MediaFormat.createVideoFormat(MIME_TYPE, VIDEO_WIDTH, VIDEO_HEIGHT)

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMES_PER_SECOND)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)
        if (VERBOSE) Log.d("DEBUG", "format: $format")

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE)
        mEncoder!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mInputSurface = mEncoder!!.createInputSurface()
//        mEncoder!!.start()
//
//        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
//        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
//        // obtained from the encoder after it has started processing data.
//        //
//        // We're not actually interested in multiplexing audio.  We just want to convert
//        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
//        if (VERBOSE) Log.d("DEBUG", "output will go to $outputFile")
//        mMuxer = MediaMuxer(outputFile.toString(),
//                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
//
//        mTrackIndex = -1
//        mMuxerStarted = false
    }

    /**
     * Releases encoder resources.  May be called after partial / failed initialization.
     */
    private fun releaseEncoder() {
        if (VERBOSE) Log.d("DEBUG", "releasing encoder objects")
        if (mEncoder != null) {
            mEncoder!!.stop()
            mEncoder!!.release()
            mEncoder = null
        }
        if (mInputSurface != null) {
            mInputSurface!!.release()
            mInputSurface = null
        }
        if (mMuxer != null) {
            mMuxer!!.stop()
            mMuxer!!.release()
            mMuxer = null
        }
    }

    private fun drainEncoder(endOfStream: Boolean) {
        /** Thời gian delay giữa 2 frame  */
        val TIMEOUT_USEC = 2500
        if (VERBOSE) Log.d("DEBUG", "drainEncoder($endOfStream)")

        if (endOfStream) {
            if (VERBOSE) Log.d("DEBUG", "sending EOS to encoder")
            mEncoder!!.signalEndOfInputStream()
        }

        var encoderOutputBuffers = mEncoder!!.outputBuffers
        while (true) {
            val encoderStatus = mEncoder!!.dequeueOutputBuffer(mBufferInfo!!, TIMEOUT_USEC.toLong())
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break      // out of while
                } else {
                    if (VERBOSE) Log.d("DEBUG", "no output available, spinning to await EOS")
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder!!.outputBuffers
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    throw RuntimeException("format changed twice")
                }
                val newFormat = mEncoder!!.outputFormat
                Log.d("DEBUG", "encoder output format changed: $newFormat")

                // now that we have the Magic Goodies, start the muxer
                mTrackIndex = mMuxer!!.addTrack(newFormat)
                mMuxer!!.start()
                mMuxerStarted = true
            } else if (encoderStatus < 0) {
                Log.d("DEBUG", "unexpected result from encoder.dequeueOutputBuffer: $encoderStatus")
                // let's ignore it
            } else {
                val encodedData = encoderOutputBuffers[encoderStatus]
                        ?: throw RuntimeException("encoderOutputBuffer " + encoderStatus +
                                " was null")

                if (mBufferInfo!!.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d("DEBUG", "ignoring BUFFER_FLAG_CODEC_CONFIG")
                    mBufferInfo!!.size = 0
                }

                if (mBufferInfo!!.size != 0) {
                    if (!mMuxerStarted) {
                        throw RuntimeException("muxer hasn't started")
                    }

                    // adjust the ByteBuffer values to match BufferInfo
                    encodedData.position(mBufferInfo!!.offset)
                    encodedData.limit(mBufferInfo!!.offset + mBufferInfo!!.size)
                    mBufferInfo!!.presentationTimeUs = mFakePts
                    mFakePts += 1000000L / FRAMES_PER_SECOND

                    mMuxer!!.writeSampleData(mTrackIndex, encodedData, mBufferInfo!!)
                    if (VERBOSE) Log.d("DEBUG", "sent " + mBufferInfo!!.size + " bytes to muxer")
                }

                mEncoder!!.releaseOutputBuffer(encoderStatus, false)

                if (mBufferInfo!!.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    if (!endOfStream) {
                        Log.d("DEBUG", "reached end of stream unexpectedly")
                    } else {
                        if (VERBOSE) Log.d("DEBUG", "end of stream reached")
                    }
                    break      // out of while
                }
            }
        }
    }

    /**
     * Vẽ từng frame theo thời gian, ví dụ ở giây 1 vẽ ảnh với zoom 1.1f, giây 2 vẽ ảnh zoom với 1.2f
     */
    private fun generateFrame(frameNum: Int) {
        /** Khởi tạo canvas để vẽ từng frame cho video  */
        val canvas = mInputSurface!!.lockCanvas(null)
        canvas.drawColor(Color.BLACK)
        try {
            /** Trong 5 giây ta sẽ vẽ hình ảnh zoom từ 1.0 -> 1.3  */
            /** Như vậy ta sẽ phải tính toán trong thời gian thứ i hình ảnh đang zoom ở mức bao nhiêu  */
            val currentDuration = computePresentationTimeNsec(frameNum)
            val currentZoom = 0.3f - currentDuration * (1.3f - 1.0f)
            val rotate = 0f +currentDuration*90
            Log.d("DEBUGSCALE", rotate.toString())
            val matrix = Matrix()
//            matrix.postTranslate(0f,frameNum.toFloat())
//            matrix.postRotate(frameNum.toFloat(),VIDEO_WIDTH/2f,VIDEO_HEIGHT/2f)
            matrix.postSkew(currentZoom,currentZoom)
//            matrix.setScale(currentZoom, currentZoom,VIDEO_WIDTH/2f,VIDEO_HEIGHT/2f)
            canvas.drawBitmap(bitmap, matrix, paint)
        } finally {
            mInputSurface!!.unlockCanvasAndPost(canvas)
        }
    }

    /**
     * Vì thời gian ở đây được tính toán dựa trên nanosecond, nên ta sẽ tính toán thời gian của video khi vẽ ở frame thứ i, thời gian video được tính toán ra milisecond
     */
    private fun computePresentationTimeNsec(frameIndex: Int): Float {
        return frameIndex/ 150f
    }
}