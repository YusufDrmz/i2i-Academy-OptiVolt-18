import React, { useEffect, useState } from 'react';
import { fetchHomes } from '../../services/api';
import HomeCard from '../HomeCard/HomeCard';
import HomeDetailModal from '../HomeDetailModal/HomeDetailModal';
import AddHomeModal from '../AddHomeModal/AddHomeModal';
import PlaceholderPage from '../PlaceholderPage/PlaceholderPage';
import {
  Zap, ShieldAlert, Plus, LayoutDashboard,
  Activity, Bell, Settings, Home, TrendingUp
} from 'lucide-react';
import './Dashboard.css';

const NAV_ITEMS = [
  { id: 'dashboard',      icon: LayoutDashboard, label: 'Dashboard'   },
  { id: 'homes',          icon: Home,            label: 'Konutlar'    },
  { id: 'telemetry',      icon: Activity,        label: 'Telemetri'   },
  { id: 'reports',        icon: TrendingUp,      label: 'Raporlar'    },
  { id: 'notifications',  icon: Bell,            label: 'Bildirimler' },
  { id: 'settings',       icon: Settings,        label: 'Ayarlar'     },
];

const PLACEHOLDER_CONFIG = {
  homes: {
    title: 'Konutlar',
    description: 'Tüm kayıtlı konutları listeleyen ve yönetim işlemlerini sunan sayfa backend entegrasyonuyla aktif olacaktır.',
    icon: Home,
  },
  telemetry: {
    title: 'Telemetri',
    description: 'Gerçek zamanlı IoT sensör verilerini ve cihaz bazlı anlık watt akışlarını izleyebileceğiniz sayfa.',
    icon: Activity,
  },
  reports: {
    title: 'Raporlar',
    description: 'Aylık ve haftalık enerji tüketim raporlarını ve maliyet analizlerini görüntüleyebileceğiniz sayfa.',
    icon: TrendingUp,
  },
  notifications: {
    title: 'Bildirimler',
    description: 'Tüm konutlara ait AI tarafından üretilen uyarı ve bildirimleri merkezi olarak görebileceğiniz sayfa.',
    icon: Bell,
  },
  settings: {
    title: 'Ayarlar',
    description: 'Sistem konfigürasyonu, tarife yönetimi ve kullanıcı tercihlerini düzenleyebileceğiniz sayfa.',
    icon: Settings,
  },
};

const Dashboard = () => {
  const [homes, setHomes]               = useState([]);
  const [loading, setLoading]           = useState(true);
  const [selectedHome, setSelectedHome] = useState(null);
  const [showAddHome, setShowAddHome]   = useState(false);
  const [activePage, setActivePage]     = useState('dashboard');

  const loadData = async () => {
    const data = await fetchHomes();
    setHomes(data);
    setLoading(false);
  };

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 2000);
    return () => clearInterval(interval);
  }, []);

  const criticalCount = homes.filter(h => h.status === 'CRITICAL').length;
  const warningCount  = homes.filter(h => h.status === 'WARNING').length;
  const totalWatt     = homes.reduce((s, h) => s + h.currentWatt, 0);

  if (loading) {
    return <div className="loading-state">OptiVolt yükleniyor…</div>;
  }

  const renderContent = () => {
    if (activePage !== 'dashboard') {
      const cfg = PLACEHOLDER_CONFIG[activePage];
      return <PlaceholderPage title={cfg.title} description={cfg.description} icon={cfg.icon} />;
    }

    return (
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
    );
  };

  return (
    <div className="app-shell">

      {/* ── Sidebar ── */}
      <aside className="sidebar">
        <div className="sidebar-logo">
          <div className="sidebar-logo-icon"><Zap size={17} /></div>
          <div>
            <div className="sidebar-logo-text">OptiVolt</div>
            <div className="sidebar-logo-sub">Izgara İzleme</div>
          </div>
        </div>

        <span className="nav-section-label">Genel</span>
        {NAV_ITEMS.map(({ id, icon: Icon, label }) => (
          <button
            key={id}
            className={`nav-item ${activePage === id ? 'active' : ''}`}
            onClick={() => setActivePage(id)}
          >
            <Icon size={16} />
            {label}
          </button>
        ))}

        <div className="sidebar-bottom">
          <div className="sidebar-user">
            <div className="user-avatar">OV</div>
            <div>
              <div className="user-name">Operatör</div>
              <div className="user-role">i2i Systems</div>
            </div>
          </div>
        </div>
      </aside>

      {/* ── Ana içerik ── */}
      <div className="main-content">
        <header className="topbar">
          <div className="topbar-left">
            <h1>{NAV_ITEMS.find(n => n.id === activePage)?.label ?? 'Dashboard'}</h1>
            <p>Gerçek zamanlı enerji tüketimi ve kota yönetimi</p>
          </div>
          {activePage === 'dashboard' && (
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
          )}
        </header>

        {renderContent()}
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
