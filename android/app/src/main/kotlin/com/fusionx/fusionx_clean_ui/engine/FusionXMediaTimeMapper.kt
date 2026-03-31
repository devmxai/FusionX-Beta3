package com.fusionx.fusionx_clean_ui.engine

import kotlin.math.roundToInt
import kotlin.math.roundToLong

interface FusionXMediaTimeMapper {
    fun sourceToMediaTimeUs(sourceTimeUs: Long): Long

    fun mediaToSourceTimeUs(mediaTimeUs: Long): Long

    object Identity : FusionXMediaTimeMapper {
        override fun sourceToMediaTimeUs(sourceTimeUs: Long): Long = sourceTimeUs.coerceAtLeast(0L)

        override fun mediaToSourceTimeUs(mediaTimeUs: Long): Long = mediaTimeUs.coerceAtLeast(0L)
    }

    class DurationRatio(
        sourceDurationUs: Long,
        mediaDurationUs: Long,
    ) : FusionXMediaTimeMapper {
        private val safeSourceDurationUs = sourceDurationUs.coerceAtLeast(1L)
        private val safeMediaDurationUs = mediaDurationUs.coerceAtLeast(1L)

        override fun sourceToMediaTimeUs(sourceTimeUs: Long): Long {
            val clampedSourceTimeUs = sourceTimeUs.coerceIn(0L, safeSourceDurationUs)
            return ((clampedSourceTimeUs.toDouble() / safeSourceDurationUs.toDouble()) *
                safeMediaDurationUs.toDouble()).roundToLong()
        }

        override fun mediaToSourceTimeUs(mediaTimeUs: Long): Long {
            val clampedMediaTimeUs = mediaTimeUs.coerceIn(0L, safeMediaDurationUs)
            return ((clampedMediaTimeUs.toDouble() / safeMediaDurationUs.toDouble()) *
                safeSourceDurationUs.toDouble()).roundToLong()
        }
    }

    class IndexedFrameMap(
        sourceFrameTimesUs: LongArray,
        mediaFrameTimesUs: LongArray,
    ) : FusionXMediaTimeMapper {
        private val sourceFrames = sourceFrameTimesUs.takeIf { it.isNotEmpty() } ?: LongArray(0)
        private val mediaFrames = mediaFrameTimesUs.takeIf { it.isNotEmpty() } ?: LongArray(0)

        override fun sourceToMediaTimeUs(sourceTimeUs: Long): Long {
            if (sourceFrames.isEmpty() || mediaFrames.isEmpty()) {
                return sourceTimeUs.coerceAtLeast(0L)
            }
            val sourceIndex = nearestFrameIndex(sourceFrames, sourceTimeUs)
            return mediaFrames[mapSourceIndexToMediaIndex(sourceIndex)]
        }

        override fun mediaToSourceTimeUs(mediaTimeUs: Long): Long {
            if (sourceFrames.isEmpty() || mediaFrames.isEmpty()) {
                return mediaTimeUs.coerceAtLeast(0L)
            }
            val mediaIndex = nearestFrameIndex(mediaFrames, mediaTimeUs)
            return sourceFrames[mapMediaIndexToSourceIndex(mediaIndex)]
        }

        private fun mapSourceIndexToMediaIndex(sourceIndex: Int): Int {
            if (sourceFrames.size == mediaFrames.size) {
                return sourceIndex.coerceIn(0, mediaFrames.lastIndex)
            }
            if (sourceFrames.size <= 1 || mediaFrames.size <= 1) {
                return 0
            }
            val ratio = sourceIndex.toDouble() / sourceFrames.lastIndex.toDouble()
            return (ratio * mediaFrames.lastIndex.toDouble())
                .roundToInt()
                .coerceIn(0, mediaFrames.lastIndex)
        }

        private fun mapMediaIndexToSourceIndex(mediaIndex: Int): Int {
            if (sourceFrames.size == mediaFrames.size) {
                return mediaIndex.coerceIn(0, sourceFrames.lastIndex)
            }
            if (sourceFrames.size <= 1 || mediaFrames.size <= 1) {
                return 0
            }
            val ratio = mediaIndex.toDouble() / mediaFrames.lastIndex.toDouble()
            return (ratio * sourceFrames.lastIndex.toDouble())
                .roundToInt()
                .coerceIn(0, sourceFrames.lastIndex)
        }

        private fun nearestFrameIndex(frameTimesUs: LongArray, targetTimeUs: Long): Int {
            if (frameTimesUs.size == 1) {
                return 0
            }
            val clampedTargetTimeUs = targetTimeUs.coerceAtLeast(0L)
            var low = 0
            var high = frameTimesUs.lastIndex
            while (low <= high) {
                val mid = (low + high) ushr 1
                val midValue = frameTimesUs[mid]
                when {
                    midValue < clampedTargetTimeUs -> low = mid + 1
                    midValue > clampedTargetTimeUs -> high = mid - 1
                    else -> return mid
                }
            }
            val upperIndex = low.coerceIn(0, frameTimesUs.lastIndex)
            val lowerIndex = (upperIndex - 1).coerceAtLeast(0)
            if (upperIndex == lowerIndex) {
                return upperIndex
            }
            val lowerDistance = (clampedTargetTimeUs - frameTimesUs[lowerIndex]).coerceAtLeast(0L)
            val upperDistance = (frameTimesUs[upperIndex] - clampedTargetTimeUs).coerceAtLeast(0L)
            return if (upperDistance < lowerDistance) upperIndex else lowerIndex
        }
    }
}
