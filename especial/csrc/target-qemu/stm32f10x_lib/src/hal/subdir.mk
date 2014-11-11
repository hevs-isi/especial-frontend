################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
/home/chm/git/stm32/stm32f10x_lib/src/hal/helper.c 

CPP_SRCS += \
/home/chm/git/stm32/stm32f10x_lib/src/hal/digitalinput.cpp \
/home/chm/git/stm32/stm32f10x_lib/src/hal/digitaloutput.cpp \
/home/chm/git/stm32/stm32f10x_lib/src/hal/gpio.cpp 

OBJS += \
./stm32f10x_lib/src/hal/digitalinput.o \
./stm32f10x_lib/src/hal/digitaloutput.o \
./stm32f10x_lib/src/hal/gpio.o \
./stm32f10x_lib/src/hal/helper.o 

C_DEPS += \
./stm32f10x_lib/src/hal/helper.d 

CPP_DEPS += \
./stm32f10x_lib/src/hal/digitalinput.d \
./stm32f10x_lib/src/hal/digitaloutput.d \
./stm32f10x_lib/src/hal/gpio.d 


# Each subdirectory must supply rules for building sources it contributes
stm32f10x_lib/src/hal/digitalinput.o: /home/chm/git/stm32/stm32f10x_lib/src/hal/digitalinput.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C++ Compiler'
	arm-none-eabi-g++ -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DHSE_VALUE=80000000 -DUSE_STDPERIPH_DRIVER -DTRACE -I"/home/chm/git/ScalaTest/especial/csrc/include" -I"/home/chm/git/stm32/stm32f10x_lib/include" -I"/home/chm/git/stm32/stm32f10x_lib/include/arm" -I"/home/chm/git/stm32/stm32f10x_lib/include/cmsis" -I"/home/chm/git/stm32/stm32f10x_lib/include/cortexm" -I"/home/chm/git/stm32/stm32f10x_lib/include/diag" -I"/home/chm/git/stm32/stm32f10x_lib/include/freertos" -I"/home/chm/git/stm32/stm32f10x_lib/include/hal" -I"/home/chm/git/stm32/stm32f10x_lib/include/stm32f1" -std=gnu++11 -fabi-version=0 -fno-exceptions -fno-rtti -fno-use-cxa-atexit -fno-threadsafe-statics -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

stm32f10x_lib/src/hal/digitaloutput.o: /home/chm/git/stm32/stm32f10x_lib/src/hal/digitaloutput.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C++ Compiler'
	arm-none-eabi-g++ -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DHSE_VALUE=80000000 -DUSE_STDPERIPH_DRIVER -DTRACE -I"/home/chm/git/ScalaTest/especial/csrc/include" -I"/home/chm/git/stm32/stm32f10x_lib/include" -I"/home/chm/git/stm32/stm32f10x_lib/include/arm" -I"/home/chm/git/stm32/stm32f10x_lib/include/cmsis" -I"/home/chm/git/stm32/stm32f10x_lib/include/cortexm" -I"/home/chm/git/stm32/stm32f10x_lib/include/diag" -I"/home/chm/git/stm32/stm32f10x_lib/include/freertos" -I"/home/chm/git/stm32/stm32f10x_lib/include/hal" -I"/home/chm/git/stm32/stm32f10x_lib/include/stm32f1" -std=gnu++11 -fabi-version=0 -fno-exceptions -fno-rtti -fno-use-cxa-atexit -fno-threadsafe-statics -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

stm32f10x_lib/src/hal/gpio.o: /home/chm/git/stm32/stm32f10x_lib/src/hal/gpio.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C++ Compiler'
	arm-none-eabi-g++ -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DHSE_VALUE=80000000 -DUSE_STDPERIPH_DRIVER -DTRACE -I"/home/chm/git/ScalaTest/especial/csrc/include" -I"/home/chm/git/stm32/stm32f10x_lib/include" -I"/home/chm/git/stm32/stm32f10x_lib/include/arm" -I"/home/chm/git/stm32/stm32f10x_lib/include/cmsis" -I"/home/chm/git/stm32/stm32f10x_lib/include/cortexm" -I"/home/chm/git/stm32/stm32f10x_lib/include/diag" -I"/home/chm/git/stm32/stm32f10x_lib/include/freertos" -I"/home/chm/git/stm32/stm32f10x_lib/include/hal" -I"/home/chm/git/stm32/stm32f10x_lib/include/stm32f1" -std=gnu++11 -fabi-version=0 -fno-exceptions -fno-rtti -fno-use-cxa-atexit -fno-threadsafe-statics -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

stm32f10x_lib/src/hal/helper.o: /home/chm/git/stm32/stm32f10x_lib/src/hal/helper.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C Compiler'
	arm-none-eabi-gcc -mcpu=cortex-m3 -mthumb -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall -Wextra  -g3 -DSTM32F10X_MD -DHSE_VALUE=80000000 -DUSE_STDPERIPH_DRIVER -DTRACE -I"/home/chm/git/ScalaTest/especial/csrc/include" -I"/home/chm/git/stm32/stm32f10x_lib/include" -I"/home/chm/git/stm32/stm32f10x_lib/include/arm" -I"/home/chm/git/stm32/stm32f10x_lib/include/cmsis" -I"/home/chm/git/stm32/stm32f10x_lib/include/cortexm" -I"/home/chm/git/stm32/stm32f10x_lib/include/diag" -I"/home/chm/git/stm32/stm32f10x_lib/include/freertos" -I"/home/chm/git/stm32/stm32f10x_lib/include/hal" -I"/home/chm/git/stm32/stm32f10x_lib/include/stm32f1" -std=gnu11 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


