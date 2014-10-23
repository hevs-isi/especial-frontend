
#include <stdint.h>

#ifndef HELPER_H
#define HELPER_H

/* Initialize the LED (the board only has one). */
void led_init(void);

void led_toggle(void);

/* Configures the RS232 serial port using the following settings:
 *   9600 Baud
 *   8 bits + 1 stop bit
 *   No parity bit
 *   No hardware flow control
 * Note that the USART2 is not enabled in this routine.  It is left disabled in
 * case any additional configuration is needed.
 */
void init_rs232(void);

void enable_rs232_interrupts(void);

void enable_rs232(void);

void send_byte(uint8_t b);

void send_string(const char* s);

#endif /* HELPER_H */
