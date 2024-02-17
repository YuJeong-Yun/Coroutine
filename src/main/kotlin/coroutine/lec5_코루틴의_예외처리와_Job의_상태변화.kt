package coroutine

import kotlinx.coroutines.*

/**
 * 새로운 root 코루틴을 만들고 싶다면 -> 새로운 영역(CoroutineScope)을 만들어야 한다
 */
fun lec05Example1(): Unit = runBlocking {
    // launch를 새로운 root 코루틴으로 만듦
    val job1 = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        printWithThread("Job 1")
    }

    val job2 = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        printWithThread("Job 2")
    }
}

/**
 * launch는 예외가 발생하면 예외를 출력하고 코루틴을 종료
 */
fun lec05Example2(): Unit = runBlocking {
    val job = CoroutineScope(Dispatchers.Default).launch {
        throw IllegalArgumentException()
    }

    delay(1_000L)
}

/**
 * 반면 async는 예외가 발생해도 출력하지 않음. 출력시키려면 await()를 호출해야함
 */
fun lec05Example3(): Unit = runBlocking {
    val job = CoroutineScope(Dispatchers.Default).async {
        throw IllegalArgumentException()
    }

    delay(1_000L)
    job.await()
}

/**
 * async에 Dispatchers를 지정하지 않으면 여기서는 예외 출력 됨
 * => 자식 코루틴의 예외는 부모에게 전파되기 때문!
 *      위에서는 async가 root라 전파될 곳이 없었음
 */
fun lec05Example3_1(): Unit = runBlocking {
    val job = async {
        throw IllegalArgumentException()
    }

    delay(1_000L)
    job.await()
}

/**
 * 자식 코루틴의 예외를 부모에게 전파하지 않으려면
 *  1. 위 방법처럼 루트 코루틴으로 만듦
 *  2. 아래처럼 supervisorJob 사용
 */
fun lec05Example4(): Unit = runBlocking {
    val job = async(SupervisorJob()) {
        throw IllegalArgumentException()
    }

    delay(1_000L)
    job.await()
}

/**
 * launch에서 예외를 다루는 방법
 *  1. try-catch-finally
 *
 *  2. CoroutineExceptionHandler -> try-catch-finally와 달리 예외 발생 이후 에러 로깅 / 에러 메세지 전송 등에 활용
 *      => launch에만 적용 가능 / 부모 코루틴이 있으면 동작하지 않음
 */
fun lec05Example5(): Unit = runBlocking {
    val job = launch {
        try {
            throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            printWithThread("정상 종료")
        }
    }
}

// coutourinteExceptionHandler(코루틴 구성요소, 발생한 예외) 사용
fun main(): Unit = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        printWithThread("예외")
        throw throwable
    }

    val job = CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
        throw IllegalArgumentException()
    }

    delay(1_000L)
}


/** 코루틴 취소/예외 정리
 *  1. 발생한 예외가 CancellationException인 경우 '취소'로 간주하고 부모 코루틴에 전파 X
 *  2. 다른 예외이면 '실패'로 간주하고 부모 코루틴에 전파 O
 *
 *  => 다만 내부적으로는 취소나 실패 모두 '취소됨' 상태로 관리
 */