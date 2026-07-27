import axios from 'axios';
const API_BASE_URL = 'https://statewide-std-coordinator-hayes.trycloudflare.com/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 2000,
});
// ─── Mock Data ────────────────────────────────────────────────────────────────

export const mockHomes = [
  {
    id: 1,
    name: "Daire 12 - Yılmaz Ailesi",
    address: "Göztepe, İzmir",
    contactEmail: "yilmaz@example.com",
    currentWatt: 4200,
    powerQuotaWatt: 5000,
    budgetQuotaTry: 1500,
    currentCostTry: 1120,
    status: "WARNING",
    appliancesCount: 3
  },
  {
    id: 2,
    name: "Villa 4 - Kaya Ailesi",
    address: "Alsancak, İzmir",
    contactEmail: "kaya@example.com",
    currentWatt: 5800,
    powerQuotaWatt: 5000,
    budgetQuotaTry: 2000,
    currentCostTry: 2150,
    status: "CRITICAL",
    appliancesCount: 5
  },
  {
    id: 3,
    name: "Daire 5 - Demir Ailesi",
    address: "Karşıyaka, İzmir",
    contactEmail: "demir@example.com",
    currentWatt: 1800,
    powerQuotaWatt: 4500,
    budgetQuotaTry: 1200,
    currentCostTry: 450,
    status: "NORMAL",
    appliancesCount: 2
  }
];

export const mockAppliancesByHomeId = {
  1: [
    { id: 101, name: "Buzdolabı",         currentWatt: 142,  maxSafeWatt: 150,  isAnomalous: false, consecutiveBreaches: 0 },
    { id: 102, name: "Klima",             currentWatt: 2800, maxSafeWatt: 2000, isAnomalous: true,  consecutiveBreaches: 3 },
    { id: 103, name: "Çamaşır Makinesi", currentWatt: 1258, maxSafeWatt: 2500, isAnomalous: false, consecutiveBreaches: 1 },
  ],
  2: [
    { id: 201, name: "Buzdolabı",          currentWatt: 160,  maxSafeWatt: 150,  isAnomalous: true,  consecutiveBreaches: 3 },
    { id: 202, name: "Klima (Salon)",      currentWatt: 2100, maxSafeWatt: 2000, isAnomalous: true,  consecutiveBreaches: 4 },
    { id: 203, name: "Klima (Yatak Oda)", currentWatt: 1950, maxSafeWatt: 2000, isAnomalous: false, consecutiveBreaches: 0 },
    { id: 204, name: "Fırın",             currentWatt: 1100, maxSafeWatt: 2200, isAnomalous: false, consecutiveBreaches: 0 },
    { id: 205, name: "Çamaşır Makinesi", currentWatt: 490,  maxSafeWatt: 2500, isAnomalous: false, consecutiveBreaches: 0 },
  ],
  3: [
    { id: 301, name: "Buzdolabı",        currentWatt: 130, maxSafeWatt: 150, isAnomalous: false, consecutiveBreaches: 0 },
    { id: 302, name: "TV + Ses Sistemi", currentWatt: 220, maxSafeWatt: 400, isAnomalous: false, consecutiveBreaches: 0 },
  ],
};

