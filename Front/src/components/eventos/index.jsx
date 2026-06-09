import './index.css';
import { Search, ChevronDown, Plus, Pencil, Trash2 } from "lucide-react";
import { useState } from "react";
import { useEventos } from '../../hooks/useEventos';
import { useClientes } from '../../hooks/useClientes';
import { useAdministradores } from '../../hooks/useAdministradores';
import logo from '../../assets/logo.png';
import { formatarPeriodoEvento, agruparEventosPorData, labelData, formatarStatus } from '../../utils/formatters';
import { EventoStatus } from '../../services/eventosService';

const eventoVazio = {
  titulo: "",
  descricao: "",
  dataInicio: "",
  dataFim: "",
  endereco: "",
  status: EventoStatus.RASCUNHO,
  tipoPagamento: "",
  valorTotal: "",
  clienteId: "",
  administradorCriadorId: "",
};

function FormEvento({ dados, onChange, clientes, administradores }) {
  return (
    <div className="form-evento">
      <label>Título
        <input value={dados.titulo} onChange={e => onChange({ ...dados, titulo: e.target.value })} placeholder="Nome do evento" />
      </label>
      <label>Descrição
        <textarea value={dados.descricao} onChange={e => onChange({ ...dados, descricao: e.target.value })} placeholder="Descrição do evento" rows={3} />
      </label>
      <div className="form-evento-linha">
        <label>Data de início
          <input type="datetime-local" value={dados.dataInicio} onChange={e => onChange({ ...dados, dataInicio: e.target.value })} />
        </label>
        <label>Data de fim
          <input type="datetime-local" value={dados.dataFim} onChange={e => onChange({ ...dados, dataFim: e.target.value })} />
        </label>
      </div>
      <label>Endereço
        <input value={dados.endereco} onChange={e => onChange({ ...dados, endereco: e.target.value })} placeholder="Rua, número, cidade" />
      </label>
      <div className="form-evento-linha">
        <label>Status
          <select value={dados.status} onChange={e => onChange({ ...dados, status: e.target.value })}>
            {Object.values(EventoStatus).map(s => (
              <option key={s} value={s}>{formatarStatus(s)}</option>
            ))}
          </select>
        </label>
        <label>Tipo de pagamento
          <input value={dados.tipoPagamento} onChange={e => onChange({ ...dados, tipoPagamento: e.target.value })} placeholder="PIX, Cartão..." />
        </label>
      </div>
      <label>Valor total (R$)
        <input type="number" value={dados.valorTotal} onChange={e => onChange({ ...dados, valorTotal: e.target.value })} placeholder="0,00" />
      </label>
      <label>Cliente
        <select value={dados.clienteId} onChange={e => onChange({ ...dados, clienteId: e.target.value })}>
          <option value="">Selecione um cliente</option>
          {clientes.map(c => (
            <option key={c.id} value={c.id}>{c.nome}</option>
          ))}
        </select>
      </label>
      <label>Administrador responsável
        <select value={dados.administradorCriadorId} onChange={e => onChange({ ...dados, administradorCriadorId: e.target.value })}>
          <option value="">Selecione um administrador</option>
          {administradores.map(a => (
            <option key={a.id} value={a.id}>{a.nome}</option>
          ))}
        </select>
      </label>
    </div>
  );
}

