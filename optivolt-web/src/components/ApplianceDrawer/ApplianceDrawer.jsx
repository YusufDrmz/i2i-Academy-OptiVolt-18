import React, { useEffect, useState } from 'react';
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer
} from 'recharts';
import { X, Zap, TriangleAlert, CircleCheck, Activity } from 'lucide-react';
import { fetchApplianceDetail } from '../../services/api';
import './ApplianceDrawer.css';

const fmtWatt = (w) => w >= 1000 ? `${(w / 1000).toFixed(1)} kW` : `${w} W`;

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="drawer-tooltip">
      <p className="drawer-tooltip-label">{label}</p>
      <p className="drawer-tooltip-watt">{payload[0]?.value} W</p>
    </div>
  );
};

const ApplianceDrawer = ({ appliance, onClose }) => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      const data = await fetchApplianceDetail(appliance.id);
      if (!cancelled) {
        setHistory(data.history || []);
        setLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, [appliance.id]);

  useEffect(() => {
    const onKey = (e) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  const pct = Math.min(Math.round((appliance.currentWatt / appliance.maxSafeWatt) * 100), 100);
  const barClass = appliance.isAnomalous ? 'bar--critical' : pct >= 80 ? 'bar--warning' : 'bar--normal';

  return (
    <>
      {/* Backdrop — sadece drawer'ı kapatır, modal açık kalır */}
      <div className="drawer-backdrop" onClick={onClose} />

      {/* Drawer panel */}
      <div className="drawer-panel">

        {/* Başlık */}
        <div className="drawer-header">
          <div className="drawer-title-wrap">
            {appliance.isAnomalous
              ? <TriangleAlert size={18} className="drawer-icon-anomaly" />
              : <CircleCheck size={18} className="drawer-icon-ok" />
            }
            <h3 className="drawer-title">{appliance.name}</h3>
          </div>
          <button className="drawer-close" onClick={onClose} aria-label="Kapat">
            <X size={18} />
          </button>
        </div>

        {/* Durum badge */}
        <div className="drawer-status-row">
          {appliance.isAnomalous ? (
            <span className="drawer-badge drawer-badge--critical">
              ⚠ Anomali — {appliance.consecutiveBreaches} ardışık ihlal
            </span>
          ) : pct >= 80 ? (
            <span className="drawer-badge drawer-badge--warning">Uyarı Seviyesi</span>
          ) : (
            <span className="drawer-badge drawer-badge--normal">Normal Çalışma</span>
          )}
        </div>

        {/* Metrikler */}
        <div className="drawer-metrics">
          <div className="drawer-metric-card">
            <span className="drawer-metric-label">Anlık Güç</span>
            <span className="drawer-metric-value">{fmtWatt(appliance.currentWatt)}</span>
          </div>
          <div className="drawer-metric-card">
            <span className="drawer-metric-label">Güvenli Limit</span>
            <span className="drawer-metric-value">{fmtWatt(appliance.maxSafeWatt)}</span>
          </div>
          <div className="drawer-metric-card">
            <span className="drawer-metric-label">Kullanım</span>
            <span className={`drawer-metric-value ${appliance.isAnomalous ? 'value--critical' : pct >= 80 ? 'value--warning' : 'value--normal'}`}>
              %{pct}
            </span>
          </div>
          <div className="drawer-metric-card">
            <span className="drawer-metric-label">İhlal Sayacı</span>
            <span className={`drawer-metric-value ${appliance.consecutiveBreaches >= 3 ? 'value--critical' : ''}`}>
              {appliance.consecutiveBreaches} / 3
            </span>
          </div>
        </div>

        {/* Progress bar */}
        <div className="drawer-bar-wrap">
          <div className="drawer-bar-track">
            <div className={`drawer-bar-fill ${barClass}`} style={{ width: `${pct}%` }} />
          </div>
          <span className="drawer-bar-label">%{pct} kapasite kullanımı</span>
        </div>

        {/* Grafik */}
        <div className="drawer-section">
          <h4 className="drawer-section-title">
            <Activity size={14} /> Son 7 Günlük Watt Trendi
          </h4>

          {loading ? (
            <div className="drawer-skeleton" />
          ) : history.length === 0 ? (
            <p className="drawer-empty">Henüz veri yok.</p>
          ) : (
            <ResponsiveContainer width="100%" height={180}>
              <AreaChart data={history} margin={{ top: 5, right: 8, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="gradAppliance" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%"  stopColor="#6366f1" stopOpacity={0.3} />
                    <stop offset="95%" stopColor="#6366f1" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="date" tick={{ fill: '#64748b', fontSize: 11 }} />
                <YAxis tick={{ fill: '#64748b', fontSize: 11 }} />
                <Tooltip content={<CustomTooltip />} />
                <Area
                  type="monotone"
                  dataKey="avgWatt"
                  stroke="#6366f1"
                  strokeWidth={2}
                  fill="url(#gradAppliance)"
                />
              </AreaChart>
            </ResponsiveContainer>
          )}
        </div>

      </div>
    </>
  );
};

export default ApplianceDrawer;
