package coroutine

import kotlinx.coroutines.*
import java.util.concurrent.Executors


/**
 * CoroutineScope의 주요 역할 : CoroutineContext 라는 데이터를 보관
 * - CoroutineContext : 코루틴과 관련된 여러가지 데이터를 가지고 있다(코루틴 이름, CoroutineExceptionHandler,
 *                      코루틴 그 자체, CoroutineDispatcher(코루틴이 어떤 스레드에 배정될지를 관리하는 역할)
 *
 *
 * - CoroutineScope: 코루틴이 탄생할 수 있는 영역
 * - CoroutineContext : 코루틴과 관련된 데이터를 보관
 */
suspend fun lec07Example1() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        printWithThread("Job 1")
        // coroutineContext.minusKey(CoroutineName.Key)
    }

    job.join()
}

/**
 * 클래스 내부에서 독립적인 CoroutineScope를 관리해서, 해당 클래스에서 사용하던 코루틴을 한 번에 종료시킬 수 있다.
 */
class AsyncLogic {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun doSomething() {
        scope.launch {
            // 무언가 코루틴이 시작되어 작업!
        }
    }

    fun destroy() {
        scope.cancel()
    }
}

/**
 * CoroutineContext = Map + Set을 합쳐놓은 상태
 *
 * CoroutineDispatcher = 코루틴을 스레드에 배정하는 역할
 *      - Dispatchers.Default : 가장 기본적인 디스패처, CPU 자원을 많이 쓸 때 권장. 별다른 설정이 없으면 이 디스패처가 사용된다.
 *      - Dispatchers.IO : I/O 작업에 최적화된 디스패처
 *      - Dispatchers.Main : 보통 UI 컴포넌트를 조작하기 위한 디스패처. 특정 의존성을 갖고 있어야 정상적으로 활용할 수 있다.
 *      - ExceutorService를 디스패처로 : asCoroutineDispatcher() 확장함수 활용
 */

// ExceutorService 예시
fun main() {
    CoroutineName("나만의 코루틴") + Dispatchers.Default
    val threadPool = Executors.newSingleThreadExecutor()
    CoroutineScope(threadPool.asCoroutineDispatcher()).launch {
        printWithThread("새로운 코루틴")
    }
}



