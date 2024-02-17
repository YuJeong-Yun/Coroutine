package coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *  job에서 완료 상태가 COMPLETING -> COMPLETED 두 단계로 나눠져 있음
 *  => 자식 코루틴을 기다려야 하기 때문
 *      내가 완료되었더라도 자식에서 예외가 발생하면 COMPLETING에서 CANCELLING으로 됨 && 남은 다른 자식 코루틴에게 취소 요청 보냄 (CancellationException 제외)
 */

/**
 * Structured Concurrency : 부모-자식 관계의 코루틴이 한 몸처럼 움직이는 것
 *      => 수많은 코루틴이 유실되거나 누수되지 않도록 보장한다 (부모가 취소되면 자식 코루틴도 취소됨)
 *      => 코드 내의 에러가 유실되지 않고 적절히 보고될 수 있도록 보장한다 (부모에게 에러 전파)
 */
fun main(): Unit = runBlocking {
    launch {
        delay(600L)
        printWithThread("A")
    }

    launch {
        delay(500L)
        throw IllegalArgumentException("코루틴 실패!")
    }
}