function ConvitesList({ convites }) {
  return (
    <section className="evento-detalhes-panel evento-detalhes-list-panel">
      <div className="evento-detalhes-panel-header">
        <div>
          <span className="evento-detalhes-kicker">Convites</span>
          <h2>Convites enviados</h2>
        </div>
        <strong>{convites.length}</strong>
      </div>

      <div className="evento-detalhes-list">
        {convites.length > 0 ? (
          convites.map((convite) => (
            <article key={convite.id} className="evento-detalhes-list-item">
              <div>
                <h3>{convite.atorNome}</h3>
                <p>{convite.personagemNome}</p>
              </div>
              <div className="evento-detalhes-meta">
                <span>{convite.personagemItemCodigo}</span>
                <span className={`evento-detalhes-status status-${convite.status.toLowerCase()}`}>
                  {convite.status}
                </span>
              </div>
            </article>
          ))
        ) : (
          <div className="evento-detalhes-empty">
            <p>Nenhum convite enviado para este evento.</p>
          </div>
        )}
      </div>
    </section>
  );
}

export default ConvitesList;
