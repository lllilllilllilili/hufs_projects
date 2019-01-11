
[타이틀] : Heart rate measurement program (systolic, diastolic)

[기술 스택] : pthread, POSIX::TIMER

[설명] : Data_acquisition_thread 에서 혈압을 측정하여 Bp_processing_thread에서 혈압을 보여주는 혈압측정 기계를 시뮬레이션한 프로그램을 구현합니다. 프로그램 모델은 Producer & Consumer model입니다. POSIX Timer에 따라 혈압을 주기적으로 측정해야 합니다.

[코드 설명] : Main thread에서 Data_acquisition_thread와 Bp_processing_thread를 각각 만들어 줍니다. Data_acquisition_thread에서는 thread에 관한 정보를 등록하고 tt_thread_wait_invocation 해서 pthread_cond_wait() 됩니다. Bp_processing_thread에서도 마찬가지로 pthread_cond_wait됩니다.
Main thread에서는 startclock()을 부르게되면 ClockHandler는 1 milli second마다 호출되면서 period가 설정된 TCB_array[Data_acquisition_thread_id].time_left_to_invoke 값을 10씩 가감하면서 0보다 같거나 작아질때까지 기다립니다. 10번의 ClockHandler가 불리고 나면 0보다 작아지게 되는데 이때pthread_cond_sinal()로 Data_acquisition_thread를 깨워주게 됩니다. TCB_array[Data_acquisition_thread_id].time_left_to_invoke의 값은 초기에 100으로 설정하였으므로 Data_acquisition_thread는 10 milli second 마다 깨어나게 됩니다. Data_acquisition_thread는 깨어나서 turn의 값에 따라 diastolic 또는 systolic 인지 확인합니다. diastolic의 경우에는 60 - 90 사이에 무작위로 값을 Queue에 전달하고 반대인 systolic의 경우에는 110~150 사이의 무작위로 값을 Queue에 전달하게 됩니다. 여기서도 data_acquisition_time을 설정하여 Data_acquisition_thread가 불릴때마다 10씩 가감하여 0보다 같거나 작아지게 되면 pthread_cond_signal()로 Bp_processing_thread를 깨워주게 됩니다. data_acquisition_time의 값은 초기에 100으로 설정하였으므로 Bp_processing_thread는 100 milli second 마다 깨어나게 됩니다. Bp_processing_thread는 깨어나서 Queue에 있는 10개의 값을 읽어서 평균을 냅니다. 그리고 turn의 값에 따라 diastolic 또는 systolic인지 확인한뒤 평균값을 Terminal 을 통해 display 합니다.

[기획 및 개발 의도] : POSIX::TIMER 공부

[역할] : 개발 100%
