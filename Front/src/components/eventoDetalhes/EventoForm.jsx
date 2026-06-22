import { EventoStatus } from "../../services/eventosService";
import { formatarStatus } from "../../utils/formatters";

function EventoForm({
  dados,
  onChange,
  clientes,
  administradores,
  disabled,
  editando,
  salvando,
  onEditar,
  onCancelar,
  onSalvar,
}) {
  return (
    <form
      className={`evento-detalhes-form ${disabled ? "is-disabled" : ""}`}
      onSubmit={onSalvar}
    >
      <div className="evento-detalhes-panel-header">
        <div>
          <span className="evento-detalhes-kicker">Dados do evento</span>
          <h2>Cadastro e edição</h2>
        </div>
        <div className="evento-detalhes-actions">
          {!editando ? (
            <button className="btn-primario" type="button" onClick={onEditar}>
              Editar
            </button>
          ) : (
            <>
              <button className="btn-secundario" type="button" onClick={onCancelar}>
                Cancelar
              </button>
              <button className="btn-primario" type="submit" disabled={salvando}>
                {salvando ? "Salvando..." : "Salvar Dados"}
              </button>
            </>
          )}
        </div>
      </div>

      <div className="evento-detalhes-form-body">
        <label>
          Título
          <input
            disabled={disabled}
            value={dados.titulo}
            onChange={(e) => onChange({ ...dados, titulo: e.target.value })}
            placeholder="Nome do evento"
          />
        </label>

        <label>
          Descrição
          <textarea
            disabled={disabled}
            value={dados.descricao}
            onChange={(e) => onChange({ ...dados, descricao: e.target.value })}
            rows={5}
            placeholder="Descrição do evento"
          />
        </label>

        <div className="evento-detalhes-form-row">
          <label>
            Data de início
            <input
              disabled={disabled}
              type="datetime-local"
              value={dados.dataInicio}
              onChange={(e) => onChange({ ...dados, dataInicio: e.target.value })}
            />
          </label>
          <label>
            Data de fim
            <input
              disabled={disabled}
              type="datetime-local"
              value={dados.dataFim}
              onChange={(e) => onChange({ ...dados, dataFim: e.target.value })}
            />
          </label>
        </div>

        <label>
          Endereço
          <input
            disabled={disabled}
            value={dados.endereco}
            onChange={(e) => onChange({ ...dados, endereco: e.target.value })}
            placeholder="Local do evento"
          />
        </label>

        <div className="evento-detalhes-form-row">
          <label>
            Status
            <select
              disabled={disabled}
              value={dados.status}
              onChange={(e) => onChange({ ...dados, status: e.target.value })}
            >
              {Object.values(EventoStatus).map((status) => (
                <option key={status} value={status}>
                  {formatarStatus(status)}
                </option>
              ))}
            </select>
          </label>

          <label>
            Tipo de pagamento
            <input
              disabled={disabled}
              value={dados.tipoPagamento}
              onChange={(e) => onChange({ ...dados, tipoPagamento: e.target.value })}
              placeholder="Pix, dinheiro, cartão..."
            />
          </label>
        </div>

        <label>
          Valor total (R$)
          <input
            disabled={disabled}
            type="number"
            value={dados.valorTotal}
            onChange={(e) => onChange({ ...dados, valorTotal: e.target.value })}
            placeholder="0,00"
          />
        </label>

        <label>
          Cliente
          <select
            disabled={disabled}
            value={dados.clienteId}
            onChange={(e) => onChange({ ...dados, clienteId: e.target.value })}
          >
            <option value="">Selecione um cliente</option>
            {clientes.map((cliente) => (
              <option key={cliente.id} value={cliente.id}>
                {cliente.nome}
              </option>
            ))}
          </select>
        </label>

        <label>
          Administrador responsável
          <select
            disabled={disabled}
            value={dados.administradorCriadorId}
            onChange={(e) =>
              onChange({ ...dados, administradorCriadorId: e.target.value })
            }
          >
            <option value="">Selecione um administrador</option>
            {administradores.map((administrador) => (
              <option key={administrador.id} value={administrador.id}>
                {administrador.nome}
              </option>
            ))}
          </select>
        </label>
      </div>
    </form>
  );
}

export default EventoForm;
