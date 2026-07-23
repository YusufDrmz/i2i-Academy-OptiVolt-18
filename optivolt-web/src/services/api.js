import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

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

const mockAppliancesByHomeId = {
  1: [
    { id: 101, name: "Buzdolabı",          currentWatt: 142,  maxSafeWatt: 150, isAnomalous: false, consecutiveBreaches: 0 },
    { id: 102, name: "Klima",              currentWatt: 2800, maxSafeWatt: 2000, isAnomalous: true,  consecutiveBreaches: 3 },
    { id: 103, name: "Çamaşır Makinesi",  currentWatt: 1258, maxSafeWatt: 2500, isAnomalous: false, consecutiveBreaches: 1 },
  ],
  2: [
    { id: 201, name: "Buzdolabı",          currentWatt: 160,  maxSafeWatt: 150, isAnomalous: true,  consecutiveBreaches: 3 },
    { id: 202, name: "Klima (Salon)",      currentWatt: 2100, maxSafeWatt: 2000, isAnomalous: true,  consecutiveBreaches: 4 },
    { id: 203, name: "Klima (Yatak Oda)", currentWatt: 1950, maxSafeWatt: 2000, isAnomalous: false, consecutiveBreaches: 0 },
    { id: 204, name: "Fırın",             currentWatt: 1100, maxSafeWatt: 2200, isAnomalous: false, consecutiveBreaches: 0 },
    { id: 205, name: "Çamaşır Makinesi",  currentWatt: 490,  maxSafeWatt: 2500, isAnomalous: false, consecutiveBreaches: 0 },
  ],
  3: [
    { id: 301, name: "Buzdolabı",          currentWatt: 130,  maxSafeWatt: 150, isAnomalous: false, consecutiveBreaches: 0 },
    { id: 302, name: "TV + Ses Sistemi",   currentWatt: 220,  maxSafeWatt: 400, isAnomalous: false, consecutiveBreaches: 0 },
  ],
};

// Grafik için günlük tüketim mock datası
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

// ─── API Fonksiyonları ────────────────────────────────────────────────────────

export const fetchHomes = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/homes`);
    return response.data;
  } catch {
    console.warn('Backend henüz yayında değil, mock ev listesi yükleniyor...');
    return mockHomes;
  }
};

export const fetchHomeDetail = async (homeId) => {
  try {
    const [homeRes, historyRes] = await Promise.all([
      axios.get(`${API_BASE_URL}/homes/${homeId}`),
      axios.get(`${API_BASE_URL}/homes/${homeId}/history`),
    ]);
    return {
      home: homeRes.data,
      history: historyRes.data,
    };
  } catch {
    console.warn(`Backend hazır değil, homeId=${homeId} için mock detay yükleniyor...`);
    const baseHome = mockHomes.find(h => h.id === homeId);
    return {
      home: {
        ...baseHome,
        appliances: mockAppliancesByHomeId[homeId] || [],
      },
      history: mockHistoryByHomeId[homeId] || [],
    };
  }
};

export const addHome = async (homeData) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/homes`, homeData);
    return response.data;
  } catch (err) {
    // Backend hazır değilse mock olarak listeye ekle
    console.warn('Backend hazır değil, mock ekleme simüle ediliyor...');
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