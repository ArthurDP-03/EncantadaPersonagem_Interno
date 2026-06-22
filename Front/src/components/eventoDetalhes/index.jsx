import "./index.css";
import { ArrowLeft } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import { useAdministradores } from "../../hooks/useAdministradores";
import { useClientes } from "../../hooks/useClientes";
import { useEventoDetalhes } from "../../hooks/useEventoDetalhes";
import EventoForm from "./EventoForm";
import EscalacaoList from "./EscalacaoList";
import ConvitesList from "./ConvitesList";

function EventoDetalhes() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { clientes } = useClientes();
  const { administradores } = useAdministradores();

  const {
    evento,
    form,
    setForm,
    personagensEvento,
    escalacoes,
    convites,
    editando,
    carregando,
    salvando,
    erro,
    iniciarEdicao,
    cancelarEdicao,
    salvarEvento,
  } = useEventoDetalhes(Number(id));

  if (carregando) {
    return (
      <main className="evento-detalhes-page">
        <p className="evento-detalhes-loading">Carregando detalhes do evento...</p>
      </main>
    );
  }

  if (erro || !evento) {
    return (
      <main className="evento-detalhes-page">
        <button className="evento-detalhes-back" type="button" onClick={() => navigate("/eventos")}>
          <ArrowLeft size={18} />
          Voltar para os eventos
        </button>
        <p className="evento-detalhes-error">{erro || "Evento não encontrado"}</p>
      </main>
    );
  }

  return (
    <main className="evento-detalhes-page">
      <div className="evento-detalhes-shell">
        <button className="evento-detalhes-back" type="button" onClick={() => navigate("/eventos")}>
          <ArrowLeft size={18} />
          Voltar para os eventos
        </button>

        <header className="evento-detalhes-title">
          <span>Evento #{evento.id}</span>
          <h1>{evento.titulo}</h1>
        </header>

        <div className="evento-detalhes-grid">
          <section className="evento-detalhes-left evento-detalhes-panel">
            <EventoForm
              dados={form}
              onChange={setForm}
              clientes={clientes}
              administradores={administradores}
              disabled={!editando}
              editando={editando}
              salvando={salvando}
              onEditar={iniciarEdicao}
              onCancelar={cancelarEdicao}
              onSalvar={salvarEvento}
            />
          </section>

          <aside className="evento-detalhes-right">
            <EscalacaoList
              escalacoes={escalacoes}
              personagensEvento={personagensEvento}
            />
            <ConvitesList convites={convites} />
          </aside>
        </div>
      </div>
    </main>
  );
}

export default EventoDetalhes;
