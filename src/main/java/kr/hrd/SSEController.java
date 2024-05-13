package kr.hrd;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Tag(name = "전자지갑 테스트", description = "전자지갑 SSE 테스트 콘트롤")
@RestController
public class SSEController {

    @Operation(summary = "토큰검증" , description = "SseEmitter으로 토큰을 검증한다.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "요청에 성공하였습니다."),
            @ApiResponse(responseCode = "404", description = "요청에 실패하였습니다.")
    })
    @Parameters({
            @Parameter(name = "token", description = "토큰을 입력한다.", example = "aklsdjfaskjdfklasdjfkljas")
    })
    @PostMapping(value = "/api/{token}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("token") String token) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .method("POST", HttpRequest.BodyPublishers.ofString(token))
                .uri(URI.create("http://localhost:8082/api/"+ token))
                .timeout(Duration.ofSeconds(120))
                .build();

        Stream<String> linesInResponse = client.send(request, HttpResponse.BodyHandlers.ofLines()).body();

        SseEmitter emitter = new SseEmitter();
        Map<String, Object> pMap = new HashMap<>();
        pMap.put("key", linesInResponse);
        emitter.send(pMap);
        emitter.complete();

        return emitter;
    }
}
