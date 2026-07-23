import React from 'react';
import { Zap, AlertTriangle, CheckCircle, Flame, Mail } from 'lucide-react';
import './HomeCard.css';

const HomeCard = ({ home, onSelect }) => {
  const powerPct  = Math.round((home.currentWatt    / home.powerQuotaWatt)  * 100);
  const budgetPct = Math.round((home.currentCostTry / home.budgetQuotaTry)  * 100);

  const badge = () => {
    if (home.status === 'CRITICAL' || powerPct >= 100)
      return <span className="badge badge-critical"><Flame size={11} /> Kritik %{powerPct}</span>;
    if (home.status === 'WARNING' || powerPct >= 80)
      return <span className="badge badge-warning"><AlertTriangle size={11} /> Uyarı %{powerPct}</span>;
    return <span className="badge badge-normal"><CheckCircle size={11} /> Normal %{powerPct}</span>;
  };

  const barClass = (pct) =>
    pct >= 100 ? 'bg-danger' : pct >= 80 ? 'bg-warning' : 'bg-success';

  return (
    <div className={`home-card ${home.status.toLowerCase()}`} onClick={() => onSelect(home)}>

      <div className="card-header">
        <div>
          <h3>{home.name}</h3>
          <p className="address">{home.address}</p>
        </div>
        {badge()}
      </div>

      <div className="card-metrics">
        {/* Güç */}
        <div className="metric-row">
          <div className="metric-label-row">
            <span className="metric-label"><Zap size={13} /> Anlık Güç</span>
            <span className="metric-value">{home.currentWatt} W / {home.powerQuotaWatt} W</span>
          </div>
          <div className="progress-bar-bg">
            <div
              className={`progress-bar-fill ${barClass(powerPct)}`}
              style={{ width: `${Math.min(powerPct, 100)}%` }}
            />
          </div>
        </div>

        {/* Bütçe */}
        <div className="metric-row">
          <div className="metric-label-row">
            <span className="metric-label">💰 Maliyet</span>
            <span className="metric-value">₺{home.currentCostTry} / ₺{home.budgetQuotaTry}</span>
          </div>
          <div className="progress-bar-bg">
            <div
              className={`progress-bar-fill ${barClass(budgetPct)}`}
              style={{ width: `${Math.min(budgetPct, 100)}%` }}
            />
          </div>
        </div>
      </div>

      <div className="card-footer">
        <span className="card-footer-left">
          <Zap size={12} color="#6366f1" />
          {home.appliancesCount} cihaz
        </span>
        <span className="email">
          <Mail size={11} /> {home.contactEmail}
        </span>
      </div>

    </div>
  );
};

export default HomeCard;
