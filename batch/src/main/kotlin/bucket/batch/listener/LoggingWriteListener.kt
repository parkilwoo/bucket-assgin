package bucket.batch.listener

import org.springframework.batch.core.ItemWriteListener
import org.springframework.batch.item.Chunk
import org.springframework.stereotype.Component


@Component
class LoggingWriteListener : ItemWriteListener<Any> {
    override fun afterWrite(items: Chunk<out Any>) {
        println("Chunk Write 완료 - ${items.size()}건 처리됨")
    }
}