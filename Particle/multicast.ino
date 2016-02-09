// Multicast Sample Code

// Define this to also output debugging information to the serial console
//#define SERIAL_DEBUG

int lastValue = 0;

// UDP support
UDP udp;
const size_t bufferSize = 16; // Make this bigger if you have more data!
unsigned char buffer[bufferSize];
// Note: remoteIP is a multicast address, it should begin with 239.x.x.x or some other
// appropriate multicast address.
// It's not the same as your local IP address (192.168.x.x, for example)!
IPAddress remoteIP(239,1,1,234);

// The remote port must be otherwise unused on any machine that is receiving these packets
int remotePort = 7234;


void setup() {
    #ifdef SERIAL_DEBUG
    Serial.begin(9600);
    #endif

    // We don't listen for UDP packets, so pass 0 here.
    udp.begin(0);

    pinMode(A0, INPUT);
    Particle.variable("value", lastValue);
}

void loop() {
    int value = analogRead(A0);

    int delta = value - lastValue;

    // Only send a packet when data change is sufficiently large
    if (delta < -3 || delta > 3) {
        // Data buffer, we send the 16-bit integer value in network byte order (big endian)
        buffer[0] = value >> 8;
        buffer[1] = (value & 0xff);

        // Send the data
        if (udp.sendPacket(buffer, bufferSize, remoteIP, remotePort) >= 0) {
            // Success
            #ifdef SERIAL_DEBUG
            Serial.printlnf("%d", value);
            #endif
        }
        else {
            #ifdef SERIAL_DEBUG
            Serial.printlnf("send failed");
            #endif
            // On error, wait a moment, then reinitialize UDP and try again.
            delay(1000);
            udp.begin(0);
        }

        lastValue = value;
    }

    delay(100);
}
