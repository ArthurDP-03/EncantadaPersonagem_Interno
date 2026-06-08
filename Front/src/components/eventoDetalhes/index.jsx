import './index.css';
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import Swal from 'sweetalert2';
import { getEventoById } from '../../services/eventosService';
import { useEventos } from '../../hooks/useEventos';
import { useConvitesDoEvento } from '../../hooks/useConvitesDoEvento'; 
import { useClientes } from '../../hooks/useClientes';
import { useAdministradores } from '../../hooks/useAdministradores';

const eventoVazio = {
  titulo: "",
  descricao: "",
  dataInicio: "",
  dataFim: "",
  endereco: "",
  status: "RASCUNHO",
  tipoPagamento: "",
  valorTotal: "",
  clienteId: "",
  administradorCriadorId: "",
};

function FormEvento({ dados, onChange, clientes, administradores, desabilitado }) {
  return (
    <div className="form-evento">
      <label>Título
        <input 
          value={dados.titulo} 
          onChange={e => onChange({ ...dados, titulo: e.target.value })} 
          placeholder="Nome do evento" 
          disabled={desabilitado}
        />
      </label>
      <label>Descrição
        <textarea 
          value={dados.descricao} 
          onChange={e => onChange({ ...dados, descricao: e.target.value })} 
          placeholder="Descrição do evento" 
          rows={3}
          disabled={desabilitado}
        />
      </label>
      <div className="form-evento-linha">
        <label>Data de início
          <input 
            type="datetime-local" 
            value={dados.dataInicio} 
            onChange={e => onChange({ ...dados, dataInicio: e.target.value })}
            disabled={desabilitado}
          />
        </label>
        <label>Data de fim
          <input 
            type="datetime-local" 
            value={dados.dataFim} 
            onChange={e => onChange({ ...dados, dataFim: e.target.value })}
            disabled={desabilitado}
          />
        </label>
      </div>
      <label>Endereço
        <input 
          value={dados.endereco} 
          onChange={e => onChange({ ...dados, endereco: e.target.value })} 
          placeholder="Rua, número, cidade"
          disabled={desabilitado}
        />
      </label>
      <div className="form-evento-linha">
        <label>Status
          <select 
            value={dados.status} 
            onChange={e => onChange({ ...dados, status: e.target.value })}
            disabled={desabilitado}
          >
            <option value="RASCUNHO">Rascunho</option>
            <option value="CONFIRMADO">Confirmado</option>
            <option value="EM_ANDAMENTO">Em Andamento</option>
            <option value="FINALIZADO">Finalizado</option>
            <option value="CANCELADO">Cancelado</option>
          </select>
        </label>
        <label>Tipo de pagamento
          <input 
            value={dados.tipoPagamento} 
            onChange={e => onChange({ ...dados, tipoPagamento: e.target.value })} 
            placeholder="PIX, Cartão..."
            disabled={desabilitado}
          />
        </label>
      </div>
      <label>Valor total (R$)
        <input 
          type="number" 
          value={dados.valorTotal} 
          onChange={e => onChange({ ...dados, valorTotal: e.target.value })} 
          placeholder="0,00"
          disabled={desabilitado}
        />
      </label>
      <label>Cliente
        <select 
          value={dados.clienteId} 
          onChange={e => onChange({ ...dados, clienteId: e.target.value })}
          disabled={desabilitado}
        >
          <option value="">Selecione um cliente</option>
          {clientes.map(c => (
            <option key={c.id} value={c.id}>{c.nome}</option>
          ))}
        </select>
      </label>
      <label>Administrador responsável
        <select 
          value={dados.administradorCriadorId} 
          onChange={e => onChange({ ...dados, administradorCriadorId: e.target.value })}
          disabled={desabilitado}
        >
          <option value="">Selecione um administrador</option>
          {administradores.map(a => (
            <option key={a.id} value={a.id}>{a.nome}</option>
          ))}
        </select>
      </label>
    </div>
  );
}

