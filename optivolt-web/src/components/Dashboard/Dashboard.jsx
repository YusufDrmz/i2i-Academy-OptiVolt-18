import React, { useEffect, useState } from 'react';
import { fetchHomes } from '../../services/api';
import HomeCard from '../HomeCard/HomeCard';
import { Activity, ShieldAlert, Zap } from 'lucide-react';
import './Dashboard.css';

const Dashboard = () => {
  const [homes, setHomes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedHome, setSelectedHome] = useState(null);

  useEffect(() => {
    const loadData = async () => {
      const data = await fetchHomes();
      setHomes(data);
      setLoading(false);
    };

    loadData();
    // 5 saniyede bir canlı polling simülasyonu
    const interval = setInterval(loadData, 5000);
    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return <div className="loading-state">VoltWise Akıllı Izgara Verileri Yükleniyor...</div>;
  }

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div>
          <h1>VoltWise Izgara İzleme Paneli</h1>
          <p>Gerçek Zamanlı Enerji Tüketimi ve Kota Yönetimi</p>
        </div>
        <div className="header-stats">
          <div className="stat-box"><Zap color="#3b82f6" /> <span>Toplam Ev: {homes.length}</span></div>
          <div className="stat-box"><ShieldAlert color="#ef4444" /> <span>Kritik Aşım: {homes.filter(h => h.status === 'CRITICAL').length}</span></div>
        </div>
      </header>

      <div className="grid-container">
        {homes.map(home => (
          <HomeCard key={home.id} home={home} onSelect={(h) => setSelectedHome(h)} />
        ))}
      </div>

      {selectedHome && (
        <div className="selected-info">
          Seçili Konut: <strong>{selectedHome.name}</strong> (Detay Modalı 2. Aşamada Bağlanacak)
        </div>
      )}
    </div>
  );
};

export default Dashboard;