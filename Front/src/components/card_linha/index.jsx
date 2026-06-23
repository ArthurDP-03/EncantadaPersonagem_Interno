import { Pencil, Trash2, MoreHorizontal } from "lucide-react";
import Swal from "sweetalert2";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import "./index.css"

function Card_linha({ titulo, informacoes, onDeletar, onEditar }) {
  const { t } = useTranslation();
  const [menuAberto, setMenuAberto] = useState(false);

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
        <button
          className="btn-icone btn-mais"
          type="button"
          onClick={() => setMenuAberto((prev) => !prev)}
          aria-label={t('common.moreOptions') || 'Mais opções'}
        >
          <MoreHorizontal size={16} />
        </button>
        {menuAberto && (
          <div className="card-linha-menu">
            {onEditar && (
              <button
                className="card-linha-menu-item"
                type="button"
                onClick={() => {
                  setMenuAberto(false);
                  onEditar();
                }}
              >
                {t('common.edit')}
              </button>
            )}
            <button
              className="card-linha-menu-item excluir"
              type="button"
              onClick={() => {
                setMenuAberto(false);
                onDeletar();
              }}
            >
              {t('common.delete')}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default Card_linha;