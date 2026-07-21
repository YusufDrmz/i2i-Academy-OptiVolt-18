import React from 'react';
import { Zap, AlertTriangle, CheckCircle, Flame, Mail } from 'lucide-react';
import './HomeCard.css';

const HomeCard = ({ home, onSelect }) => {
  const usagePercentage = Math.round((home.currentWatt / home.powerQuotaWatt) * 100);

  const getStatusBadge = () => {
    if (usagePercentage >= 100 || home.status === 'CRITICAL') {
      return (
        <span className="badge badge-critical">
          <Flame size={14} /> Kritik Aşım (%{usagePercentage})
        </span>
      );
    }
    if (usagePercentage >= 80 || home.status === 'WARNING') {
      return (
        <span className="badge badge-warning">
          <AlertTriangle size={14} /> Kota Uyarısı (%{usagePercentage})
        </span>
      );
    }
    return (
      <span className="badge badge-normal">
        <CheckCircle size={14} /> Normal (%{usagePercentage})
      </span>
    );
  };

  return (
    <div className={`home-card ${home.status.toLowerCase()}`} onClick={() => onSelect(home)}>
      <div className="card-header">
        <div>
          <h3>{home.name}</h3>
          <p className="address">{home.address}</p>
        </div>
        {getStatusBadge()}
      </div>

      <div className="card-body">
        <div className="metric">
          <span className="label"><Zap size={16} /> Anlık Güç:</span>
          <span className="value">{home.currentWatt} W / {home.powerQuotaWatt} W</span>
        </div>

        {/* Kota İlerleme Çubuğu */}
        <div className="progress-bar-bg">
          <div 
            className={`progress-bar-fill ${usagePercentage >= 100 ? 'bg-danger' : usagePercentage >= 80 ? 'bg-warning' : 'bg-success'}`} 
            style={{ width: `${Math.min(usagePercentage, 100)}%` }}
          />
        </div>

        <div className="metric mt-2">
          <span className="label">Tahmini Maliyet:</span>
          <span className="value">{home.currentCostTry} ₺ / {home.budgetQuotaTry} ₺</span>
        </div>
      </div>

      <div className="card-footer">
        <span>{home.appliancesCount} Cihaz Bağlı</span>
        <span className="email"><Mail size={12} /> {home.contactEmail}</span>
      </div>
    </div>
  );
};

export default HomeCard;