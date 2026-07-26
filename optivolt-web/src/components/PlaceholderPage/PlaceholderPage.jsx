import React from 'react';
import { Construction } from 'lucide-react';
import './PlaceholderPage.css';

const PlaceholderPage = ({ title, description, icon: Icon }) => {
  return (
    <div className="placeholder-page">
      <div className="placeholder-content">
        <div className="placeholder-icon-wrap">
          {Icon ? <Icon size={40} className="placeholder-icon" /> : <Construction size={40} className="placeholder-icon" />}
        </div>
        <h2 className="placeholder-title">{title}</h2>
        <p className="placeholder-desc">{description || 'Bu sayfa backend entegrasyonu tamamlandıktan sonra aktif olacaktır.'}</p>
        <div className="placeholder-badge">Yakında</div>
      </div>
    </div>
  );
};

export default PlaceholderPage;
