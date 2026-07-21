import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

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
    status: "WARNING", // NORMAL (%0-79), WARNING (%80-99), CRITICAL (%100+)
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

export const fetchHomes = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/homes`);
    return response.data;
  } catch (error) {
    console.warn("Backend henüz yayında değil, Mock veriler yükleniyor...", error);
    return mockHomes;
  }
};