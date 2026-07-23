import React, { useState } from 'react';
import { X, Plus, Trash2, Zap, Home } from 'lucide-react';
import { addHome } from '../../services/api';
import './AddHomeModal.css';

const EMPTY_APPLIANCE = { name: '', wattLimit: '' };

const AddHomeModal = ({ onClose, onSuccess }) => {
  const [form, setForm] = useState({
    name: '',
    address: '',
    contactEmail: '',
    budgetQuotaTry: '',
    powerQuotaWatt: '',
  });
  const [appliances, setAppliances] = useState([{ ...EMPTY_APPLIANCE }]);
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  // ── Form alanı güncelle ──────────────────────────────────────
  const setField = (key, val) => {
    setForm(prev => ({ ...prev, [key]: val }));
    if (errors[key]) setErrors(prev => ({ ...prev, [key]: null }));
  };

  // ── Cihaz satırı güncelle ────────────────────────────────────
  const setAppliance = (idx, key, val) => {
    setAppliances(prev => prev.map((a, i) => i === idx ? { ...a, [key]: val } : a));
    const errKey = `appliance_${idx}_${key}`;
    if (errors[errKey]) setErrors(prev => ({ ...prev, [errKey]: null }));
  };

  const addAppliance = () => setAppliances(prev => [...prev, { ...EMPTY_APPLIANCE }]);

  const removeAppliance = (idx) => {
    if (appliances.length === 1) return; // en az 1 cihaz zorunlu
    setAppliances(prev => prev.filter((_, i) => i !== idx));
  };

  // ── Validasyon ───────────────────────────────────────────────
  const validate = () => {
    const e = {};
    if (!form.name.trim())         e.name         = 'Ev adı zorunlu';
    if (!form.contactEmail.trim()) e.contactEmail = 'E-posta zorunlu';
    else if (!/\S+@\S+\.\S+/.test(form.contactEmail)) e.contactEmail = 'Geçerli bir e-posta girin';
    if (!form.budgetQuotaTry || Number(form.budgetQuotaTry) <= 0) e.budgetQuotaTry = 'Geçerli bir bütçe girin';
    if (!form.powerQuotaWatt || Number(form.powerQuotaWatt) <= 0) e.powerQuotaWatt = 'Geçerli bir güç kotası girin';

    appliances.forEach((a, i) => {
      if (!a.name.trim())                          e[`appliance_${i}_name`]      = 'Cihaz adı zorunlu';
      if (!a.wattLimit || Number(a.wattLimit) <= 0) e[`appliance_${i}_wattLimit`] = 'Watt limiti girin';
    });

    return e;
  };

  // ── Gönder ───────────────────────────────────────────────────
  const handleSubmit = async () => {
    const e = validate();
    if (Object.keys(e).length > 0) { setErrors(e); return; }

    setSubmitting(true);
    try {
      await addHome({
        ...form,
        budgetQuotaTry: Number(form.budgetQuotaTry),
        powerQuotaWatt: Number(form.powerQuotaWatt),
        appliances: appliances.map(a => ({
          name: a.name.trim(),
          wattLimit: Number(a.wattLimit),
        })),
      });
      onSuccess();
      onClose();
    } catch (err) {
      setErrors({ submit: 'Kayıt sırasında bir hata oluştu. Lütfen tekrar deneyin.' });
    } finally {
      setSubmitting(false);
    }
  };

  // ESC ile kapat
  React.useEffect(() => {
    const onKey = (e) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  return (
    <div className="modal-backdrop" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="addhome-panel">

        {/* ── Başlık ── */}
        <div className="addhome-header">
          <div className="addhome-title-wrap">
            <Home size={18} className="addhome-title-icon" />
            <h2 className="addhome-title">Yeni Konut Ekle</h2>
          </div>
          <button className="close-btn" onClick={onClose} aria-label="Kapat">
            <X size={20} />
          </button>
        </div>

        {/* ── Konut bilgileri ── */}
        <section className="addhome-section">
          <h3 className="addhome-section-title">Konut Bilgileri</h3>

          <div className="field-row">
            <div className="field-group">
              <label className="field-label">Konut Adı <span className="required">*</span></label>
              <input
                className={`field-input ${errors.name ? 'field-input--error' : ''}`}
                placeholder="ör. Daire 12 - Yılmaz Ailesi"
                value={form.name}
                onChange={e => setField('name', e.target.value)}
              />
              {errors.name && <span className="field-error">{errors.name}</span>}
            </div>
            <div className="field-group">
              <label className="field-label">Adres</label>
              <input
                className="field-input"
                placeholder="ör. Göztepe, İzmir"
                value={form.address}
                onChange={e => setField('address', e.target.value)}
              />
            </div>
          </div>

          <div className="field-row">
            <div className="field-group">
              <label className="field-label">İletişim E-postası <span className="required">*</span></label>
              <input
                className={`field-input ${errors.contactEmail ? 'field-input--error' : ''}`}
                placeholder="ornek@email.com"
                type="email"
                value={form.contactEmail}
                onChange={e => setField('contactEmail', e.target.value)}
              />
              {errors.contactEmail && <span className="field-error">{errors.contactEmail}</span>}
            </div>
          </div>

          <div className="field-row">
            <div className="field-group">
              <label className="field-label">Bütçe Kotası (₺) <span className="required">*</span></label>
              <input
                className={`field-input ${errors.budgetQuotaTry ? 'field-input--error' : ''}`}
                placeholder="ör. 1500"
                type="number"
                min="1"
                value={form.budgetQuotaTry}
                onChange={e => setField('budgetQuotaTry', e.target.value)}
              />
              {errors.budgetQuotaTry && <span className="field-error">{errors.budgetQuotaTry}</span>}
            </div>
            <div className="field-group">
              <label className="field-label">Güç Kotası (Watt) <span className="required">*</span></label>
              <input
                className={`field-input ${errors.powerQuotaWatt ? 'field-input--error' : ''}`}
                placeholder="ör. 5000"
                type="number"
                min="1"
                value={form.powerQuotaWatt}
                onChange={e => setField('powerQuotaWatt', e.target.value)}
              />
              {errors.powerQuotaWatt && <span className="field-error">{errors.powerQuotaWatt}</span>}
            </div>
          </div>
        </section>

        {/* ── Cihazlar ── */}
        <section className="addhome-section">
          <div className="appliance-section-header">
            <h3 className="addhome-section-title">
              <Zap size={14} /> Cihazlar
            </h3>
            <button className="add-appliance-btn" onClick={addAppliance}>
              <Plus size={14} /> Cihaz Ekle
            </button>
          </div>

          <div className="appliance-form-list">
            {appliances.map((a, i) => (
              <div key={i} className="appliance-form-row">
                <div className="appliance-form-index">{i + 1}</div>

                <div className="field-group">
                  <input
                    className={`field-input ${errors[`appliance_${i}_name`] ? 'field-input--error' : ''}`}
                    placeholder="Cihaz adı (ör. Buzdolabı)"
                    value={a.name}
                    onChange={e => setAppliance(i, 'name', e.target.value)}
                  />
                  {errors[`appliance_${i}_name`] && (
                    <span className="field-error">{errors[`appliance_${i}_name`]}</span>
                  )}
                </div>

                <div className="field-group appliance-watt-group">
                  <input
                    className={`field-input ${errors[`appliance_${i}_wattLimit`] ? 'field-input--error' : ''}`}
                    placeholder="Watt limiti"
                    type="number"
                    min="1"
                    value={a.wattLimit}
                    onChange={e => setAppliance(i, 'wattLimit', e.target.value)}
                  />
                  {errors[`appliance_${i}_wattLimit`] && (
                    <span className="field-error">{errors[`appliance_${i}_wattLimit`]}</span>
                  )}
                </div>

                <button
                  className="remove-appliance-btn"
                  onClick={() => removeAppliance(i)}
                  disabled={appliances.length === 1}
                  aria-label="Cihazı kaldır"
                >
                  <Trash2 size={15} />
                </button>
              </div>
            ))}
          </div>
        </section>

        {/* ── Hata & Butonlar ── */}
        {errors.submit && (
          <div className="submit-error">{errors.submit}</div>
        )}

        <div className="addhome-footer">
          <button className="btn-cancel" onClick={onClose}>İptal</button>
          <button
            className="btn-submit"
            onClick={handleSubmit}
            disabled={submitting}
          >
            {submitting ? 'Kaydediliyor...' : 'Konutu Kaydet'}
          </button>
        </div>

      </div>
    </div>
  );
};

export default AddHomeModal;
