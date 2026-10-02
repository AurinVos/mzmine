/*
 * Copyright (c) 2026 The MZmine Development Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.mzmine.modules.io.import_rawdata_wiff2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.netty.handler.ssl.OpenSsl;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class GrpcTransportTest {

  @Test
  void wiffPlaintextChannelUsesUnshadedProviderWithoutNativeTls() throws InterruptedException {
    // Creating an idle channel follows Wiff2DataAccess startup without connecting or issuing RPCs.
    ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress("localhost", 12345)
        .usePlaintext();
    assertEquals("io.grpc.netty.NettyChannelBuilder", builder.getClass().getName());
    assertFalse(OpenSsl.isAvailable(), "The open distribution must not load native TLS binaries");
    assertThrows(ClassNotFoundException.class,
        () -> Class.forName("io.netty.internal.tcnative.SSLContext"));

    ManagedChannel channel = builder.build();
    try {
      assertFalse(channel.isShutdown());
    } finally {
      channel.shutdownNow();
    }
    assertTrue(channel.awaitTermination(5, TimeUnit.SECONDS));
    assertTrue(channel.isTerminated());
  }
}
