import React, { useEffect, useState } from 'react';
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer
} from 'recharts';
import { X, Zap, TriangleAlert, CircleCheck, TrendingUp } from 'lucide-react';
import { fetchHomeDetail } from '../../services/api';
import './HomeDetailModal.css';

// ─── Yardımcı: Watt formatı ──────────────────────────────────────────────────
const fmtWatt = (w) => w >= 1000 ? `${(w / 1000).toFixed(1)} kW` : `${w} W`;

// ─── Cihaz satırı ────────────────────────────────────────────────────────────
const ApplianceRow = ({ appliance }) => {
  const { name, currentWatt, maxSafeWatt, isAnomalous, consecutiveBreaches } = appliance;
  const pct = Math.min((currentWatt / maxSafeWatt) * 100, 100);

  let rowClass = 'appliance-row';
  if (isAnomalous) rowClass += ' appliance-row--anomalous';
  else if (pct >= 80) rowClass += ' appliance-row--warning';

  return (
    <div className={rowClass}>
      <div className="appliance-info">
        <div className="appliance-name-wrap">
          {isAnomalous
            ? <TriangleAlert size={15} className="icon-anomaly" />
            : <CircleCheck size={15} className="icon-ok" />
          }
          <span className="appliance-name">{name}</span>
          {isAnomalous && (
            <span className="breach-badge">{consecutiveBreaches} ardışık ihlal</span>
          )}
        </div>
        <div className="appliance-watts">
          <span className={isAnomalous ? 'watt-critical' : ''}>{fmtWatt(currentWatt)}</span>
          <span className="watt-limit">/ {fmtWatt(maxSafeWatt)}</span>
        </div>
      </div>
      <div className="appliance-bar-track">
        <div
          className={`appliance-bar-fill ${isAnomalous ? 'bar--critical' : pct >= 80 ? 'bar--warning' : 'bar--normal'}`}
          style={{ width: `${pct}%` }}
        />
      </div>
    </div>
  );
};

// ─── Özel Tooltip ────────────────────────────────────────────────────────────
const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="chart-tooltip">
      <p className="tooltip-label">{label}</p>
      <p className="tooltip-kwh">{payload[0]?.value} kWh</p>
      <p className="tooltip-cost">₺{payload[1]?.value}</p>
    </div>
  );
};

// ─── Ana Modal ───────────────────────────────────────────────────────────────
const HomeDetailModal = ({ home: baseHome, onClose }) => {
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);