const mockHistoryByHomeId = {
  1: [
    { date: "17 Tem", totalKwh: 18.2, totalCost: 39.1 },
    { date: "18 Tem", totalKwh: 21.5, totalCost: 46.2 },
    { date: "19 Tem", totalKwh: 19.8, totalCost: 42.6 },
    { date: "20 Tem", totalKwh: 24.1, totalCost: 51.8 },
    { date: "21 Tem", totalKwh: 22.3, totalCost: 47.9 },
    { date: "22 Tem", totalKwh: 26.7, totalCost: 57.4 },
    { date: "23 Tem", totalKwh: 28.0, totalCost: 60.2 },
  ],
  2: [
    { date: "17 Tem", totalKwh: 32.1, totalCost: 69.0 },
    { date: "18 Tem", totalKwh: 35.4, totalCost: 76.1 },
    { date: "19 Tem", totalKwh: 38.9, totalCost: 83.6 },
    { date: "20 Tem", totalKwh: 41.2, totalCost: 88.6 },
    { date: "21 Tem", totalKwh: 44.0, totalCost: 94.6 },
    { date: "22 Tem", totalKwh: 47.3, totalCost: 101.7 },
    { date: "23 Tem", totalKwh: 50.1, totalCost: 107.7 },
  ],
  3: [
    { date: "17 Tem", totalKwh: 9.1,  totalCost: 19.6 },
    { date: "18 Tem", totalKwh: 10.3, totalCost: 22.1 },
    { date: "19 Tem", totalKwh: 8.7,  totalCost: 18.7 },
    { date: "20 Tem", totalKwh: 11.2, totalCost: 24.1 },
    { date: "21 Tem", totalKwh: 9.8,  totalCost: 21.1 },
    { date: "22 Tem", totalKwh: 10.5, totalCost: 22.6 },
    { date: "23 Tem", totalKwh: 12.0, totalCost: 25.8 },
  ],
};

const mockApplianceHistory = {
  101: [ { date: "17 Tem", avgWatt: 138 }, { date: "18 Tem", avgWatt: 141 }, { date: "19 Tem", avgWatt: 135 }, { date: "20 Tem", avgWatt: 143 }, { date: "21 Tem", avgWatt: 139 }, { date: "22 Tem", avgWatt: 142 }, { date: "23 Tem", avgWatt: 142 } ],
  102: [ { date: "17 Tem", avgWatt: 1850 }, { date: "18 Tem", avgWatt: 2100 }, { date: "19 Tem", avgWatt: 2400 }, { date: "20 Tem", avgWatt: 2650 }, { date: "21 Tem", avgWatt: 2750 }, { date: "22 Tem", avgWatt: 2800 }, { date: "23 Tem", avgWatt: 2800 } ],
  103: [ { date: "17 Tem", avgWatt: 900 }, { date: "18 Tem", avgWatt: 0 }, { date: "19 Tem", avgWatt: 1100 }, { date: "20 Tem", avgWatt: 0 }, { date: "21 Tem", avgWatt: 1258 }, { date: "22 Tem", avgWatt: 950 }, { date: "23 Tem", avgWatt: 1258 } ],
  201: [ { date: "17 Tem", avgWatt: 148 }, { date: "18 Tem", avgWatt: 155 }, { date: "19 Tem", avgWatt: 158 }, { date: "20 Tem", avgWatt: 160 }, { date: "21 Tem", avgWatt: 162 }, { date: "22 Tem", avgWatt: 160 }, { date: "23 Tem", avgWatt: 160 } ],
  202: [ { date: "17 Tem", avgWatt: 1800 }, { date: "18 Tem", avgWatt: 1950 }, { date: "19 Tem", avgWatt: 2000 }, { date: "20 Tem", avgWatt: 2050 }, { date: "21 Tem", avgWatt: 2080 }, { date: "22 Tem", avgWatt: 2100 }, { date: "23 Tem", avgWatt: 2100 } ],
  203: [ { date: "17 Tem", avgWatt: 1700 }, { date: "18 Tem", avgWatt: 1800 }, { date: "19 Tem", avgWatt: 1850 }, { date: "20 Tem", avgWatt: 1900 }, { date: "21 Tem", avgWatt: 1920 }, { date: "22 Tem", avgWatt: 1940 }, { date: "23 Tem", avgWatt: 1950 } ],
  301: [ { date: "17 Tem", avgWatt: 125 }, { date: "18 Tem", avgWatt: 128 }, { date: "19 Tem", avgWatt: 130 }, { date: "20 Tem", avgWatt: 127 }, { date: "21 Tem", avgWatt: 129 }, { date: "22 Tem", avgWatt: 131 }, { date: "23 Tem", avgWatt: 130 } ],
  302: [ { date: "17 Tem", avgWatt: 180 }, { date: "18 Tem", avgWatt: 210 }, { date: "19 Tem", avgWatt: 195 }, { date: "20 Tem", avgWatt: 220 }, { date: "21 Tem", avgWatt: 215 }, { date: "22 Tem", avgWatt: 218 }, { date: "23 Tem", avgWatt: 220 } ],
};

