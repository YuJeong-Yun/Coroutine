package coroutine

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis


// 코루틴 빌더 = 코루틴을 만드는 함수
// delay = 특정 시간만큼 멈추고 다른 코루틴으로 넘김
// job = 코루틴 자체를 제어(시작 - start/취소 - cancle /종료시 까지 대기 - join)할 수 있는 객체
// deffered = job 상속 받음. async와 사용. start, join, cancle 외에 await  까지 사용 가능
/**
 * < 코루틴 생성 법 >
 *     1. runBlocking = 새로운 코루틴을 만들고, 루틴 세계와 코루틴 세계를 이어준다
 *                      이 안에서 만들어진 코루틴과 안에서 추가적으로 만든 코루틴이 '모두 다 완료될 때까지 스레드를 blocking' 시킴
 *                      => 스레드는 풀릴 떄 까지 다른 코드를 실행할 수 없음
 */
fun example1() {
    runBlocking {
        printWithThread("START")
        launch {
            delay(2_000L) // runBlocking 떄문에 끝날 때 까지 기다림
            printWithThread("LAUNCH END")
        }
    }

    printWithThread("END")
}

/**
 *     2. launch = 반환 값이 없는 코루틴 실행
 *                 job에는 할당 가능. 반환 값이 있는 게 아니라 해당 코루틴 자체를 넘기는 것!
 */
// 코루틴 시작 시점 지정
fun example2(): Unit = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) { // job.start 해야 해당 코루틴 실행됨
        printWithThread("Hello launch")
    }

    delay(1_000L)
    job.start()
}

// 코루틴 취소
fun example3(): Unit = runBlocking {
    val job = launch {
        (1..5).forEach {
            printWithThread(it)
            delay(500)
        }
    }

    delay(1_000L)
    job.cancel() // 1초 실행되고 중지됨
}


// 작업 대기
fun example4(): Unit = runBlocking {
    val job1 = launch {
        delay(1_000)
        printWithThread("Job 1")
    }
    job1.join() // 해당 작업이 끝날 때 까지 대기하고 다음 코드 실행

    val job2 = launch {
        delay(1_000L)
        printWithThread("Job 2")
    }
}

/**
 *      3. async = launch와 유사. but 결과를 반환할 수 있음
 *                  여러 API를 동시에 호출하여 소요시간을 최소화할 수 있다.
 *                  callBack 을 이용하지 않고 동기 방식으로 코드를 작성할 수 있다.
 *
 *          * 주의사항 : Coroutinestart.LAZY 옵션을 사용하면, await() 함수를 호출했을 때 계산 결과를 계속 기다린다.
 *                      but 앞에 start 한 번 시켜주면 괜찮음
 */
fun example5(): Unit = runBlocking {
    // 이 job 은 정확히는 Deferred 객체. (Deferred는 job을 상속받음)
    val job = async {
        3 + 5
    }
    val eight = job.await() // await : async의 결과를 가져오는 함수
    printWithThread(eight)
}


// 여러 API를 동시에 호출하여 소요시간을 최소화
suspend fun apiCall1(): Int { // suspend fun은 suspend fun에서만 부를 수 있음. delay가 suspend fun임!
    delay(1_000L)
    return 1
}

suspend fun apiCall2(): Int {
    delay(1_000L)
    return 2
}


fun main(): Unit = runBlocking {
    val time = measureTimeMillis {
        val job1 = async(start = CoroutineStart.LAZY) { apiCall1() }
        val job2 = async(start = CoroutineStart.LAZY) { apiCall2() }

        job1.start() // 둘 다 start 해주면 CoroutineStart.LAZY 사용해도 앞에 작업 끝날 때 까지 기다리지 않음
        job2.start()
        printWithThread(job1.await() + job2.await())
    }

    printWithThread("소요 시간 : $time ms")
}



