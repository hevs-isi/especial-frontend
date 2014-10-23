################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../system/src/CMSIS/system_stm32f10x.c \
../system/src/CMSIS/vectors_stm32f10x.c 

OBJS += \
./system/src/CMSIS/system_stm32f10x.o \
./system/src/CMSIS/vectors_stm32f10x.o 

C_DEPS += \
./system/src/CMSIS/system_stm32f10x.d \
./system/src/CMSIS/vectors_stm32f10x.d 


# Each subdirectory must supply rules for building sources it contributes
system/src/CMSIS/%.o: ../system/src/CMSIS/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C Compiler'
	arm-none-eabi-gcc -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DUSE_STDPERIPH_DRIVER -I"../include/" -I"../system/include/" -I"../system/include/arm/" -I"../system/include/CMSIS/" -I"../system/include/cortexm/" -I"../system/include/diag/" -I"../system/include/stm32f1-stdperiph/" -I"../system/include/freertos/" -I../system/include/freertos/portable/ -I../system/include/freertos/portable/GCC/ARM_CM3/ -std=gnu11 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


