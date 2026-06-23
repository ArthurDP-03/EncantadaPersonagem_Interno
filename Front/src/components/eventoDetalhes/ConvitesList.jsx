import { CheckCircle2, Plus, RotateCcw, Search, Trash2, UserPlus, X } from "lucide-react";
import { useMemo, useState } from "react";

const isEscalacaoAtiva = (escala) => escala.status !== "CANCELADA";
const isConviteAtivo = (convite) => ["PENDENTE", "ACEITO"].includes(convite.status);
const filtrosConvite = ["TODOS", "PENDENTE", "ACEITO", "RECUSADO", "EXPIRADO", "CANCELADO"];

function ConvitesList({
  personagensEvento,
  convites,
  escalacoes,
  atores,
  processandoConviteId,
  enviandoConvitesEventoPersonagemId,
  escalandoConviteId,
  onAdicionarConvites,
  onExcluirConvite,
  onAdicionarEscalacao,
  onReativarConvite,
}) {
  const [personagemSelecionado, setPersonagemSelecionado] = useState(null);
  const [atoresSelecionados, setAtoresSelecionados] = useState([]);
  const [buscaAtor, setBuscaAtor] = useState("");
  const [filtroStatus, setFiltroStatus] = useState("TODOS");

  const convitesPorPersonagem = useMemo(() => {
    return convites.reduce((acc, convite) => {
      acc[convite.eventoPersonagemId] = acc[convite.eventoPersonagemId] || [];
      acc[convite.eventoPersonagemId].push(convite);
      return acc;
    }, {});
  }, [convites]);

  const escalacoesPorPersonagem = useMemo(() => {
    return escalacoes.reduce((acc, escala) => {
      if (isEscalacaoAtiva(escala)) {
        acc[escala.eventoPersonagemId] = escala;
      }
      return acc;
    }, {});
  }, [escalacoes]);

  const personagemModal = personagemSelecionado
    ? {
        ...personagemSelecionado,
        convites: convitesPorPersonagem[personagemSelecionado.id] || [],
        escalacao: escalacoesPorPersonagem[personagemSelecionado.id] || null,
      }
    : null;

  const idsAtoresIndisponiveis = useMemo(() => {
    if (!personagemModal) return new Set();

    const ids = new Set();

    convites.forEach((convite) => {
      if (convite.eventoPersonagemId === personagemModal.id || isConviteAtivo(convite)) {
        ids.add(convite.atorId);
      }
    });

    escalacoes.filter(isEscalacaoAtiva).forEach((escala) => {
      ids.add(escala.atorId);
    });

    return ids;
  }, [convites, escalacoes, personagemModal]);

  const atoresDisponiveis = useMemo(() => {
    if (!personagemModal) return [];

    const termo = buscaAtor.trim().toLowerCase();
    return atores
      .filter((ator) => ator.ativo !== false && !idsAtoresIndisponiveis.has(ator.id))
      .filter((ator) => {
        if (!termo) return true;
        return [ator.nome, ator.email]
          .filter(Boolean)
          .some((valor) => valor.toLowerCase().includes(termo));
      });
  }, [atores, buscaAtor, idsAtoresIndisponiveis, personagemModal]);

  const convitesFiltrados = useMemo(() => {
    if (!personagemModal) return [];
    if (filtroStatus === "TODOS") return personagemModal.convites;
    return personagemModal.convites.filter((convite) => convite.status === filtroStatus);
  }, [filtroStatus, personagemModal]);

  const totalConvites = convites.length;

  const abrirModal = (personagem) => {
    setPersonagemSelecionado(personagem);
    setAtoresSelecionados([]);
    setBuscaAtor("");
    setFiltroStatus("TODOS");
  };

  const fecharModal = () => {
    setPersonagemSelecionado(null);
    setAtoresSelecionados([]);
    setBuscaAtor("");
    setFiltroStatus("TODOS");
  };

  const alternarAtor = (atorId) => {
    setAtoresSelecionados((ids) =>
      ids.includes(atorId) ? ids.filter((id) => id !== atorId) : [...ids, atorId]
    );
  };

  const adicionarConvites = async () => {
    const enviados = await onAdicionarConvites(personagemModal, atoresSelecionados);
    if (enviados) {
      setAtoresSelecionados([]);
      setBuscaAtor("");
    }
  };

  return (
    <>
      <section className="evento-detalhes-panel evento-detalhes-list-panel">
        <div className="evento-detalhes-panel-header">
          <div>
            <span className="evento-detalhes-kicker">Convites</span>
            <h2>Convites por personagem</h2>
          </div>
          <strong>{totalConvites}</strong>
        </div>

        <div className="evento-detalhes-list">
          {personagensEvento.length > 0 ? (
            personagensEvento.map((personagem) => {
              const convitesPersonagem = convitesPorPersonagem[personagem.id] || [];
              const escalacao = escalacoesPorPersonagem[personagem.id];

              return (
                <button
                  key={personagem.id}
                  className={`evento-detalhes-list-item evento-detalhes-personagem-convite${
                    escalacao ? " is-escalado" : ""
                  }`}
                  type="button"
                  onClick={() => abrirModal(personagem)}
                >
                  <div>
                    <h3>{personagem.personagemNome}</h3>
                    <p>{personagem.personagemItemCodigo}</p>
                  </div>
                  <div className="evento-detalhes-meta">
                    <span>{convitesPersonagem.length} convite(s)</span>
                    {escalacao ? (
                      <span className="evento-detalhes-status status-confirmada">
                        {escalacao.atorNome}
                      </span>
                    ) : (
                      <span className="evento-detalhes-status status-pendente">Aberto</span>
                    )}
                  </div>
                </button>
              );
            })
          ) : (
            <div className="evento-detalhes-empty">
              <p>Nenhum personagem adicionado a este evento.</p>
            </div>
          )}
        </div>
      </section>

      {personagemModal && (
        <div className="evento-detalhes-modal" role="dialog" aria-modal="true">
          <div className="evento-detalhes-modal-content evento-detalhes-modal-content-wide">
            <div className="evento-detalhes-modal-header">
              <div>
                <span className="evento-detalhes-kicker">Personagem Item</span>
                <h2>{personagemModal.personagemNome}</h2>
                <p>{personagemModal.personagemItemCodigo}</p>
              </div>
              <button
                className="evento-detalhes-modal-close"
                type="button"
                title="Fechar"
                aria-label="Fechar"
                onClick={fecharModal}
              >
                <X size={20} />
              </button>
            </div>

            {personagemModal.escalacao && (
              <div className="evento-detalhes-escalado-banner">
                <CheckCircle2 size={18} />
                <span>Escalado para {personagemModal.escalacao.atorNome}</span>
              </div>
            )}

            <div className="evento-detalhes-convite-modal-body">
              <section>
                <div className="evento-detalhes-subheader">
                  <h3>Convites</h3>
                  <span>{personagemModal.convites.length}</span>
                </div>

                <div className="evento-detalhes-filter">
                  <select
                    value={filtroStatus}
                    aria-label="Filtrar convites por status"
                    onChange={(event) => setFiltroStatus(event.target.value)}
                  >
                    {filtrosConvite.map((status) => (
                      <option key={status} value={status}>
                        {status === "TODOS" ? "Todos os convites" : status}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="evento-detalhes-modal-list evento-detalhes-convites-modal-list">
                  {convitesFiltrados.length > 0 ? (
                    convitesFiltrados.map((convite) => {
                      const podeEscalar = convite.status === "ACEITO" && !personagemModal.escalacao;
                      const podeReativar = convite.status === "CANCELADO" && !personagemModal.escalacao;

                      return (
                        <article key={convite.id} className="evento-detalhes-list-item">
                          <div>
                            <h3>{convite.atorNome}</h3>
                            <p>{convite.personagemItemCodigo}</p>
                          </div>
                          <div className="evento-detalhes-meta">
                            <span className={`evento-detalhes-status status-${convite.status.toLowerCase()}`}>
                              {convite.status}
                            </span>
                            <div className="evento-detalhes-row-actions">
                              {podeEscalar && (
                                <button
                                  className="evento-detalhes-icon-button"
                                  type="button"
                                  title="Adicionar à escalação"
                                  aria-label={`Adicionar ${convite.atorNome} à escalação`}
                                  disabled={Boolean(escalandoConviteId || processandoConviteId)}
                                  onClick={() => onAdicionarEscalacao(convite)}
                                >
                                  <UserPlus size={16} />
                                  <span>{escalandoConviteId === convite.id ? "Adicionando" : "Escalar"}</span>
                                </button>
                              )}
                              {podeReativar && (
                                <button
                                  className="evento-detalhes-icon-button"
                                  type="button"
                                  title="Reativar convite"
                                  aria-label={`Reativar convite de ${convite.atorNome}`}
                                  disabled={Boolean(processandoConviteId || escalandoConviteId)}
                                  onClick={() => onReativarConvite(convite)}
                                >
                                  <RotateCcw size={16} />
                                  <span>{processandoConviteId === convite.id ? "Reativando" : "Reativar"}</span>
                                </button>
                              )}
                              <button
                                className="evento-detalhes-delete-button"
                                type="button"
                                title="Excluir convite"
                                aria-label={`Excluir convite de ${convite.atorNome}`}
                                disabled={Boolean(processandoConviteId || escalandoConviteId)}
                                onClick={() => onExcluirConvite(convite)}
                              >
                                <Trash2 size={16} />
                              </button>
                            </div>
                          </div>
                        </article>
                      );
                    })
                  ) : (
                    <div className="evento-detalhes-empty">
                      <p>Nenhum convite encontrado para este filtro.</p>
                    </div>
                  )}
                </div>
              </section>

              <section className={personagemModal.escalacao ? "is-disabled" : ""}>
                <div className="evento-detalhes-subheader">
                  <h3>Adicionar convites</h3>
                  <span>{atoresSelecionados.length}</span>
                </div>

                <div className="evento-detalhes-search">
                  <input
                    type="text"
                    value={buscaAtor}
                    placeholder="Buscar ator ou e-mail"
                    disabled={Boolean(personagemModal.escalacao)}
                    onChange={(event) => setBuscaAtor(event.target.value)}
                  />
                  <Search size={18} />
                </div>

                <div className="evento-detalhes-modal-list evento-detalhes-atores-list">
                  {atoresDisponiveis.length > 0 ? (
                    atoresDisponiveis.map((ator) => (
                      <label key={ator.id} className="evento-detalhes-ator-option">
                        <input
                          type="checkbox"
                          checked={atoresSelecionados.includes(ator.id)}
                          disabled={Boolean(personagemModal.escalacao)}
                          onChange={() => alternarAtor(ator.id)}
                        />
                        <span>
                          <strong>{ator.nome}</strong>
                          <small>{ator.email}</small>
                        </span>
                      </label>
                    ))
                  ) : (
                    <div className="evento-detalhes-empty">
                      <p>Nenhum ator disponível para novo convite.</p>
                    </div>
                  )}
                </div>

                <div className="evento-detalhes-modal-footer">
                  <button
                    className="evento-detalhes-icon-button"
                    type="button"
                    disabled={
                      Boolean(personagemModal.escalacao) ||
                      atoresSelecionados.length === 0 ||
                      enviandoConvitesEventoPersonagemId === personagemModal.id
                    }
                    onClick={adicionarConvites}
                  >
                    <Plus size={16} />
                    <span>
                      {enviandoConvitesEventoPersonagemId === personagemModal.id
                        ? "Enviando"
                        : "Adicionar convites"}
                    </span>
                  </button>
                </div>
              </section>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default ConvitesList;
