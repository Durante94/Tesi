package com.fabrizio.tesi.gateway.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.DefaultWebSessionManager;

import com.fabrizio.tesi.gateway.dto.CallbackRequest;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;

@SpringBootTest(properties = {"keycloack.oidcaddr=http://localhost:50000/auth"})
@FieldDefaults(level = AccessLevel.PRIVATE)
class ConfigControllerMockServerTest {
    static MockWebServer mockBackEnd;

    @Autowired
    ConfigController controller;

    @Value("${keycloack.feingress}")
    String entryPoint;

    MockServerHttpResponse response;
    WebSession session;

    final String[] bodies = new String[]{
            "{"//RISPOSTA INVALIDA
                    + "\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.iYi-4oDGqLNK5m48rpr6eyskfJMFvpn4srms_gz4hSM\","
                    + "\"refresh_token\":\"\""
                    + "}",
            "{"//RISPOSTA SENZA PROPRIETà PER I RUOLI
                    + "\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyZXNvdXJjZV9hY2Nlc3MiOnt9fQ.v0-pswAorOPnzXw0h0BMg-vSYxbBNFfJRlT8vx2eksk\","
                    + "\"refresh_token\":\"\""
                    + "}",
            "{"//RISPOSTA SENZA RUOLI
                    + "\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyZXNvdXJjZV9hY2Nlc3MiOnsidGVzaS1hcHAiOiJ7XCJyb2xlc1wiOltdfSJ9fQ.nJqNQ8yWhu1_nMvYoffw2bUpE9EyI1jwsMFxssm6COA\","
                    + "\"refresh_token\":\"\""
                    + "}",
            "{"//ACCESSO SENZA RUOLI VALIDI
                    + "\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyZXNvdXJjZV9hY2Nlc3MiOnsidGVzaS1hcHAiOiJ7XCJyb2xlc1wiOltcInNjZW1vXCJdfSJ9fQ.QRvb0jER0iGlPFLX0mLWCR2j1Ix_s1pFB_kQXZfeAa0\","
                    + "\"refresh_token\":\"\""
                    + "}",
            "{"//ACCESSO SENZA RUOLI VALIDI
                    + "\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyZXNvdXJjZV9hY2Nlc3MiOnsidGVzaS1hcHAiOiJ7XCJyb2xlc1wiOltcInNjZW1vXCJdfSJ9fQ.QRvb0jER0iGlPFLX0mLWCR2j1Ix_s1pFB_kQXZfeAa0\","
                    + "\"refresh_token\":\"\","
                    + "\"id_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1MTYyMzkwMjIsImV4cCI6MTUxNjIzOTAyMn0.BA8MjTQQmXNtRSOhTvnibDjbtx7M5KO8M4XLQu-Oizg\","
                    + "\"expires_in\": 1505468754"
                    + "}",
            "{"//ACCESSO COME ANALIST
                    + "\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyZXNvdXJjZV9hY2Nlc3MiOnsidGVzaS1hcHAiOiJ7XCJyb2xlc1wiOltcImFuYWxpc3RcIl19In19.WKw5MNE2JatWp_O0cFMTdaxdl5PhOGh_oKfysrn8yT8\","
                    + "\"refresh_token\":\"\","
                    + "\"id_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1MTYyMzkwMjIsImV4cCI6MTUxNjIzOTAyMn0.BA8MjTQQmXNtRSOhTvnibDjbtx7M5KO8M4XLQu-Oizg\","
                    + "\"expires_in\": 1505468754"
                    + "}",
            "{"//ACCESSO COME ADMIN
                    + "\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyZXNvdXJjZV9hY2Nlc3MiOnsidGVzaS1hcHAiOiJ7XCJyb2xlc1wiOltcImFkbWluXCJdfSJ9fQ.KhQi_7r8YXfC3SfSyAEEVxGHz5mS4GIg49-fo0RK1-s\","
                    + "\"refresh_token\":\"\","
                    + "\"id_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1MTYyMzkwMjIsImV4cCI6MTUxNjIzOTAyMn0.BA8MjTQQmXNtRSOhTvnibDjbtx7M5KO8M4XLQu-Oizg\","
                    + "\"expires_in\": 1505468754"
                    + "}"
    };

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(50000);
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void init() {
        response = new MockServerHttpResponse();
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        session = sessionManager.getSession(exchange).block();
    }