function Eventos() {
  const [busca, setBusca]             = useState("");
  const [ordem, setOrdem]             = useState("");
  const [modalCriar, setModalCriar]   = useState(false);
  const [modalEditar, setModalEditar] = useState(null);
  const [form, setForm]               = useState(eventoVazio);
  const [imagem]                      = useState(logo);

  const { eventos, carregando, erro, criar, editar, deletar } = useEventos();
  const { clientes }                                          = useClientes();
  const { administradores }                                   = useAdministradores();

  if (carregando) return <p>Carregando...</p>;
  if (erro) return <p>Erro: {erro}</p>;

  const eventosFiltrados = eventos
    .filter(e =>
      e.titulo.toLowerCase().includes(busca.toLowerCase()) ||
      e.endereco?.toLowerCase().includes(busca.toLowerCase())
    )
    .sort((a, b) => {
      if (ordem === "az") return a.titulo.localeCompare(b.titulo);
      if (ordem === "za") return b.titulo.localeCompare(a.titulo);
      return 0;
    });

  const grupos = agruparEventosPorData(eventosFiltrados);

  function handleCriar(event) {
    event.preventDefault();
    if (!form.titulo.trim()) return;

    criar({
      ...form,
      valorTotal: Number(form.valorTotal),
      clienteId: Number(form.clienteId),
      administradorCriadorId: Number(form.administradorCriadorId),
    }).then(() => {
      setModalCriar(false);
      setForm(eventoVazio);
    });
  }

  function handleEditar(event) {
    event.preventDefault();
    if (!modalEditar) return;

    editar(modalEditar.id, {
      titulo: modalEditar.titulo,
      descricao: modalEditar.descricao,
      dataInicio: modalEditar.dataInicio,
      dataFim: modalEditar.dataFim,
      endereco: modalEditar.endereco,
      status: modalEditar.status,
      tipoPagamento: modalEditar.tipoPagamento,
      valorTotal: Number(modalEditar.valorTotal),
      clienteId: Number(modalEditar.clienteId),
      administradorCriadorId: Number(modalEditar.administradorCriadorId),
    }).then(() => setModalEditar(null));
  }

  const formProps = { clientes, administradores };

  return (
    <section className="section-eventos">
      <div className="conteudo-95 layout">
        <div className="conteudo">

          <h1 className="titulo t1">Eventos</h1>

          <div className="timeline">
            {grupos.length === 0 ? (
              <div className="eventos-vazio">Nenhum evento encontrado.</div>
            ) : (
              grupos.map((item, idx) => {
                const { prefixo, texto } = labelData(item.data);
                return (
                  <div key={idx} className="grupo-data">
                    <div className="label-data">
                      {prefixo && <span className="texto t1">{prefixo},&nbsp;</span>}
                      <span className="texto-data texto t1">{texto}</span>
                    </div>

                    <ul className="lista-eventos">
                      {item.eventos.map(evento => (
                        <li key={evento.id} className="card-eventos">
                          <div className="imagem-container">
                            <img src={imagem} alt="" className="imagem" />
                            <div className="card-eventos-acoes">
                              <button
                                className="btn-icone btn-editar"
                                type="button"
                                title="Editar"
                                onClick={() => setModalEditar({ ...evento })}
                              >
                                <Pencil size={16} />
                              </button>
                              <button
                                className="btn-icone btn-deletar"
                                type="button"
                                title="Deletar"
                                onClick={() => deletar(evento.id)}
                              >
                                <Trash2 size={16} />
                              </button>
                            </div>
                          </div>
                          <div className="textos">
                            <div className="texto t1"><p>{evento.titulo}</p></div>
                            <div className="texto t2"><p>{formatarStatus(evento.status)}</p></div>
                            <div className="texto t2"><p>{formatarPeriodoEvento(evento.dataInicio, evento.dataFim)}</p></div>
                            <div className="texto t2"><p>{evento.endereco}</p></div>
                          </div>
                        </li>
                      ))}
                    </ul>
                  </div>
                );
              })
            )}
          </div>

        </div>
      </div>

      {/* FAB – Novo evento */}
      <button className="eventos-fab" title="Novo evento" onClick={() => { setForm(eventoVazio); setModalCriar(true); }}>
        <Plus size={24} />
      </button>

      {/* Modal: Criar */}
      {modalCriar && (
        <div className="modal">
          <form onSubmit={handleCriar}>
            <h2 className="modal-titulo">Novo Evento</h2>
            <FormEvento dados={form} onChange={setForm} {...formProps} />
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => { setModalCriar(false); setForm(eventoVazio); }}>Cancelar</button>
              <button type="submit" className="btn-primario">Criar</button>
            </div>
          </form>
        </div>
      )}

      {/* Modal: Editar */}
      {modalEditar && (
        <div className="modal">
          <form onSubmit={handleEditar}>
            <h2 className="modal-titulo">Editar Evento</h2>
            <FormEvento dados={modalEditar} onChange={setModalEditar} {...formProps} />
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => setModalEditar(null)}>Cancelar</button>
              <button type="submit" className="btn-primario">Salvar</button>
            </div>
          </form>
        </div>
      )}

    </section>
  );
}

export default Eventos;
