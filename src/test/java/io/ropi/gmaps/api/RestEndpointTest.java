package io.ropi.gmaps.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RestEndpointTest {

    @Test
    void roundUpToNextPowerOfTwo() {
        assertThat(RestEndpoint.roundUpToNextPowerOfTwo(5)).isEqualTo(8);
        assertThat(RestEndpoint.roundUpToNextPowerOfTwo(33)).isEqualTo(64);
        assertThat(RestEndpoint.roundUpToNextPowerOfTwo(32)).isEqualTo(32);
    }

    @Test
    void bitCount() {
        assertThat(RestEndpoint.bitCount(5)).isEqualTo(3);
        assertThat(RestEndpoint.bitCount(33)).isEqualTo(6);
        assertThat(RestEndpoint.bitCount(32)).isEqualTo(6);
        assertThat(RestEndpoint.bitCount(31)).isEqualTo(5);
    }
}