    @Test
    void destroySessionWithInvalidTokenTest() {
        session.getAttributes().put("RefreshAuth", "cacellami.cacellami.cacellami");

        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.UNAUTHORIZED.value()));

        controller.destroySession(session, response);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertTrue(session.getAttributes().isEmpty());
    }

    @Test
    void destroySessionWithPseudoValidTokenTest() {
        session.getAttributes().put("RefreshAuth", "cacellami.cacellami.cacellami");

        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.NO_CONTENT.value()));

        controller.destroySession(session, response);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertTrue(session.getAttributes().isEmpty());
    }

    @Test
    void getAuthNullRequestCallbackTest() {
        controller.getAuth(session, null, response);

        assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
        assertEquals(URI.create(entryPoint + "/index.html"), response.getHeaders().getLocation());
    }

    @Test
    void getAuthRequestCallbackNoCodeTest() {
        CallbackRequest dto = new CallbackRequest();
        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        try {
            controller.getAuth(session, dto, response);
        } catch (RuntimeException e) {
            assertNotNull(e);
        }
    }

    @Test
    void getAuthRequestCallbackEmptyBodyTest() {
        CallbackRequest dto = new CallbackRequest();
        Buffer body = new Buffer();
        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody(body));

        controller.getAuth(session, dto, response);

        assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
        assertEquals(URI.create(entryPoint + "/index.html"), response.getHeaders().getLocation());
    }

    @Test
    void getAuthRequestCallbackBlankResponseTest() {
        CallbackRequest dto = new CallbackRequest();
        String body = "{}";
        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody(body));

        controller.getAuth(session, dto, response);

        assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
        assertEquals(URI.create(entryPoint + "/index.html"), response.getHeaders().getLocation());
    }

    @Test
    void getAuthRequestCallbackBlankAccesTokenTest() {
        CallbackRequest dto = new CallbackRequest();
        String body = "{\"access_token\":\"\"}";
        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody(body));

        controller.getAuth(session, dto, response);

        assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
        assertEquals(URI.create(entryPoint + "/index.html"), response.getHeaders().getLocation());
    }

    @Test
    void getAuthRequestCallbackBlankRefreshTokenTest() {
        CallbackRequest dto = new CallbackRequest();

        for (int i = 0; i < bodies.length; i++) {
            mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody(bodies[i]));

            controller.getAuth(session, dto, response);
            assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
            if (i >= bodies.length - 2) {
                assertEquals(URI.create(entryPoint + "/web/"), response.getHeaders().getLocation());
                assertNotNull(session.getAttributes().get("Authorization"));
            } else
                assertEquals(URI.create(entryPoint + "/index.html"), response.getHeaders().getLocation());
        }
    }

    @Test
    void refreshJWTTest() {
        Map<String, String>[] sessionAttribute = new Map[]{
                Map.of(),
                Map.of("RefreshAuth", ""),
                Map.of("RefreshAuth", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        };

        for (int i = 0; i < sessionAttribute.length; i++) {
            if (i == sessionAttribute.length - 1) {
                for (int j = 0; j < bodies.length; j++) {
                    mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody(bodies[j]));
                    session.getAttributes().clear();
                    session.getAttributes().putAll(sessionAttribute[i]);

                    ResponseEntity<Integer> resp = controller.refreshJwt(session, null, response);
                    if (j >= bodies.length - 2)
                        assertEquals(HttpStatus.OK, resp.getStatusCode());
                    else if (j == 3)
                        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
                    else
                        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
                }

            } else {
                session.getAttributes().clear();
                session.getAttributes().putAll(sessionAttribute[i]);
                ResponseEntity<Integer> resp = controller.refreshJwt(session, null, response);
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            }
        }
    }
}