const mockNotificationsByHomeId = {
  1: [
    { id: 1, type: "QUOTA_80", sentAt: "2026-07-23T18:42:00", emailSent: true, content: "Sayın kullanıcı, Daire 12 konutunuzda aylık bütçenizin %80'ine ulaşıldı. Mevcut tüketim hızınızda devam edilmesi halinde ay sonundan önce bütçenizi aşmanız beklenmektedir. Özellikle akşam saatlerinde klima kullanımınızı azaltmanızı öneririz." },
    { id: 2, type: "ANOMALY_DETECTED", sentAt: "2026-07-22T14:15:00", emailSent: true, content: "Daire 12 konutunuzdaki Klima cihazı art arda 3 ölçüm döngüsünde güvenli güç limitinin üzerinde çalışmaktadır. Cihazınızı kontrol etmenizi ve gerekirse teknik servis çağırmanızı öneririz." },
  ],
  2: [
    { id: 3, type: "QUOTA_100", sentAt: "2026-07-23T20:10:00", emailSent: true, content: "Villa 4 konutunuz aylık bütçe kotasını aşmıştır. Bu andan itibaren tüketim cezalı tarife üzerinden hesaplanacaktır. Gereksiz cihazları kapatarak ek maliyeti minimize edebilirsiniz." },
    { id: 4, type: "ANOMALY_DETECTED", sentAt: "2026-07-23T19:55:00", emailSent: true, content: "Villa 4 konutunuzdaki Buzdolabı ve Klima (Salon) cihazları anormal güç tüketimi göstermektedir. Lütfen kontrol edin." },
    { id: 5, type: "QUOTA_80", sentAt: "2026-07-22T16:30:00", emailSent: true, content: "Villa 4 konutunuz aylık bütçesinin %80'ine ulaşmıştır. Tüketimi azaltmazsanız kota aşımı gerçekleşecektir." },
  ],
  3: [],
};

// ─── API Fonksiyonları ─────────────────────────────────────────────────────────

export const fetchHomes = async () => {
  try {
    const response = await api.get('/homes');
    return response.data;
  } catch {
    return mockHomes;
  }
};
export const fetchHomeDetail = async (homeId) => {
  try {
    const [homeRes, historyRes] = await Promise.all([
      api.get(`/homes/${homeId}`),
      api.get(`/homes/${homeId}/history`),
    ]);
    return { home: homeRes.data, history: historyRes.data };
  } catch {
    const baseHome = mockHomes.find(h => h.id === homeId);
    return {
      home: { ...baseHome, appliances: mockAppliancesByHomeId[homeId] || [] },
      history: mockHistoryByHomeId[homeId] || [],
    };
  }
};

export const fetchApplianceDetail = async (applianceId) => {
  try {
    const response = await api.get(`/appliances/${applianceId}/history`);
    return { history: response.data };
  } catch {
    return { history: mockApplianceHistory[applianceId] || [] };
  }
};

export const fetchHomeNotifications = async (homeId) => {
  try {
    const response = await api.get(`/homes/${homeId}/notifications`);
    return response.data;
  } catch {
    return mockNotificationsByHomeId[homeId] || [];
  }
};
export const addHome = async (homeData) => {
  try {
    const response = await api.post('/homes', homeData);
    return response.data;
  } catch {
    const newHome = {
      id: Date.now(),
      ...homeData,
      currentWatt: 0,
      currentCostTry: 0,
      status: 'NORMAL',
      appliancesCount: homeData.appliances?.length ?? 0,
    };
    mockHomes.push(newHome);
    return newHome;
  }
};

export const addAppliance = async (homeId, applianceData) => {
  try {
    const response = await api.post(`/homes/${homeId}/appliances`, applianceData);
    return response.data;
  } catch {
    const newAppliance = {
      id: Date.now(),
      ...applianceData,
      currentWatt: 0,
      isAnomalous: false,
      consecutiveBreaches: 0,
    };
    const list = mockAppliancesByHomeId[homeId];
    if (list) list.push(newAppliance);
    return newAppliance;
  }
};
