// datServ.js 
// takes sensor input from MCP8004 and creates a live data stream
// Andrew Young

// Load RPi gpio library 
var gpio = require('rpi-gpio');
gpio.setMode(gpio.MODE_BCM);

// Pin numbers
var CLOCK = 18;
var MOSI = 23;
var MISO = 24;
var CS = 25;

// Setting up pins for use
gpio.setup(CLOCK, gpio.DIR_OUT);
gpio.setup(MOSI, gpio.DIR_OUT);
gpio.setup(MISO, gpio.DIR_IN);
gpio.setup(CS, gpio.DIR_OUT);

// Patterned after Limor Fried's readadc in python

function readADC(adcnum, clockpin, mosipin, misopin, cspin){
	if((adcnum>7) || (adcnum<0)){
		return -1;
	}

	gpio.output(cspin, true);
	gpio.output(clockpin, false); // start clock low
	gpio.output(cspin, false); // bring CS low

	commandout = adcnum;
	commandout |= 0x18; // start bit + single-ended bit
	commandout <<=3; // only send 5 bits

	// Write the command requesting data to output
	for(i=0; i<5; i++){
		if(commandout & 0x80){
			gpio.output(mosipin, true);
		} else{
			gpio.output(mosipin, false);
		}
		commandout <<=1;
		
		// Twiddle the clock to write the next bit
		gpio.output(clockpin, true);
		gpio.output(clockpin, false);
	}

	var adcout = 0;

	// Read 12 bits - one empty, one null, 10 ADC bits

	for(i=0; i<12; i++){
		// Twiddle the clock
		gpio.output(clockpin, true);
		gpio.output(clockpin, false);

		adcout <<=1;
		
		if(gpio.input(misopin)){
			adcout |=0x1;
		}
	}

	gpio.output(cspin, true);

	// drop null bit
	adcout >> 1;
	
	return adcout;
}

var reading = readADC(0, CLOCK, MOSI, MISO, CS);
console.log("Reading: " + reading);
