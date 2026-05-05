import { Pencil, Trash2 } from "lucide-react";
import Swal from "sweetalert2";
import "./index.css"

function Card_linha({ titulo, informacoes, onDeletar, onEditar }) {
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
        {onEditar && (
          <button className="btn-icone btn-editar" type="button" onClick={onEditar}>
            <Pencil size={16} />
          </button>
        )}
        <button className="btn-icone btn-deletar" type="button" onClick={onDeletar}>
          <Trash2 size={16} />
        </button>
      </div>
    </div>
  );
}

export default Card_linha;