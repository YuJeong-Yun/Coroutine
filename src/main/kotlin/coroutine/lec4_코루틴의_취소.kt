package coroutine

import kotlinx.coroutines.*

/**
 * 필요하지 않은 코루틴을 적절히 취소해 컴퓨터 자원을 아껴야 한다!
 */

/**
 * 코루틴이 취소( cancle() )에 협조하는 방법
 * 1. delay() / yield() 같은 kotlinx.coroutines 패키지의 suspend 함수 사용
 *
 */
fun lec04Example1(): Unit = runBlocking {
    val job = launch {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                printWithThread("${i++}번째 출력!")
                nextPrintTime += 1_000L
            }
        }
    }

    delay(100L)
    job.cancel() // 취소 안 됨
}

/**
 * 2. 코루틴 스스로 본인의 상태를 확인해 취소 요청을 받았으면, CancellationException을 던지기
 *      isAcitve :현재 코루틴이 취소 명령을 받았는지, 여전히 활성화 상태인지
 */
fun lec04Example2(): Unit = runBlocking {
    // 메인 스레드와 별개의 스레드에서 동작
    // 기본적으로 runBlocking과 같은 스레드에서 동작하므로 cancle 시키려면 다른 스레드에서 동작해야함
    val job = launch(Dispatchers.Default) {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                printWithThread("${i++}번째 출력!")
                nextPrintTime += 1_000L
            }

            if (!isActive) { // 활성 상태 아니면 CancellationException 던지기
                throw CancellationException()
            }
        }

        // 혹은 아래와 같이 활성화 상태 아니면 while문 종료하도록 해도 됨 (취소는 아님)
//        while (isActive && i <= 5) {
//            if (nextPrintTime <= System.currentTimeMillis()) {
//                printWithThread("${i++}번째 출력!")
//                nextPrintTime += 1_000L
//            }
//        }
    }

    delay(100L)
    job.cancel()
}

// canellationExcepton을 예외처리 하면 취소 못 함
fun main(): Unit = runBlocking {
    val job = launch {
        try {
            delay(1_000L)
        } catch (e: CancellationException) {
            // 아무것도 안한다!
        } finally {
            // 취소 시 여기서 필요한 자원을 닫을 수도 있습니다.
        }

        printWithThread("delay에 의해 취소되지 않았다!")
    }

    delay(100L)
    printWithThread("취소 시작")
    job.cancel()
}