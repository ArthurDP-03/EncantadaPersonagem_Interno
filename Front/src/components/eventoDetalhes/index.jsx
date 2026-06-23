import "./index.css";
import { ArrowLeft } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import { useClientes } from "../../hooks/useClientes";
import { useEventoDetalhes } from "../../hooks/useEventoDetalhes";
import EventoForm from "./EventoForm";
import EscalacaoList from "./EscalacaoList";
import ConvitesList from "./ConvitesList";
import PersonagemItemList from "./PersonagemItemList";

function EventoDetalhes() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { clientes } = useClientes();

  const {
    evento,
    form,
    setForm,
    personagensEvento,
    personagemItensDisponiveis,
    escalacoes,
    convites,
    editando,
    carregando,
    salvando,
    adicionandoPersonagemItemId,
    removendoEventoPersonagemId,
    erro,
    iniciarEdicao,
    cancelarEdicao,
    salvarEvento,
    adicionarPersonagemItem,
    removerPersonagemItem,
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
              disabled={!editando}
              editando={editando}
              salvando={salvando}
              onEditar={iniciarEdicao}
              onCancelar={cancelarEdicao}
              onSalvar={salvarEvento}
            />
          </section>

          <aside className="evento-detalhes-right">
            <PersonagemItemList
              personagensEvento={personagensEvento}
              personagemItensDisponiveis={personagemItensDisponiveis}
              adicionandoPersonagemItemId={adicionandoPersonagemItemId}
              removendoEventoPersonagemId={removendoEventoPersonagemId}
              onAdicionar={adicionarPersonagemItem}
              onRemover={removerPersonagemItem}
            />
            <ConvitesList convites={convites} />
            <EscalacaoList
              escalacoes={escalacoes}
              personagensEvento={personagensEvento}
            />
          </aside>
        </div>
      </div>
    </main>
  );
}

export default EventoDetalhes;
