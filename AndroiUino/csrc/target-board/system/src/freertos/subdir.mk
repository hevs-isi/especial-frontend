################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../system/src/freertos/croutine.c \
../system/src/freertos/list.c \
../system/src/freertos/queue.c \
../system/src/freertos/tasks.c \
../system/src/freertos/timers.c 

OBJS += \
./system/src/freertos/croutine.o \
./system/src/freertos/list.o \
./system/src/freertos/queue.o \
./system/src/freertos/tasks.o \
./system/src/freertos/timers.o 

C_DEPS += \
./system/src/freertos/croutine.d \
./system/src/freertos/list.d \
./system/src/freertos/queue.d \
./system/src/freertos/tasks.d \
./system/src/freertos/timers.d 


# Each subdirectory must supply rules for building sources it contributes
system/src/freertos/%.o: ../system/src/freertos/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C Compiler'
	arm-none-eabi-gcc -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DUSE_STDPERIPH_DRIVER -I"../include/" -I"../system/include/" -I"../system/include/arm/" -I"../system/include/CMSIS/" -I"../system/include/cortexm/" -I"../system/include/diag/" -I"../system/include/stm32f1-stdperiph/" -I"../system/include/freertos/" -I../system/include/freertos/portable/ -I../system/include/freertos/portable/GCC/ARM_CM3/ -std=gnu11 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


