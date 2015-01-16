################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
/home/chm/git/stm32/stm32f10x_lib/src/freertos/croutine.c \
/home/chm/git/stm32/stm32f10x_lib/src/freertos/list.c \
/home/chm/git/stm32/stm32f10x_lib/src/freertos/queue.c \
/home/chm/git/stm32/stm32f10x_lib/src/freertos/tasks.c \
/home/chm/git/stm32/stm32f10x_lib/src/freertos/timers.c 

OBJS += \
./stm32f10x_lib/src/freertos/croutine.o \
./stm32f10x_lib/src/freertos/list.o \
./stm32f10x_lib/src/freertos/queue.o \
./stm32f10x_lib/src/freertos/tasks.o \
./stm32f10x_lib/src/freertos/timers.o 

C_DEPS += \
./stm32f10x_lib/src/freertos/croutine.d \
./stm32f10x_lib/src/freertos/list.d \
./stm32f10x_lib/src/freertos/queue.d \
./stm32f10x_lib/src/freertos/tasks.d \
./stm32f10x_lib/src/freertos/timers.d 


# Each subdirectory must supply rules for building sources it contributes
stm32f10x_lib/src/freertos/croutine.o: /home/chm/git/stm32/stm32f10x_lib/src/freertos/croutine.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C Compiler'
	arm-none-eabi-gcc -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DHSE_VALUE=80000000 -DUSE_STDPERIPH_DRIVER -DTRACE -I"/home/chm/git/ScalaTest/especial/csrc/include" -I"/home/chm/git/stm32/stm32f10x_lib/include" -I"/home/chm/git/stm32/stm32f10x_lib/include/arm" -I"/home/chm/git/stm32/stm32f10x_lib/include/cmsis" -I"/home/chm/git/stm32/stm32f10x_lib/include/cortexm" -I"/home/chm/git/stm32/stm32f10x_lib/include/diag" -I"/home/chm/git/stm32/stm32f10x_lib/include/freertos" -I"/home/chm/git/stm32/stm32f10x_lib/include/hal" -I"/home/chm/git/stm32/stm32f10x_lib/include/stm32f1" -I"/home/chm/git/stm32/stm32f10x_lib/include/demo" -std=gnu11 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

stm32f10x_lib/src/freertos/list.o: /home/chm/git/stm32/stm32f10x_lib/src/freertos/list.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C Compiler'
	arm-none-eabi-gcc -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DHSE_VALUE=80000000 -DUSE_STDPERIPH_DRIVER -DTRACE -I"/home/chm/git/ScalaTest/especial/csrc/include" -I"/home/chm/git/stm32/stm32f10x_lib/include" -I"/home/chm/git/stm32/stm32f10x_lib/include/arm" -I"/home/chm/git/stm32/stm32f10x_lib/include/cmsis" -I"/home/chm/git/stm32/stm32f10x_lib/include/cortexm" -I"/home/chm/git/stm32/stm32f10x_lib/include/diag" -I"/home/chm/git/stm32/stm32f10x_lib/include/freertos" -I"/home/chm/git/stm32/stm32f10x_lib/include/hal" -I"/home/chm/git/stm32/stm32f10x_lib/include/stm32f1" -I"/home/chm/git/stm32/stm32f10x_lib/include/demo" -std=gnu11 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

stm32f10x_lib/src/freertos/queue.o: /home/chm/git/stm32/stm32f10x_lib/src/freertos/queue.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C Compiler'
	arm-none-eabi-gcc -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DHSE_VALUE=80000000 -DUSE_STDPERIPH_DRIVER -DTRACE -I"/home/chm/git/ScalaTest/especial/csrc/include" -I"/home/chm/git/stm32/stm32f10x_lib/include" -I"/home/chm/git/stm32/stm32f10x_lib/include/arm" -I"/home/chm/git/stm32/stm32f10x_lib/include/cmsis" -I"/home/chm/git/stm32/stm32f10x_lib/include/cortexm" -I"/home/chm/git/stm32/stm32f10x_lib/include/diag" -I"/home/chm/git/stm32/stm32f10x_lib/include/freertos" -I"/home/chm/git/stm32/stm32f10x_lib/include/hal" -I"/home/chm/git/stm32/stm32f10x_lib/include/stm32f1" -I"/home/chm/git/stm32/stm32f10x_lib/include/demo" -std=gnu11 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

stm32f10x_lib/src/freertos/tasks.o: /home/chm/git/stm32/stm32f10x_lib/src/freertos/tasks.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C Compiler'
	arm-none-eabi-gcc -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DHSE_VALUE=80000000 -DUSE_STDPERIPH_DRIVER -DTRACE -I"/home/chm/git/ScalaTest/especial/csrc/include" -I"/home/chm/git/stm32/stm32f10x_lib/include" -I"/home/chm/git/stm32/stm32f10x_lib/include/arm" -I"/home/chm/git/stm32/stm32f10x_lib/include/cmsis" -I"/home/chm/git/stm32/stm32f10x_lib/include/cortexm" -I"/home/chm/git/stm32/stm32f10x_lib/include/diag" -I"/home/chm/git/stm32/stm32f10x_lib/include/freertos" -I"/home/chm/git/stm32/stm32f10x_lib/include/hal" -I"/home/chm/git/stm32/stm32f10x_lib/include/stm32f1" -I"/home/chm/git/stm32/stm32f10x_lib/include/demo" -std=gnu11 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

stm32f10x_lib/src/freertos/timers.o: /home/chm/git/stm32/stm32f10x_lib/src/freertos/timers.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C Compiler'
	arm-none-eabi-gcc -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DHSE_VALUE=80000000 -DUSE_STDPERIPH_DRIVER -DTRACE -I"/home/chm/git/ScalaTest/especial/csrc/include" -I"/home/chm/git/stm32/stm32f10x_lib/include" -I"/home/chm/git/stm32/stm32f10x_lib/include/arm" -I"/home/chm/git/stm32/stm32f10x_lib/include/cmsis" -I"/home/chm/git/stm32/stm32f10x_lib/include/cortexm" -I"/home/chm/git/stm32/stm32f10x_lib/include/diag" -I"/home/chm/git/stm32/stm32f10x_lib/include/freertos" -I"/home/chm/git/stm32/stm32f10x_lib/include/hal" -I"/home/chm/git/stm32/stm32f10x_lib/include/stm32f1" -I"/home/chm/git/stm32/stm32f10x_lib/include/demo" -std=gnu11 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


