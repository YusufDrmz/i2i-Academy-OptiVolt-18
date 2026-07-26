# ⚡ OptiVolt — Gerçek Zamanlı IoT Enerji İzleme Platformu

> i2i Academy — Proje #18

Akıllı ev cihazlarının elektrik tüketimini gerçek zamanlı izleyen, bütçe ihlallerini tespit eden ve Google Gemini AI destekli Türkçe e-posta uyarıları gönderen bir IoT enerji yönetim platformu.

---

## 🛠️ Teknoloji Stack

| Katman | Teknoloji |
|--------|-----------|
| Frontend | React 18 + Vite + Tailwind CSS |
| Backend | Java 17 + Spring Boot 3.x |
| Mesajlaşma | Apache Kafka (KRaft / Zookeeper) |
| In-Memory | Apache Ignite 2.15 |
| Veritabanı | PostgreSQL 15 |
| AI | Google Gemini API |
| Konteyner | Docker + Docker Compose |

---

## 🚀 Kurulum

### Ön Gereksinimler
- Docker Desktop
- Node.js 18+
- Java 17
- Maven 3.8+

### 1. Repoyu Klonla

```bash
git clone https://github.com/YusufDrmz/i2i-Academy-OptiVolt-18.git
cd i2i-Academy-OptiVolt-18
```

### 2. Environment Variables

```bash
cp .env.example .env
```

`.env` dosyasını aç ve aşağıdaki değerleri doldur:

```
DB_USERNAME=optivolt_user
DB_PASSWORD=güvenli_şifre
GEMINI_API_KEY=AIza...
MAIL_USERNAME=email@gmail.com
MAIL_PASSWORD=uygulama_şifresi
```

> **Not:** Gmail App Password için Google hesabında 2FA aktif olmalı. [Buradan](https://myaccount.google.com/apppasswords) oluşturabilirsin.

### 3. Docker Servislerini Başlat

```bash
docker compose up -d
```

Servis durumunu kontrol et:

```bash
docker ps
```

4 container `Up` görünmeli:
- `optivolt-postgres` → Port 5432
- `optivolt-zookeeper` → Port 2181
- `optivolt-kafka` → Port 9092
- `optivolt-ignite` → Port 10800

### 4. Backend'i Başlat (IntelliJ)

`optivolt-core/src/main/java/com/i2i/optivolt/OptivoltCoreApplication.java` dosyasını aç, `main` metodunu run et.

> **Java 17 zorunlu.** IntelliJ → File → Project Structure → SDK → Java 17

### 5. Frontend'i Başlat

```bash
cd optivolt-web
npm install
npm run dev
```

Tarayıcıda aç: **http://localhost:5173**

---

## 📁 Proje Yapısı

```
i2i-Academy-OptiVolt-18/
├── docker-compose.yml
├── .env.example
├── docker/
│   └── postgres/
│       └── init.sql
├── optivolt-core/          # Spring Boot Backend
│   ├── pom.xml
│   └── src/
└── optivolt-web/           # React Frontend
    ├── package.json
    └── src/
        ├── components/
        │   ├── Dashboard/
        │   ├── HomeCard/
        │   ├── HomeDetailModal/
        │   ├── ApplianceDrawer/
        │   ├── AddHomeModal/
        │   ├── AddApplianceModal/
        │   └── PlaceholderPage/
        └── services/
            └── api.js
```

---

## 🔌 API Endpoint'leri

Base URL: `http://localhost:8080/api`  
Swagger UI: `http://localhost:8080/swagger-ui.html`

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/homes` | Yeni konut kaydet |
| GET | `/homes` | Tüm konutları listele (Ignite) |
| GET | `/homes/{id}` | Konut detayı (Ignite + PG) |
| DELETE | `/homes/{id}` | Konut sil (soft delete) |
| GET | `/homes/{id}/history` | Günlük tüketim geçmişi |
| GET | `/homes/{id}/notifications` | AI bildirim geçmişi |
| GET | `/tariffs` | Tarife listesi |

---

## ⚙️ Kafka Topic'leri

| Topic | Producer | Consumer |
|-------|----------|----------|
| `voltwise.asset.registration` | Core | Telemetry Sensors |
| `voltwise.telemetry.stream` | Telemetry Sensors | Core |

---

## 💡 İş Kuralları

- **%80 kota** → AI uyarı e-postası gönderilir
- **%100 kota** → Ceza tarifesi aktif (2x), AI uyarısı
- **3 ardışık ihlal** → Cihaz anomali olarak işaretlenir, AI uyarısı
- **Tarife dilimleri:** Gece 0.8x · Gündüz 1.0x · Peak 1.5x · Akşam 1.2x

---

## 👥 Ekip

| Üye | Sorumluluk |
|-----|------------|
| Yusuf (YusufDrmz) | Frontend + Altyapı |
| Üye 2 | Core: Veri & Akış |
| Üye 3 | Core: İş Mantığı, AI & Sensörler |

---

*i2i Academy · 2026*
