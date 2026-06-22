function EscalacaoList({ escalacoes, personagensEvento }) {
  return (
    <section className="evento-detalhes-panel evento-detalhes-list-panel">
      <div className="evento-detalhes-panel-header">
        <div>
          <span className="evento-detalhes-kicker">Escalação</span>
          <h2>Atores escalados</h2>
        </div>
        <strong>{escalacoes.length}</strong>
      </div>

      <div className="evento-detalhes-list">
        {escalacoes.length > 0 ? (
          escalacoes.map((escala) => (
            <article key={escala.id} className="evento-detalhes-list-item">
              <div>
                <h3>{escala.atorNome}</h3>
                <p>{escala.personagemNome}</p>
              </div>
              <div className="evento-detalhes-meta">
                <span>{escala.personagemItemCodigo}</span>
                <span className={`evento-detalhes-status status-${escala.status.toLowerCase()}`}>
                  {escala.status.replaceAll("_", " ")}
                </span>
              </div>
            </article>
          ))
        ) : (
          <div className="evento-detalhes-empty">
            <p>Nenhuma escalação encontrada para este evento.</p>
            {personagensEvento.length > 0 && (
              <small>
                Personagens vinculados:{" "}
                {personagensEvento.map((personagem) => personagem.personagemNome).join(", ")}
              </small>
            )}
          </div>
        )}
      </div>
    </section>
  );
}

export default EscalacaoList;
