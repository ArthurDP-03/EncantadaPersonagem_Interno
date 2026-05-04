import {Pencil, Trash2 } from "lucide-react";
import './index.css'

function Card_linha({ titulo, informacoes }) {
  return (
    <div className="card-linha">
      <div className="textos">
        <div className="texto t1">{titulo}</div>
        <div className="informacao">
          {Object.entries(informacoes).map(([label, valor]) => (
            <div key={label} className="texto t2">
              <p>{label}: {valor}</p>
            </div>
          ))}
        </div>
      </div>
      <div className="acoes">
        <button className="btn-icone btn-editar"><Pencil size={16} /></button>
        <button className="btn-icone btn-deletar"><Trash2 size={16} /></button>
      </div>
    </div>
  );
}


export default Card_linha;