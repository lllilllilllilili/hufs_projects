[타이틀] : TimerManagementSystem

[기술 스택] : pthread, POSIX::TIMER

[설명] : thread 10개가 동시에 돌아가면서 각각의 thread에 주어진 period에 따라 current time을 출력하는 프로그램입니다. 10개의 thread는 주기가 전부 다릅니다. 따라서, POSIX timer handler를 사용하여 10 mili-sec 마다 SIGRTMIN이 발생하게 되고, 10개의 thread의 period를 조정하게 됩니다.

[코드 설명] : Main thread에서는 thread 10개를 만듭니다. 
각 thread는 time-triggered threads()에서 thread와 id값을 설정하고 pthread_cond_wait 됩니다.
POSIX timer을 이용하기 위한 설정 및 초기화 단계를 startclock() 함수에 구현합니다. 
따라서, 설정한 주기대로 10 mili-sec 마다 SIGRTMIN이 발생할 것이고 ClockHandler로 이동하여 각 thread id 값에 따라 period를 10씩 차감할것입니다. 
이때, signalhandler가 놓친 것이 있다면 timer_getoverrun() 통해서 1 보다 클경우 *10을 통해 thread의 period에서 차감됩니다.
Thread의 period가 0 이하가 되면 pthread_cond_signal에 의해서time_triggered threads()의 pthread_con_wait 상태에서 깨어나게됩니다. 
그리고 현재 시간을 터미널에 출력합니다. 
이러한 과정이 반복 수행되며 각 thread를 period를 설정하여 관리할 수 있는 모의 커널 프로그램을 완성할 수 있습니다.

[기획 및 개발 의도] : POSIX::TIMER 공부 

[역할] : 개발 100% 

