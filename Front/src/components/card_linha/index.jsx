import { Pencil, Trash2 } from "lucide-react";
import Swal from "sweetalert2";
import "./index.css"

function Card_linha({ titulo, informacoes, onDeletar, onEditar }) {

  const handleDeletar = async () => {
    const confirmar = await Swal.fire({
      title: `Deletar ${titulo}?`,
      text: "Esta ação não pode ser desfeita!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sim, deletar",
      cancelButtonText: "Cancelar",
      confirmButtonColor: "#2d6a4f",
      cancelButtonColor: "#aaa",
    });

    if (confirmar.isConfirmed) {
      try {
        await onDeletar();
        Swal.fire({
          title: "Deletado!",
          text: `${titulo} foi removido com sucesso.`,
          icon: "success",
          timer: 2000,
          showConfirmButton: false,
        });
      } catch (err) {
        Swal.fire({
          title: "Erro!",
          text: err.message || "Não foi possível deletar.",
          icon: "error",
        });
      }
    }
  };

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
        <button className="btn-icone btn-deletar" type="button" onClick={handleDeletar}>
          <Trash2 size={16} />
        </button>
      </div>
    </div>
  );
}

export default Card_linha;