useEffect(() => {
  let cancelled = false;

  const load = async (isFirst) => {
    if (isFirst) setLoading(true);
    const data = await fetchHomeDetail(baseHome.id);
    if (!cancelled) {
      setDetail(data);
      if (isFirst) setLoading(false);
    }
  };

  load(true);
  const interval = setInterval(function() { load(false); }, 5000);

  return () => {
    cancelled = true;
    clearInterval(interval);
  };
}, [baseHome.id]);

  // ESC ile kapat
  useEffect(() => {
    const onKey = (e) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  const home = detail?.home ?? baseHome;
  const history = detail?.history ?? [];
  const appliances = home.appliances ?? [];
  const anomalousCount = appliances.filter(a => a.isAnomalous).length;

  const budgetPct = Math.round((home.currentCostTry / home.budgetQuotaTry) * 100);
  const powerPct  = Math.round((home.currentWatt   / home.powerQuotaWatt)  * 100);

  const statusLabel = {
    NORMAL:   { text: 'Normal',        cls: 'status--normal'   },
    WARNING:  { text: 'Uyarı (%80+)',  cls: 'status--warning'  },
    CRITICAL: { text: 'Kritik (%100+)', cls: 'status--critical' },
  }[home.status] ?? { text: home.status, cls: '' };

  return (
    <div className="modal-backdrop" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal-panel">

        {/* ── Başlık ── */}
        <div className="modal-header">
          <div>
            <h2 className="modal-title">{home.name}</h2>
            <p className="modal-address">{home.address} · {home.contactEmail}</p>
          </div>
          <div className="modal-header-right">
            <span className={`status-badge ${statusLabel.cls}`}>{statusLabel.text}</span>
            <button className="close-btn" onClick={onClose} aria-label="Kapat">
              <X size={20} />
            </button>
          </div>
        </div>

        {/* ── Özet metrikler ── */}
        <div className="modal-metrics">
          <div className="metric-card">
            <span className="metric-label">Anlık Güç</span>
            <span className="metric-value">{fmtWatt(home.currentWatt)}</span>
            <div className="metric-bar-track">
              <div
                className={`metric-bar-fill ${powerPct >= 100 ? 'bar--critical' : powerPct >= 80 ? 'bar--warning' : 'bar--normal'}`}
                style={{ width: `${Math.min(powerPct, 100)}%` }}
              />
            </div>
            <span className="metric-sub">Kota: {fmtWatt(home.powerQuotaWatt)} · %{powerPct}</span>
          </div>

          <div className="metric-card">
            <span className="metric-label">Güncel Maliyet</span>
            <span className="metric-value">₺{home.currentCostTry.toLocaleString('tr-TR')}</span>
            <div className="metric-bar-track">
              <div
                className={`metric-bar-fill ${budgetPct >= 100 ? 'bar--critical' : budgetPct >= 80 ? 'bar--warning' : 'bar--normal'}`}
                style={{ width: `${Math.min(budgetPct, 100)}%` }}
              />
            </div>
            <span className="metric-sub">Bütçe: ₺{home.budgetQuotaTry.toLocaleString('tr-TR')} · %{budgetPct}</span>
          </div>

          <div className="metric-card metric-card--stat">
            <Zap size={22} className="stat-icon stat-icon--blue" />
            <span className="stat-number">{appliances.length}</span>
            <span className="metric-label">Toplam Cihaz</span>
          </div>

          <div className="metric-card metric-card--stat">
            <TriangleAlert size={22} className={`stat-icon ${anomalousCount > 0 ? 'stat-icon--red' : 'stat-icon--green'}`} />
            <span className="stat-number">{anomalousCount}</span>
            <span className="metric-label">Anomalili Cihaz</span>
          </div>
        </div>

        {/* ── Cihaz listesi ── */}
        <section className="modal-section">
          <h3 className="section-title">
            <Zap size={16} /> Cihaz Durumları
          </h3>

          {loading ? (
            <div className="skeleton-list">
              {[1, 2, 3].map(i => <div key={i} className="skeleton-row" />)}
            </div>
          ) : appliances.length === 0 ? (
            <p className="empty-state">Bu konuta kayıtlı cihaz bulunamadı.</p>
          ) : (
            <div className="appliance-list">
              {appliances.map(a => <ApplianceRow key={a.id} appliance={a} />)}
            </div>
          )}
        </section>

        {/* ── Tüketim grafiği ── */}
        <section className="modal-section">
          <h3 className="section-title">
            <TrendingUp size={16} /> Son 7 Günlük Tüketim Trendi
          </h3>

          {loading ? (
            <div className="skeleton-chart" />
          ) : history.length === 0 ? (
            <p className="empty-state">Henüz tarihsel veri yok.</p>
          ) : (
            <ResponsiveContainer width="100%" height={220}>
              <AreaChart data={history} margin={{ top: 5, right: 10, left: -10, bottom: 0 }}>
                <defs>
                  <linearGradient id="gradKwh" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%"  stopColor="#3b82f6" stopOpacity={0.25} />
                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0} />
                  </linearGradient>
                  <linearGradient id="gradCost" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%"  stopColor="#f59e0b" stopOpacity={0.25} />
                    <stop offset="95%" stopColor="#f59e0b" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                <XAxis dataKey="date" tick={{ fill: '#94a3b8', fontSize: 12 }} />
                <YAxis tick={{ fill: '#94a3b8', fontSize: 12 }} />
                <Tooltip content={<CustomTooltip />} />
                <Area
                  type="monotone"
                  dataKey="totalKwh"
                  stroke="#3b82f6"
                  strokeWidth={2}
                  fill="url(#gradKwh)"
                  name="kWh"
                />
                <Area
                  type="monotone"
                  dataKey="totalCost"
                  stroke="#f59e0b"
                  strokeWidth={2}
                  fill="url(#gradCost)"
                  name="₺"
                />
              </AreaChart>
            </ResponsiveContainer>
          )}

          {!loading && history.length > 0 && (
            <div className="chart-legend">
              <span className="legend-item legend-item--blue">■ kWh tüketim</span>
              <span className="legend-item legend-item--amber">■ ₺ maliyet</span>
            </div>
          )}
        </section>

      </div>
    </div>
  );
};

export default HomeDetailModal;
