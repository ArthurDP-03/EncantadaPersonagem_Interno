import './index.css';
import { Search, ChevronDown, Plus, Pencil, Trash2 } from "lucide-react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useEventos } from '../../hooks/useEventos';
import { useClientes } from '../../hooks/useClientes';
import { useAdministradores } from '../../hooks/useAdministradores';
import logo from '../../assets/logo.png';
import { formatarPeriodoEvento, agruparEventosPorData, labelData, formatarStatus } from '../../utils/formatters';
import { EventoStatus } from '../../services/eventosService';
import { useTranslation } from 'react-i18next';

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
  const { t } = useTranslation();

  return (
    <div className="form-evento">
      <label>{t('common.fields.title')}
        <input value={dados.titulo} onChange={e => onChange({ ...dados, titulo: e.target.value })} placeholder={t('events.placeholders.title')} />
      </label>
      <label>{t('common.fields.description')}
        <textarea value={dados.descricao} onChange={e => onChange({ ...dados, descricao: e.target.value })} placeholder={t('events.placeholders.description')} rows={3} />
      </label>
      <div className="form-evento-linha">
        <label>{t('common.fields.startDate')}
          <input type="datetime-local" value={dados.dataInicio} onChange={e => onChange({ ...dados, dataInicio: e.target.value })} />
        </label>
        <label>{t('common.fields.endDate')}
          <input type="datetime-local" value={dados.dataFim} onChange={e => onChange({ ...dados, dataFim: e.target.value })} />
        </label>
      </div>
      <label>{t('common.fields.address')}
        <input value={dados.endereco} onChange={e => onChange({ ...dados, endereco: e.target.value })} placeholder={t('events.placeholders.address')} />
      </label>
      <div className="form-evento-linha">
        <label>{t('common.fields.status')}
          <select value={dados.status} onChange={e => onChange({ ...dados, status: e.target.value })}>
            {Object.values(EventoStatus).map(s => (
              <option key={s} value={s}>{formatarStatus(s)}</option>
            ))}
          </select>
        </label>
        <label>{t('common.fields.paymentType')}
          <input value={dados.tipoPagamento} onChange={e => onChange({ ...dados, tipoPagamento: e.target.value })} placeholder={t('events.placeholders.paymentType')} />
        </label>
      </div>
      <label>{t('common.fields.totalValue')} (R$)
        <input type="number" value={dados.valorTotal} onChange={e => onChange({ ...dados, valorTotal: e.target.value })} placeholder={t('common.placeholders.value')} />
      </label>
      <label>{t('common.fields.client')}
        <select value={dados.clienteId} onChange={e => onChange({ ...dados, clienteId: e.target.value })}>
          <option value="">{t('events.select.client')}</option>
          {clientes.map(c => (
            <option key={c.id} value={c.id}>{c.nome}</option>
          ))}
        </select>
      </label>
      <label>{t('common.fields.responsibleAdmin')}
        <select value={dados.administradorCriadorId} onChange={e => onChange({ ...dados, administradorCriadorId: e.target.value })}>
          <option value="">{t('events.select.admin')}</option>
          {administradores.map(a => (
            <option key={a.id} value={a.id}>{a.nome}</option>
          ))}
        </select>
      </label>
    </div>
  );
}

function Eventos() {
  const { t } = useTranslation();
  const [busca, setBusca]             = useState("");
  const [ordem, setOrdem]             = useState("");
  const [modalCriar, setModalCriar]   = useState(false);
  const [form, setForm]               = useState(eventoVazio);
  const [imagem]                      = useState(logo);
  const navigate                       = useNavigate();

  const { eventos, carregando, erro, criar, deletar } = useEventos();
  const { clientes }                                          = useClientes();
  const { administradores }                                   = useAdministradores();

  if (carregando) return <p>{t('common.loading')}</p>;
  if (erro) return <p>{t('common.error', { message: erro })}</p>;

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

  const formProps = { clientes, administradores };

  return (
    <section className="section-eventos">
      <div className="conteudo-95 layout">
        <div className="conteudo">

          <h1 className="titulo t1">{t('events.title')}</h1>

          <div className="filtros">
            <div className="input-container">
              <select className="input" value={ordem} onChange={e => setOrdem(e.target.value)}>
                <option value="">{t('common.order')}</option>
                <option value="az">{t('common.orderAZ')}</option>
                <option value="za">{t('common.orderZA')}</option>
              </select>
              <ChevronDown className="icon" size={18} />
            </div>
            <div className="input-container">
              <input
                type="text"
                placeholder={t('common.search')}
                className="input"
                value={busca}
                onChange={e => setBusca(e.target.value)}
              />
              <Search className="icon" size={18} />
            </div>
          </div>

          <div className="timeline">
            {grupos.length === 0 ? (
              <div className="eventos-vazio">{t('events.empty')}</div>
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
                                title="Detalhes do evento"
                                onClick={() => navigate(`/eventos/${evento.id}`)}
                              >
                                <Pencil size={16} />
                              </button>
                              <button
                                className="btn-icone btn-deletar"
                                type="button"
                                title={t('common.delete')}
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
      <button className="eventos-fab" title={t('events.newButton')} onClick={() => { setForm(eventoVazio); setModalCriar(true); }}>
        <Plus size={24} />
      </button>

      {/* Modal: Criar */}
      {modalCriar && (
        <div className="modal">
          <form onSubmit={handleCriar}>
            <h2 className="modal-titulo">{t('events.newTitle')}</h2>
            <FormEvento dados={form} onChange={setForm} {...formProps} />
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => { setModalCriar(false); setForm(eventoVazio); }}>{t('common.cancel')}</button>
              <button type="submit" className="btn-primario">{t('common.create')}</button>
            </div>
          </form>
        </div>
      )}

    </section>
  );
}

export default Eventos;
