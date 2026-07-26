import React, { useState } from 'react';
import { X, Zap } from 'lucide-react';
import { addAppliance } from '../../services/api';
import './AddApplianceModal.css';

const AddApplianceModal = ({ homeId, onClose, onSuccess }) => {
  const [form, setForm] = useState({ name: '', wattLimit: '' });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const setField = (key, val) => {
    setForm(prev => ({ ...prev, [key]: val }));
    if (errors[key]) setErrors(prev => ({ ...prev, [key]: null }));
  };

  const validate = () => {
    const e = {};
    if (!form.name.trim()) e.name = 'Cihaz adı zorunlu';
    if (!form.wattLimit || Number(form.wattLimit) <= 0) e.wattLimit = 'Geçerli bir watt limiti girin';
    return e;
  };

  const handleSubmit = async () => {
  const e = validate();
  if (Object.keys(e).length > 0) { setErrors(e); return; }

  setSubmitting(true);
  try {
    await addAppliance(homeId, {
  name: form.name.trim(),
  wattLimit: Number(form.wattLimit),
});
alert('Cihaz başarıyla eklendi!');
onSuccess();
onClose();
  } catch {
    setErrors({ submit: 'Kayıt sırasında hata oluştu.' });
  } finally {
    setSubmitting(false);
  }
};

  React.useEffect(() => {
    const onKey = (e) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  return (
    <div className="appl-backdrop" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="appl-panel">
        <div className="appl-header">
          <div className="appl-title-wrap">
            <Zap size={16} className="appl-title-icon" />
            <h3 className="appl-title">Yeni Cihaz Ekle</h3>
          </div>
          <button className="appl-close" onClick={onClose}><X size={18} /></button>
        </div>

        <div className="appl-body">
          <div className="appl-field">
            <label className="appl-label">Cihaz Adı <span className="required">*</span></label>
            <input
              className={`appl-input ${errors.name ? 'appl-input--error' : ''}`}
              placeholder="ör. Buzdolabı, Klima, Fırın..."
              value={form.name}
              onChange={e => setField('name', e.target.value)}
            />
            {errors.name && <span className="appl-error">{errors.name}</span>}
          </div>

          <div className="appl-field">
            <label className="appl-label">Güvenli Watt Limiti <span className="required">*</span></label>
            <input
              className={`appl-input ${errors.wattLimit ? 'appl-input--error' : ''}`}
              placeholder="ör. 150, 2000, 2500"
              type="number"
              min="1"
              value={form.wattLimit}
              onChange={e => setField('wattLimit', e.target.value)}
            />
            {errors.wattLimit && <span className="appl-error">{errors.wattLimit}</span>}
          </div>
        </div>

        {errors.submit && <div className="appl-submit-error">{errors.submit}</div>}

        <div className="appl-footer">
          <button className="appl-btn-cancel" onClick={onClose}>İptal</button>
          <button className="appl-btn-submit" onClick={handleSubmit} disabled={submitting}>
            {submitting ? 'Kaydediliyor...' : 'Cihazı Ekle'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AddApplianceModal;
