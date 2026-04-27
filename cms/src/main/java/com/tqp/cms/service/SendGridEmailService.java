package com.tqp.cms.service;

import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class SendGridEmailService {
    private static final String DEFAULT_LOGO_URL = "https://res.cloudinary.com/dkhl4h3nz/image/upload/v1777220016/icons8-clinic-96_1_ecv8ax.png";
    private static final String DEFAULT_THEME_COLOR = "#72d6e2";
    private static final String DEFAULT_SUPPORT_EMAIL = "support@clinic.local";
    private static final String DEFAULT_SUPPORT_HOTLINE = "0825499079";

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.sendgrid.com")
            .build();

    @Value("${sendgrid.enabled:false}")
    private boolean enabled;

    @Value("${sendgrid.api-key:}")
    private String apiKey;

    @Value("${sendgrid.from-email:}")
    private String fromEmail;

    @Value("${sendgrid.from-name:Clinic Management System}")
    private String fromName;

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
                "from", Map.of("email", fromEmail, "name", fromName),
                "subject", subject,
                "content", List.of(
                        Map.of("type", "text/plain", "value", content),
                        Map.of("type", "text/html", "value", buildHtmlEmail(subject, content))
                )
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

    private String buildHtmlEmail(String subject, String content) {
        String safeSubject = escapeHtml(subject);
        String safeContent = escapeHtml(content).replace("\n", "<br/>");
        String safeFromName = escapeHtml(fromName);
        String safeSupportEmail = escapeHtml(DEFAULT_SUPPORT_EMAIL);
        String safeSupportHotline = escapeHtml(DEFAULT_SUPPORT_HOTLINE);
        String sentAt = java.time.LocalDateTime.now().toString().replace("T", " ");

        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>%s</title>
                </head>
                <body style="margin:0;padding:0;background:#f3f4f6;font-family:Arial,sans-serif;color:#1f2937;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f3f4f6;padding:24px 0;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="640" cellpadding="0" cellspacing="0" style="max-width:640px;background:#ffffff;border-radius:10px;overflow:hidden;">
                          <tr>
                            <td align="center" style="background:%s;padding:26px 20px;">
                              <img src="%s" alt="logo" style="max-width:180px;height:auto;display:block;margin:0 auto;" />
                              <p style="margin:10px 0 0 0;color:#ffffff;font-size:16px;font-weight:600;">%s</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:28px 36px;">
                              <p style="margin:0 0 10px 0;font-size:14px;color:#6b7280;">Thong bao tu he thong</p>
                              <h2 style="margin:0 0 16px 0;font-size:28px;line-height:1.3;color:#111827;">%s</h2>
                              <p style="margin:0;font-size:17px;line-height:1.8;color:#1f2937;">%s</p>
                              <p style="margin:16px 0 0 0;font-size:13px;color:#6b7280;">Thoi gian gui: %s</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:0 36px 28px 36px;">
                              <hr style="border:none;border-top:1px solid #e5e7eb;margin:0 0 20px 0;" />
                              <p style="margin:0 0 8px 0;font-size:14px;color:#4b5563;">Need support? Contact us:</p>
                              <p style="margin:0;font-size:14px;color:#111827;">
                                Hotline: <strong>%s</strong><br/>
                                Email: <a href="mailto:%s" style="color:%s;text-decoration:none;">%s</a>
                              </p>
                              <p style="margin:22px 0 0 0;font-size:14px;color:#6b7280;">
                                Best regards,<br/>
                                <strong>%s</strong>
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(
                safeSubject,
                DEFAULT_THEME_COLOR,
                DEFAULT_LOGO_URL,
                safeFromName,
                safeSubject,
                safeContent,
                sentAt,
                safeSupportHotline,
                safeSupportEmail,
                DEFAULT_THEME_COLOR,
                safeSupportEmail,
                safeFromName
        );
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
