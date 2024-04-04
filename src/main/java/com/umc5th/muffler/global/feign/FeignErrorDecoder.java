package com.umc5th.muffler.global.feign;

import com.umc5th.muffler.global.response.exception.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.BufferedReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

public class FeignErrorDecoder implements ErrorDecoder {

    @SneakyThrows
    @Override
    public Exception decode(String methodKey, Response response) {
        Reader reader = response.body().asReader(StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String errorResult = bufferedReader.lines()
                .collect(Collectors.joining(System.lineSeparator())).replace("\\", "");

        HttpStatus status = HttpStatus.valueOf(response.status());
        String url = response.request().url();

        return new FeignException(status, url, errorResult);
    }
}
