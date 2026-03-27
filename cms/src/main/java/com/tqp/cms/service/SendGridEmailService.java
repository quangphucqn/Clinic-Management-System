package com.tqp.cms.service;

import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendGridEmailService {
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.sendgrid.com")
            .build();

    @Value("${sendgrid.enabled:false}")
    private boolean enabled;

    @Value("${sendgrid.api-key:}")
    private String apiKey;

    @Value("${sendgrid.from-email:}")
    private String fromEmail;

    public void send(String subject, String content, List<String> toEmails) {
        if (toEmails == null || toEmails.isEmpty()) {
            return;
        }

        if (!enabled || apiKey == null || apiKey.isBlank() || fromEmail == null || fromEmail.isBlank()) {
            throw new AppException(ErrorCode.SENDGRID_NOT_CONFIGURED);
        }

        var personalizations = toEmails.stream()
                .distinct()
                .map(email -> Map.of("to", List.of(Map.of("email", email))))
                .toList();

        Map<String, Object> body = Map.of(
                "personalizations", personalizations,
                "from", Map.of("email", fromEmail),
                "subject", subject,
                "content", List.of(Map.of("type", "text/plain", "value", content))
        );

        try {
            restClient.post()
                    .uri("/v3/mail/send")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
                    })
                    .toBodilessEntity();
        } catch (AppException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("SendGrid email send failed", exception);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
