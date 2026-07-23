import React, { useEffect, useState } from 'react';
import { fetchHomes } from '../../services/api';
import HomeCard from '../HomeCard/HomeCard';
import HomeDetailModal from '../HomeDetailModal/HomeDetailModal';
import AddHomeModal from '../AddHomeModal/AddHomeModal';
import {
  Zap, ShieldAlert, Plus, LayoutDashboard,
  Activity, Bell, Settings, Home, TrendingUp
} from 'lucide-react';
import './Dashboard.css';

const NAV_ITEMS = [
  { icon: LayoutDashboard, label: 'Dashboard',   active: true  },
  { icon: Home,            label: 'Konutlar',    active: false },
  { icon: Activity,        label: 'Telemetri',   active: false },
  { icon: TrendingUp,      label: 'Raporlar',    active: false },
  { icon: Bell,            label: 'Bildirimler', active: false },
  { icon: Settings,        label: 'Ayarlar',     active: false },
];

const Dashboard = () => {
  const [homes, setHomes]           = useState([]);
  const [loading, setLoading]       = useState(true);
  const [selectedHome, setSelectedHome] = useState(null);
  const [showAddHome, setShowAddHome]   = useState(false);

  const loadData = async () => {
    const data = await fetchHomes();
    setHomes(data);
    setLoading(false);
  };

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 5000);
    return () => clearInterval(interval);
  }, []);

  const criticalCount = homes.filter(h => h.status === 'CRITICAL').length;
  const warningCount  = homes.filter(h => h.status === 'WARNING').length;
  const normalCount   = homes.filter(h => h.status === 'NORMAL').length;
  const totalWatt     = homes.reduce((s, h) => s + h.currentWatt, 0);

  if (loading) {
    return <div className="loading-state">VoltWise yükleniyor…</div>;
  }

  return (
    <div className="app-shell">

      {/* ── Sidebar ── */}
      <aside className="sidebar">
        {/* Logo */}
        <div className="sidebar-logo">
          <div className="sidebar-logo-icon">
            <Zap size={17} />
          </div>
          <div>
            <div className="sidebar-logo-text">VoltWise</div>
            <div className="sidebar-logo-sub">Izgara İzleme</div>
          </div>
        </div>

        {/* Nav */}
        <span className="nav-section-label">Genel</span>
        {NAV_ITEMS.map(({ icon: Icon, label, active }) => (
          <button key={label} className={`nav-item ${active ? 'active' : ''}`}>
            <Icon size={16} />
            {label}
          </button>
        ))}

        {/* Alt kullanıcı */}
        <div className="sidebar-bottom">
          <div className="sidebar-user">
            <div className="user-avatar">VW</div>
            <div>
              <div className="user-name">Operatör</div>
              <div className="user-role">i2i Systems</div>
            </div>
          </div>
        </div>
      </aside>

      {/* ── Ana içerik ── */}
      <div className="main-content">

        {/* Topbar */}
        <header className="topbar">
          <div className="topbar-left">
            <h1>Izgara İzleme Paneli</h1>
            <p>Gerçek zamanlı enerji tüketimi ve kota yönetimi</p>
          </div>
          <div className="topbar-right">
            <div className="stat-pill">
              <Zap size={14} color="#6366f1" />
              {homes.length} Konut
            </div>
            {criticalCount > 0 && (
              <div className="stat-pill critical">
                <ShieldAlert size={14} />
                {criticalCount} Kritik
              </div>
            )}
            <button className="add-home-btn" onClick={() => setShowAddHome(true)}>
              <Plus size={15} />
              Yeni Konut
            </button>
          </div>
        </header>

        {/* Scroll alanı */}
        <div className="content-scroll">

          {/* Özet satırı */}
          <div className="summary-row">
            <div className="summary-card">
              <div className="summary-icon indigo"><Home size={20} /></div>
              <div className="summary-info">
                <span className="summary-value">{homes.length}</span>
                <span className="summary-label">Toplam Konut</span>
              </div>
            </div>
            <div className="summary-card">
              <div className="summary-icon emerald"><Activity size={20} /></div>
              <div className="summary-info">
                <span className="summary-value">{(totalWatt / 1000).toFixed(1)} kW</span>
                <span className="summary-label">Toplam Güç</span>
              </div>
            </div>
            <div className="summary-card">
              <div className="summary-icon amber"><ShieldAlert size={20} /></div>
              <div className="summary-info">
                <span className="summary-value">{warningCount}</span>
                <span className="summary-label">Uyarı Durumu</span>
              </div>
            </div>
            <div className="summary-card">
              <div className="summary-icon red"><Zap size={20} /></div>
              <div className="summary-info">
                <span className="summary-value">{criticalCount}</span>
                <span className="summary-label">Kritik Aşım</span>
              </div>
            </div>
          </div>

          {/* Kart grid */}
          <div className="section-header">
            <span className="section-title">Konut Listesi</span>
            <span className="section-count">{homes.length} kayıt</span>
          </div>

          <div className="grid-container">
            {homes.map(home => (
              <HomeCard key={home.id} home={home} onSelect={setSelectedHome} />
            ))}
          </div>

        </div>
      </div>

      {/* Modaller */}
      {selectedHome && (
        <HomeDetailModal
          home={selectedHome}
          onClose={() => setSelectedHome(null)}
        />
      )}
      {showAddHome && (
        <AddHomeModal
          onClose={() => setShowAddHome(false)}
          onSuccess={() => { setShowAddHome(false); loadData(); }}
        />
      )}
    </div>
  );
};

export default Dashboard;
