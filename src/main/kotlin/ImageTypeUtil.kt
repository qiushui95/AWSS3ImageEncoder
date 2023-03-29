object ImageTypeUtil {

    fun getImageType(readCount: Int, buffer: ByteArray): ImageType {
        return when {
            isJPEG(readCount, buffer, 0) -> ImageType.OriginImage
            isPNG(readCount, buffer, 0) -> ImageType.OriginImage
            isGIF(readCount, buffer, 0) -> ImageType.OriginImage
            isBMP(readCount, buffer, 0) -> ImageType.OriginImage
            isWebP(readCount, buffer, 0) -> ImageType.OriginImage
            isJPEG(readCount, buffer, 106) -> ImageType.EncodeImage
            isPNG(readCount, buffer, 106) -> ImageType.EncodeImage
            isGIF(readCount, buffer, 106) -> ImageType.EncodeImage
            isBMP(readCount, buffer, 106) -> ImageType.EncodeImage
            isWebP(readCount, buffer, 106) -> ImageType.EncodeImage
            else -> ImageType.None
        }
    }

    private fun isJPEG(readCount: Int, buffer: ByteArray, xorValue: Int): Boolean {
        return when {
            readCount <= 4 -> false
            buffer[0] != (0xFF xor xorValue).toByte() -> false
            buffer[1] != (0xD8 xor xorValue).toByte() -> false
            buffer[2] != (0xFF xor xorValue).toByte() -> false
            buffer[3] != (0xE0 xor xorValue).toByte() -> false
            else -> true
        }
    }

    private fun isPNG(readCount: Int, buffer: ByteArray, xorValue: Int): Boolean {
        return when {
            readCount <= 8 -> false
            buffer[0] != (0x89 xor xorValue).toByte() -> false
            buffer[1] != (0x50 xor xorValue).toByte() -> false
            buffer[2] != (0x4E xor xorValue).toByte() -> false
            buffer[3] != (0x47 xor xorValue).toByte() -> false
            buffer[4] != (0x0D xor xorValue).toByte() -> false
            buffer[5] != (0x0A xor xorValue).toByte() -> false
            buffer[6] != (0x1A xor xorValue).toByte() -> false
            buffer[7] != (0x0A xor xorValue).toByte() -> false
            else -> true
        }
    }

    private fun isGIF(readCount: Int, buffer: ByteArray, xorValue: Int): Boolean {
        return when {
            readCount <= 4 -> false
            buffer[0] != (0x47 xor xorValue).toByte() -> false
            buffer[1] != (0x49 xor xorValue).toByte() -> false
            buffer[2] != (0x46 xor xorValue).toByte() -> false
            buffer[3] != (0x38 xor xorValue).toByte() -> false
            else -> true
        }
    }

    private fun isBMP(readCount: Int, buffer: ByteArray, xorValue: Int): Boolean {
        return when {
            readCount <= 2 -> false
            buffer[0] != (0x42 xor xorValue).toByte() -> false
            buffer[1] != (0x4D xor xorValue).toByte() -> false
            else -> true
        }
    }

    private fun isWebP(readCount: Int, buffer: ByteArray, xorValue: Int): Boolean {
        return when {
            readCount <= 12 -> false
            buffer[0] != ('R'.code xor xorValue).toByte() -> false
            buffer[1] != ('I'.code xor xorValue).toByte() -> false
            buffer[2] != ('F'.code xor xorValue).toByte() -> false
            buffer[3] != ('F'.code xor xorValue).toByte() -> false
            buffer[8] != ('W'.code xor xorValue).toByte() -> false
            buffer[9] != ('E'.code xor xorValue).toByte() -> false
            buffer[10] != ('B'.code xor xorValue).toByte() -> false
            buffer[11] != ('P'.code xor xorValue).toByte() -> false
            else -> true
        }
    }
}