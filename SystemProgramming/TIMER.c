   #include<sys/time.h>
#include<stdio.h>
#include<signal.h>
#include<stdlib.h>
#include<pthread.h>
#include<time.h>
#include<unistd.h>
#define num_threads 10
/*
Create condition and mutex variables.
*/
pthread_cond_t cond_array[10] = {PTHREAD_COND_INITIALIZER};
pthread_mutex_t API_Mutex = PTHREAD_MUTEX_INITIALIZER;
/*
Create a TCB struct to manage threads.
*/
struct TCB{
    int period;
    int thread_id;
    int time_left_to_invoke;
};
/*
Declare struct associated with time, TCB, signal, and signalhandler.
*/
struct tm * times;
struct TCB TCB_array[10];
struct sigevent sigev;
struct itimerspec itval, oitval;
struct sigaction newact;
/*
10 threads are running at the same time so, declare period and id variable as global variables.
because 10 threads have different id and period values for each thread.
*/
int period = 1000;
int id=0;
/*
Declared the name of the function
*/
void ClockHandler(int sig, siginfo_t *info, void *context);
void* time_triggered_threads(void* arg);
void tt_thread_register(int period, int thread_id);
void tt_thread_wait_invocation(int thread_id);
void startclock();
/*
Setting to use the POSIX Timer, setting to send signal periodically and generate a timer.
*/
void startclock(){
    timer_t timerid;
    sigemptyset(&newact.sa_mask);
    newact.sa_flags = SA_SIGINFO;
    newact.sa_sigaction = ClockHandler;
    sigaction(SIGRTMIN, &newact, NULL);
    sigev.sigev_notify = SIGEV_SIGNAL;
    sigev.sigev_signo = SIGRTMIN;
    sigev.sigev_value.sival_ptr=&timerid;
    timer_create(CLOCK_REALTIME, &sigev, &timerid);
    itval.it_value.tv_sec=0;
    itval.it_value.tv_nsec=(long)10*(1000000L);
    itval.it_interval.tv_sec=0;
    itval.it_interval.tv_nsec=(long)10*(1000000L);
    timer_settime(timerid, 0, &itval, &oitval);
}
/*
Each thread will have a period and id as it executes. and then each thread will be put into wait by 
tt_thread_wait_invocation() function. when it wakes up by signal, it shows the current time on the screen.
*/
void* time_triggered_threads(void *arg){ 
    tt_thread_register(period, id);
    pthread_mutex_lock(&API_Mutex);    
    time_t timer;    
    int pre_id = id;
    period += 1000; 
    id++;    
    pthread_mutex_unlock(&API_Mutex);
    while(1){
        tt_thread_wait_invocation(pre_id);
        time(&timer); 
        times=localtime(&timer);
        printf("POSIX_Timer :: Thread id = %d >> current time = hour : %02d, minute : %02d, sec : %02d \n", (pre_id+1), times->tm_hour, times->tm_min, times->tm_sec);
    }        
}
/*
The POSIX Timer calls the ClockHandler function every 1/100 seconds. 
Whenever is called, it is subtracted by 10 from the period of the thread
and when it reaches 0, it wakes up the wait state of the thread.
*/
void ClockHandler(int sig, siginfo_t *info, void *context){
    int n_overrun;
    pthread_mutex_lock(&API_Mutex);
    int i;
    for(i=0; i<num_threads; i++){
        n_overrun = timer_getoverrun(context);    
        if(n_overrun>=1) {//If the signalhandler misses the time, get it via the getoverrun() function.
         TCB_array[i].time_left_to_invoke -= n_overrun*10; //Subtract from time_left_to_invoke by the time missed.
        }
        if((TCB_array[i].time_left_to_invoke -= 10)<=0){ 
            TCB_array[i].time_left_to_invoke = TCB_array[i].period; 
            pthread_cond_signal(&cond_array[i]);
            }
    }
    pthread_mutex_unlock(&API_Mutex);
            
}
/*
Sets the period and id of each thread. This is managed using TCB structure.
*/
void tt_thread_register(int period, int thread_id){
    pthread_mutex_lock(&API_Mutex); 
    TCB_array[thread_id].period = period;
    TCB_array[thread_id].thread_id = thread_id;
    TCB_array[thread_id].time_left_to_invoke = period;
    pthread_mutex_unlock(&API_Mutex);
}
/*
Wait for the thread through thread_id.
*/
void tt_thread_wait_invocation(int thread_id){
    pthread_mutex_lock(&API_Mutex);
    pthread_cond_wait(&cond_array[thread_id], &API_Mutex);
    pthread_mutex_unlock(&API_Mutex);
}
/*
Create 10 threads, It also creates a periodic timer through startclock() and then exits all threads through pthread_join().
*/
int main(int argc, char* argv[]){
    pthread_t thread[10]; 
    int ret, i;
                
    for(i=0; i<10; i++)
        pthread_create(&thread[i], NULL, time_triggered_threads, NULL);
    
    startclock();
    
    while(1)
        pause();
    for(i=0; i<10; i++){
        printf("ith thread will be die\n", i);
        pthread_join(&thread,NULL);
    }
} 
