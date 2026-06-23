import { Search, Plus, Trash2, X } from "lucide-react";
import { useMemo, useState } from "react";

function PersonagemItemList({
  personagensEvento,
  personagemItensDisponiveis,
  adicionandoPersonagemItemId,
  removendoEventoPersonagemId,
  onAdicionar,
  onRemover,
}) {
  const [modalAberto, setModalAberto] = useState(false);
  const [busca, setBusca] = useState("");

  const itensFiltrados = useMemo(() => {
    const termo = busca.trim().toLowerCase();

    if (!termo) return personagemItensDisponiveis;

    return personagemItensDisponiveis.filter((item) =>
      [item.nomePersonagem, item.codigo]
        .filter(Boolean)
        .some((valor) => valor.toLowerCase().includes(termo))
    );
  }, [busca, personagemItensDisponiveis]);

  const adicionarItem = async (personagemItemId) => {
    const adicionado = await onAdicionar(personagemItemId);
    if (adicionado) {
      setBusca("");
      setModalAberto(false);
    }
  };

  return (
    <>
      <section className="evento-detalhes-panel evento-detalhes-list-panel">
        <div className="evento-detalhes-panel-header">
          <div>
            <span className="evento-detalhes-kicker">Personagens</span>
            <h2>Personagens adicionados ao evento</h2>
          </div>
          <div className="evento-detalhes-header-actions">
            <strong>{personagensEvento.length}</strong>
            <button
              className="evento-detalhes-add-button"
              type="button"
              title="Adicionar personagem"
              aria-label="Adicionar personagem"
              onClick={() => setModalAberto(true)}
            >
              <Plus size={20} />
            </button>
          </div>
        </div>

        <div className="evento-detalhes-list">
          {personagensEvento.length > 0 ? (
            personagensEvento.map((personagem) => (
              <article key={personagem.id} className="evento-detalhes-list-item">
                <div>
                  <h3>{personagem.personagemNome}</h3>
                  <p>{personagem.personagemItemCodigo}</p>
                </div>
                <div className="evento-detalhes-meta">
                  <span>Item #{personagem.personagemItemId}</span>
                  <button
                    className="evento-detalhes-delete-button"
                    type="button"
                    title="Remover personagem do evento"
                    aria-label={`Remover ${personagem.personagemNome} do evento`}
                    disabled={Boolean(removendoEventoPersonagemId)}
                    onClick={() => onRemover(personagem)}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </article>
            ))
          ) : (
            <div className="evento-detalhes-empty">
              <p>Nenhum personagem adicionado a este evento.</p>
            </div>
          )}
        </div>
      </section>

      {modalAberto && (
        <div className="evento-detalhes-modal" role="dialog" aria-modal="true">
          <div className="evento-detalhes-modal-content">
            <div className="evento-detalhes-modal-header">
              <h2>Adicionar personagem</h2>
              <button
                className="evento-detalhes-modal-close"
                type="button"
                title="Fechar"
                aria-label="Fechar"
                onClick={() => setModalAberto(false)}
              >
                <X size={20} />
              </button>
            </div>

            <div className="evento-detalhes-search">
              <input
                type="text"
                value={busca}
                placeholder="Buscar personagem ou código"
                onChange={(event) => setBusca(event.target.value)}
                autoFocus
              />
              <Search size={18} />
            </div>

            <div className="evento-detalhes-modal-list">
              {itensFiltrados.length > 0 ? (
                itensFiltrados.map((item) => {
                  const adicionando = adicionandoPersonagemItemId === item.id;

                  return (
                    <article key={item.id} className="evento-detalhes-list-item">
                      <div>
                        <h3>{item.nomePersonagem}</h3>
                        <p>{item.codigo}</p>
                      </div>
                      <div className="evento-detalhes-meta">
                        <span className={`evento-detalhes-status status-${item.status.toLowerCase()}`}>
                          {item.status}
                        </span>
                        <button
                          className="evento-detalhes-icon-button"
                          type="button"
                          title="Adicionar ao evento"
                          aria-label={`Adicionar ${item.nomePersonagem} ao evento`}
                          disabled={Boolean(adicionandoPersonagemItemId)}
                          onClick={() => adicionarItem(item.id)}
                        >
                          <Plus size={16} />
                          <span>{adicionando ? "Adicionando" : "Adicionar"}</span>
                        </button>
                      </div>
                    </article>
                  );
                })
              ) : (
                <div className="evento-detalhes-empty">
                  <p>Nenhum item disponível encontrado.</p>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default PersonagemItemList;