function ConvitesList({ convites, carregandoConvites }) {
  if (carregandoConvites) return <p className="loading">Carregando convites...</p>;
  if (convites.length === 0) return <p className="vazio">Nenhum convite encontrado</p>;

  return (
    <div className="convites-list">
      {convites.map(convite => (
        <div key={convite.id} className="convite-card">
          <div className="convite-header">
            <h4>{convite.personagemNome}</h4>
            <span className={`status status-${convite.status.toLowerCase()}`}>
              {convite.status}
            </span>
          </div>
          <div className="convite-details">
            <p><strong>Ator:</strong> {convite.atorNome}</p>
            <p><strong>Data de envio:</strong> {new Date(convite.dataEnvio).toLocaleDateString('pt-BR')}</p>
            <p><strong>Expiração:</strong> {new Date(convite.dataExpiracao).toLocaleDateString('pt-BR')}</p>
            {convite.dataResposta && (
              <p><strong>Data resposta:</strong> {new Date(convite.dataResposta).toLocaleDateString('pt-BR')}</p>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}

function EventoDetalhes() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [evento, setEvento] = useState(null);
  const [formData, setFormData] = useState(eventoVazio);
  const [editando, setEditando] = useState(false);
  const [carregandoEvento, setCarregandoEvento] = useState(true);

  const { editar } = useEventos();
  const { convites, carregando: carregandoConvites } = useConvitesDoEvento(id); // ← limpo
  const { clientes } = useClientes();
  const { administradores } = useAdministradores();

  useEffect(() => {
    const carregarEvento = async () => {
      try {
        setCarregandoEvento(true);
        const dados = await getEventoById(Number(id));
        setEvento(dados);
        setFormData(dados);
      } catch (err) {
        console.error('Erro ao carregar evento:', err);
        Swal.fire({
          icon: 'error',
          title: 'Erro ao carregar evento',
          text: err.data?.error || 'Não foi possível carregar o evento',
        });
        navigate('/eventos');
      } finally {
        setCarregandoEvento(false);
      }
    };
    carregarEvento();
  }, [id, navigate]);

  const handleSalvar = async (e) => {
    e.preventDefault();
    if (!formData.titulo.trim()) return;
    try {
      await editar(evento.id, {
        ...formData,
        valorTotal: Number(formData.valorTotal),
        clienteId: Number(formData.clienteId),
        administradorCriadorId: Number(formData.administradorCriadorId),
      });
      setEditando(false);
    } catch (err) {
      console.error('Erro ao salvar evento:', err);
    }
  };

  const handleCancelar = () => {
    setFormData(evento);
    setEditando(false);
  };

  if (carregandoEvento) return <p className="loading">Carregando...</p>;
  if (!evento) return null;

  return (
    <section className="section-evento-detalhes">
      <div className="conteudo-95 layout">

        <div className="header-detalhes">
          <button className="btn-voltar" onClick={() => navigate('/eventos')} title="Voltar">
            <ArrowLeft size={20} />
            Voltar
          </button>
          <h1 className="titulo t1">{evento.titulo}</h1>
        </div>

        <div className="conteudo-detalhes">

          <div className="coluna coluna-esquerda">
            <div className={`form-container ${editando ? 'editando' : 'desabilitado'}`}>
              <form onSubmit={handleSalvar}>
                <div className="form-header">
                  <h2>Dados do Evento</h2>
                  {!editando && (
                    <button type="button" className="btn-editar-form" onClick={() => setEditando(true)}>
                      Editar
                    </button>
                  )}
                </div>

                <FormEvento
                  dados={formData}
                  onChange={setFormData}
                  clientes={clientes}
                  administradores={administradores}
                  desabilitado={!editando}
                />

                {editando && (
                  <div className="form-acoes">
                    <button type="button" className="btn-secundario" onClick={handleCancelar}>
                      Cancelar
                    </button>
                    <button type="submit" className="btn-primario">
                      Salvar
                    </button>
                  </div>
                )}
              </form>
            </div>
          </div>

          <div className="coluna coluna-direita">
            <div className="convites-container">
              <h2>Convites ({convites.length})</h2>
              <ConvitesList convites={convites} carregandoConvites={carregandoConvites} />
            </div>
          </div>

        </div>
      </div>
    </section>
  );
}

export default EventoDetalhes;
