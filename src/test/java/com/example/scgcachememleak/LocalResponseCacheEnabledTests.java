package com.example.scgcachememleak;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.statics.StaticsService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
class LocalResponseCacheEnabledTests {

  @LocalServerPort
  protected int mPort = 0;

  @Autowired
  private WebTestClient webClient;

  @BeforeAll
  static void setup() {
    System.setProperty("io.netty.leakDetection.level", "PARANOID");
    StaticsService.main(new String[0]);
  }

  @Test
  void test(CapturedOutput logOutput) throws InterruptedException {
    for (int i = 0; i < 5; i++) {
      webClient.get().uri("/x/" + i + ".jpg").exchange().expectStatus().is2xxSuccessful().expectBody()
          .consumeWith(res -> res.getRequestBodyContent());

      Runtime.getRuntime().gc();
      Runtime.getRuntime().gc();

      TimeUnit.SECONDS.sleep(1);

      webClient.get().uri("/x/" + i + ".jpg").exchange().expectStatus().is2xxSuccessful().expectBody()
          .consumeWith(res -> res.getRequestBodyContent());

      Assertions.assertFalse(logOutput.getAll().contains("LEAK: ByteBuf.release()"),
          "memory leaked, for more information see log output");
    }
  }
}
