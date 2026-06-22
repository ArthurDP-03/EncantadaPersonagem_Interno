import React from 'react';
import { useNavigate } from 'react-router-dom';
import './index.css';

const CardAtalho = ({ title, description, icon: Icon, path }) => {
  const navigate = useNavigate();

  return (
    <button 
      className="shortcut-card" 
      onClick={() => navigate(path)}
      aria-label={`Ir para ${title}`}
    >
      <div className="card-icon-wrapper">
        <Icon className="card-icon" size={28} />
      </div>
      <div className="card-content">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </button>
  );
};

export default CardAtalho;