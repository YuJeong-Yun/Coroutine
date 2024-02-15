package coroutine

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

// runBlocking = 일반 루틴 세계와 코루틴 세계를 연결 -> 이 함수 자체로 코루틴을 만듦
fun main(): Unit = runBlocking {
    printWithThread("START")
    // launch = 반환값이 없는 코루틴을 만듦 -> 즉 여기에는 runBlocking, launch에 의한 코루틴 2개가 있음
    launch {
        newRoutine()
    }
    yield()
    printWithThread("END")
}
// 실행 결과 : START - END - 3

// susnfend fun = 다른 susnfend fun 호출 가능
suspend fun newRoutine() {
    val num1 = 1
    val num2 = 2
    yield() // yeild = 지금 코루틴을 중단하고 다른 코루틴이 실행되도록 한다 (스레드를 양보!)
    printWithThread("${num1 + num2}")
}

// < 메모리 관점에서 코루틴 >
// 새로운 루틴이 호출된 후
// '완전히 종료되기 전', 해당 루틴에서 사용했던 정보들을 보관하고 있어야 한다!
// 루틴이 중단되었다가
// 해당 메모리에 접근이 가능해야 하므로

// 현재 실행되는 스레드 확인
// Edit configuration -> VM options : -Dkotlinx.coroutines.debug 입력하면 어떤 코루틴에서 실행되는지도 보여줌
fun printWithThread(str: Any?) {
    println("[${Thread.currentThread().name}] $str")
}


// < 루틴과 코루틴 차이 >
// 루틴 : 시작되면 끝날 떄 까지 멈추지 않는다
// 코루틴 : 중단되었다가 재개 가능