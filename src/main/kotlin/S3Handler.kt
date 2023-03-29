import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import kotlin.experimental.xor

class S3Handler : RequestHandler<S3Event, Unit> {

    override fun handleRequest(s3Event: S3Event?, context: Context?) {
        s3Event ?: throw RuntimeException("input is null")
        context ?: throw RuntimeException("context is null")

        context.logger.log("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")

        context.logger.log("开始处理任务")

        val record = s3Event.records.getOrNull(0) ?: throw RuntimeException("record is null")

        val srcBucket = record.s3.bucket.name
        val srcKey = record.s3.`object`.urlDecodedKey

        context.logger.log("当前处理文件:s3://${srcBucket}/${srcKey}")

        val s3Client = S3Client.builder()
            .build()

        val getObjectRequest = GetObjectRequest.builder()
            .bucket(srcBucket)
            .key(srcKey)
            .build()

        when (getImageType(s3Client, getObjectRequest)) {
            ImageType.EncodeImage -> {
                context.logger.log("s3://${srcBucket}/${srcKey}已被加密,跳过此次处理")
                return
            }

            ImageType.None -> {
                context.logger.log("s3://${srcBucket}/${srcKey}不是图片格式,跳过此次处理")
                return
            }

            ImageType.OriginImage -> {}
        }


        val dstFile = File("/tmp/$srcKey")

        dstFile.parentFile.mkdirs()

        dstFile.createNewFile()

        s3Client.getObject(getObjectRequest).use { input ->
            dstFile.outputStream().use { output ->
                val buffer = ByteArray(1024 * 4)

                do {

                    val length = input.read(buffer)

                    if (length == -1) break

                    repeat(length) {
                        buffer[it] = buffer[it] xor 106
                    }

                    output.write(buffer, 0, length)

                } while (true)
            }
        }

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(srcBucket)
            .key(srcKey)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(dstFile))

        context.logger.log("s3://${srcBucket}/${srcKey}加密完成")
    }

    private fun getImageType(s3Client: S3Client, request: GetObjectRequest): ImageType {
        val buffer = ByteArray(20)

        val length = s3Client.getObject(request).use { input ->
            input.read(buffer)
        }

        return ImageTypeUtil.getImageType(length, buffer)
    }
}