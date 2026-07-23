package com.i2i.optivolt.notification;

import com.i2i.optivolt.rules.AlertEvent;
import com.i2i.optivolt.rules.AlertPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor

public class NotificationService implements AlertPublisher {

    private final GeminiClient geminiClient;
    private final EmailService emailService;
    private final EventLogRepository eventLogRepository;
    private final HomeInfoProvider homeInfoProvider;

    @Override
    public void publish(AlertEvent event) {
        HomeContact contact = homeInfoProvider.getHomeContact(event.getHomeId());
        String prompt = buildPrompt(event, contact);
        String advisoryText = geminiClient.generateAdvisory(prompt);

        EventLog eventLog = new EventLog();
        eventLog.setHomeId(event.getHomeId());
        eventLog.setEventType(event.getType().name());
        eventLog.setSeverity(severityFor(event));
        eventLog.setMessage(advisoryText);
        eventLog.setEmailSent(false);
        eventLogRepository.save(eventLog);

        try {
            emailService.sendEnergyAlert(contact.getContactEmail(), subjectFor(event), advisoryText);
            eventLog.setEmailSent(true);
            eventLogRepository.save(eventLog);
        } catch (Exception e) {
            log.warn("Advisory for home {} persisted but email was not sent", event.getHomeId());
        }
    }

    private String buildPrompt(AlertEvent event, HomeContact contact) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sen VoltWise adlı bir ev enerji yönetimi asistanısın. ");
        sb.append("Aşağıdaki duruma göre ").append(contact.getHomeName())
                .append(" için samimi, kısa (3-4 cümle) ve uygulanabilir bir Türkçe enerji tasarrufu tavsiyesi yaz. ");
        sb.append("Sadece tavsiye metnini döndür, başka açıklama ekleme.\n\n");

        switch (event.getType()) {
            case QUOTA_80_PERCENT -> sb.append("Durum: Ev, aylık bütçe kotasının %80'ine ulaştı. ");
            case QUOTA_100_PERCENT -> sb.append("Durum: Ev, aylık bütçe kotasını aştı ve ceza tarifesine geçildi. ");
            case DEVICE_ANOMALY -> sb.append("Durum: '").append(event.getApplianceName())
                    .append("' cihazı art arda güvenli tüketim sınırını aştı ve anormal olarak işaretlendi. ");
        }

        sb.append(String.format(
                "Bugünkü toplam tüketim: %.1f W, bugünkü toplam maliyet: %.2f TL, bütçe limiti: %.2f TL.",
                event.getTotalWattToday(), event.getTotalCostToday(), event.getBudgetQuotaTry()));

        return sb.toString();
    }

    private String subjectFor(AlertEvent event) {
        return switch (event.getType()) {
            case QUOTA_80_PERCENT -> "VoltWise: Bütçenizin %80'ine ulaştınız";
            case QUOTA_100_PERCENT -> "VoltWise: Bütçe limitiniz aşıldı - ceza tarifesi aktif";
            case DEVICE_ANOMALY -> "VoltWise: " + event.getApplianceName() + " için anomali tespit edildi";
        };
    }

    private String severityFor(AlertEvent event) {
        return switch (event.getType()) {
            case QUOTA_80_PERCENT -> "WARNING";
            case QUOTA_100_PERCENT -> "CRITICAL";
            case DEVICE_ANOMALY -> "CRITICAL";
        };
    }
}