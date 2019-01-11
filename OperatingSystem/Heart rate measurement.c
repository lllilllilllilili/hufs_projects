#include<sys/time.h>
#include<stdio.h>
#include<signal.h>
#include<stdlib.h>
#include<pthread.h>
#include<time.h>
#include<unistd.h>
#include<time.h>
#define MAX 10
/*
Create condition and mutex variables.
*/
pthread_cond_t cond_array[2] = {PTHREAD_COND_INITIALIZER}; 
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
struct TCB TCB_array[10];
struct sigevent sigev;
struct itimerspec itval, oitval;
struct sigaction newact;
/*
Use the queue to synchronization of Data_acquisition_thread and Bp_processing_thread.
For using queue, need front and rear pointer. 
*/
int front = 0;
int rear = 0;
int queue_array[MAX];
int startPos=-1, endPos=0;
/*
Declared the name of the function.
*/
void ClockHandler(int sig, siginfo_t *info, void *context);
void tt_thread_register(int period, int thread_id);
void tt_thread_wait_invocation(int thread_id);
void* Data_acquisition_thread(void *arg);
void* Bp_processing_thread(void *arg);
void push(int value);
int pop();
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
    itval.it_value.tv_nsec=(long)10*(100000L);
    itval.it_interval.tv_sec=0;
    itv
al.it_interval.tv_nsec=(long)10*(100000L);
    timer_settime(timerid, 0, &itval, &oitval);
}
/*
1. When Data_acquisition_thread is created, period, id are registered.
2. Waited by tt_thread_wait_invocation().
3. The Clock_Handler wakes up Data_acquisition_thread every 10 milli seconds.
4. After waking up, it generates a random value for the diastolic or systolic through turn and push it to the queue.
5. When data_acquisition_time is 0, turn increases 1 and Bp_processing_thread wakes up.
6. While() is true, it is waited again by tt_thread_wait_invocation() and wait until signal comes.
*/
void* Data_acquisition_thread(void *arg){
    int turn = 0;
    int data_acquisition_time=100;    
    int Data_acquisition_thread_id=0;
    int period =100;
    int bp_processing_thread_id=1;
    int bp_diastolic_variable=0;
    int bp_systolic_variable=0;
    srand(time(NULL));
    tt_thread_register(period, Data_acquisition_thread_id);    
    while(1){
    tt_thread_wait_invocation(Data_acquisition_thread_id);
    pthread_mutex_lock(&API_Mutex);
    if(turn%2==0){
        bp_diastolic_variable=rand()%31+60;
        push(bp_diastolic_variable);
    }    
    else {
        bp_systolic_variable=rand()%41+110;
        push(bp_systolic_variable);
    }
    if((data_acquisition_time-=10)<=0){
        data_acquisition_time=100;    
        turn ++;
        pthread_cond_signal(&cond_array[bp_processing_thread_id]);    
    };    
    pthread_mutex_unlock(&API_Mutex);
    /*
    Mutext_lock and unlock were used for variables related to queue.
    */
    }
}
/*
1. Bp_processing_thread is awakened by Data_acquisition_thread every 100 milli seconds
2. Calculate the average by adding all 10 values in the queue.
3. Check the value of turn if it is diastolic or systolic.
4. Display the average value bp on the terminal and turn increases 1.
5. While() is true, it is waited again by tt_thread_wait_invocation() and wait until signal comes.
*/
void* Bp_processing_thread(void *arg){
    int turn=0, sum=0, i;
    int bp_processing_thread_id=1;
    
    float avg_bp=0;    
    int pop_num=0;
    
    while(1){
    tt_thread_wait_invocation(bp_processing_thread_id);
    pthread_mutex_lock(&API_Mutex);
    for(i=0; i<10; i++){
        pop_num=pop();
        sum+=pop_num;    
        }        
    pthread_mutex_unlock(&API_Mutex);
    /*
    Mutext_lock and unlock were used for variables related to queue.
    */
        avg_bp=(float)sum/10;
        if(turn%2==0)
            printf("Diastolic bp = %.2f\n", avg_bp); 
    
        else
            printf("Systolic bp = %.2f\n", avg_bp);
        sum=0;        
        turn++;
    }
}
/*
The POSIX Timer calls the ClockHandler function every 1/1000 seconds. 
Whenever is called, it is subtracted 10 from the period of the thread
and when it reaches 0, it wakes up the wait state of the thread.
As a result, wake up Data_acquisition_thread every 10 milli-sec.
*/
void ClockHandler(int sig, siginfo_t *info, void *context){
    int n_overrun;
    pthread_mutex_lock(&API_Mutex);
    int Data_acquisition_thread_id =0;        
        n_overrun = timer_getoverrun(context);    
        if(n_overrun>=1) {
         TCB_array[Data_acquisition_thread_id].time_left_to_invoke -= n_overrun*10;
        }
        if((TCB_array[Data_acquisition_thread_id].time_left_to_invoke -= 10)<=0){ 
/*
ClockHandler wakes up Data_acquisition_thread without 10.
However, added this code because it may miss times.
*/
            TCB_array[Data_acquisition_thread_id].time_left_to_invoke = TCB_array[Data_acquisition_thread_id].period;
            pthread_cond_signal(&cond_array[Data_acquisition_thread_id]);
            }
    pthread_mutex_unlock(&API_Mutex);
/*
Used Mutex_lock and unlock for accessing the TCB structure.
*/            
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
Wait for the thread through thread_id
*/
void tt_thread_wait_invocation(int thread_id){
    pthread_mutex_lock(&API_Mutex);
    pthread_cond_wait(&cond_array[thread_id], &API_Mutex);
    pthread_mutex_unlock(&API_Mutex);
}
/*
Implemented push() and pop() functions to use the circular Queue.
*/
void push(int value){
    if(endPos==-1){
        //printf("Q is full\n");
        exit(0);
    }
    queue_array[endPos]=value;
    if(startPos==-1)startPos=endPos;
    endPos=(endPos+1)%MAX;
    if(endPos==startPos) endPos=-1;
    
}
int pop(){
    if(startPos==-1)
    {
        //printf("Q is empty\n");
        exit(0);
    }
    int val=queue_array[startPos];
    if(endPos==-1) endPos=startPos;
    startPos=(startPos+1)%MAX;
    if(startPos==endPos) startPos=-1;
    return val;
}
/*
Create 2 threads(Data_acquisition_thread, Bp_processing_thread),
It also creates a periodic timer through startclock(). and then exits all threads through pthread_join().
*/
int main(int argc, char* argv[]){
    pthread_t thread[2]; 
    int i;
    pthread_create(&thread[0], NULL, Data_acquisition_thread, NULL);
    pthread_create(&thread[1], NULL, Bp_processing_thread, NULL);
    startclock();
    while(1)
        pause();
    pthread_join(Data_acquisition_thread,NULL);
    pthread_join(Bp_processing_thread,NULL);
}     
