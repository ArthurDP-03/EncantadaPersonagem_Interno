// components/Clientes/Clientes.jsx
import './index.css'
import { Search, ChevronDown, Plus, Pencil, Trash2 } from "lucide-react";
import { useState } from "react";

const clientesIniciais = [
  { id: 1, nome: "Ana Souza",      email: "ana.souza@email.com",      telefone: "41 9 9999-1111", eventosAtivos: 2, eventosTotais: 5, ultimoEvento: "15/03/2026" },
  { id: 2, nome: "Bruno Lima",     email: "bruno.lima@email.com",     telefone: "41 9 9999-2222", eventosAtivos: 1, eventosTotais: 3, ultimoEvento: "01/02/2026" },
  { id: 3, nome: "Carla Mendes",   email: "carla.mendes@email.com",   telefone: "41 9 9999-3333", eventosAtivos: 0, eventosTotais: 7, ultimoEvento: "20/01/2026" },
  { id: 4, nome: "Diego Costa",    email: "diego.costa@email.com",    telefone: "41 9 9999-4444", eventosAtivos: 3, eventosTotais: 4, ultimoEvento: "10/04/2026" },
  { id: 5, nome: "Elena Rocha",    email: "elena.rocha@email.com",    telefone: "41 9 9999-5555", eventosAtivos: 1, eventosTotais: 2, ultimoEvento: "28/03/2026" },
  { id: 6, nome: "Felipe Alves",   email: "felipe.alves@email.com",   telefone: "41 9 9999-6666", eventosAtivos: 2, eventosTotais: 6, ultimoEvento: "05/04/2026" },
  { id: 7, nome: "Gabriela Nunes", email: "gabriela.nunes@email.com", telefone: "41 9 9999-7777", eventosAtivos: 0, eventosTotais: 1, ultimoEvento: "12/12/2025" },
];

const clienteVazio = { nome: "", email: "", telefone: "", eventosAtivos: 0, eventosTotais: 0, ultimoEvento: "" };

function Modal({ titulo, onConfirmar, onCancelar, confirmLabel = "Confirmar", danger = false, children }) {
  return (
    <div className="modal-overlay" onClick={onCancelar}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <h2 className="modal-titulo">{titulo}</h2>
        {children}
        <div className="modal-acoes">
          <button className="btn-secundario" onClick={onCancelar}>Cancelar</button>
          <button className={danger ? "btn-perigo" : "btn-primario"} onClick={onConfirmar}>{confirmLabel}</button>
        </div>
      </div>
    </div>
  );
}

function FormCliente({ dados, onChange }) {
  return (
    <div className="form-cliente">
      <label>Nome
        <input value={dados.nome} onChange={e => onChange({ ...dados, nome: e.target.value })} placeholder="Nome completo" />
      </label>
      <label>Email
        <input value={dados.email} onChange={e => onChange({ ...dados, email: e.target.value })} placeholder="email@exemplo.com" />
      </label>
      <label>Telefone
        <input value={dados.telefone} onChange={e => onChange({ ...dados, telefone: e.target.value })} placeholder="41 9 9999-0000" />
      </label>
      <div className="form-row">
        <label>Eventos Ativos
          <input type="number" min={0} value={dados.eventosAtivos} onChange={e => onChange({ ...dados, eventosAtivos: Number(e.target.value) })} />
        </label>
        <label>Eventos Totais
          <input type="number" min={0} value={dados.eventosTotais} onChange={e => onChange({ ...dados, eventosTotais: Number(e.target.value) })} />
        </label>
      </div>
      <label>Último Evento
        <input value={dados.ultimoEvento} onChange={e => onChange({ ...dados, ultimoEvento: e.target.value })} placeholder="DD/MM/AAAA" />
      </label>
    </div>
  );
}

function Clientes() {
  // const { clientes, carregando, erro } = useClientes();
  // if (carregando) return <p>Carregando...</p>;
  // if (erro) return <p>Erro: {erro}</p>;

  const [clientes, setClientes]         = useState(clientesIniciais);
  const [busca, setBusca]               = useState("");
  const [ordem, setOrdem]               = useState("");
  const [modalCriar, setModalCriar]     = useState(false);
  const [modalEditar, setModalEditar]   = useState(null);
  const [modalDeletar, setModalDeletar] = useState(null);
  const [form, setForm]                 = useState(clienteVazio);

  const clientesFiltrados = clientes
    .filter(c =>
      c.nome.toLowerCase().includes(busca.toLowerCase()) ||
      c.email.toLowerCase().includes(busca.toLowerCase())
    )
    .sort((a, b) => {
      if (ordem === "az") return a.nome.localeCompare(b.nome);
      if (ordem === "za") return b.nome.localeCompare(a.nome);
      if (ordem === "ativos") return b.eventosAtivos - a.eventosAtivos;
      return 0;
    });

  function handleCriar() {
    if (!form.nome.trim()) return;
    setClientes(prev => [...prev, { ...form, id: Date.now() }]);
    setModalCriar(false);
    setForm(clienteVazio);
  }

  function handleEditar() {
    if (!modalEditar) return;
    setClientes(prev => prev.map(c => c.id === modalEditar.id ? modalEditar : c));
    setModalEditar(null);
  }

  function handleDeletar() {
    setClientes(prev => prev.filter(c => c.id !== modalDeletar));
    setModalDeletar(null);
  }

  return (
    <section className="section-clientes">
      <div className="conteudo-95 layout">
        <div className="conteudo">

          <h1 className="titulo t1">Clientes</h1>

          <div className="filtros">
            <div className="input-container">
              <select className="input" value={ordem} onChange={e => setOrdem(e.target.value)}>
                <option value="">Ordenar</option>
                <option value="az">Alfabética (A-Z)</option>
                <option value="za">Alfabética (Z-A)</option>
                <option value="ativos">Disponibilidade</option>
              </select>
              <ChevronDown className="icon" size={18} />
            </div>
            <div className="input-container">
              <input
                type="text"
                placeholder="Buscar"
                className="input"
                value={busca}
                onChange={e => setBusca(e.target.value)}
              />
              <Search className="icon" size={18} />
            </div>
          </div>

          {/* ── Tabela CRUD ── */}
          <ul className="clientes-lista">
            {clientesFiltrados.length === 0 && (
              <li className="clientes-vazio">Nenhum cliente encontrado.</li>
            )}
            {clientesFiltrados.map((c, i) => (
              <li key={c.id} className="clientes-item" style={{ animationDelay: `${i * 0.04}s` }}>
                <div className="clientes-item-info">
                  <span className="clientes-item-nome">{c.nome}</span>
                  <span className="clientes-item-email">Email: {c.email}</span>
                </div>
                <div className="clientes-item-detalhes">
                  <span>Telefone: {c.telefone}</span>
                  <span>Eventos Ativos: {c.eventosAtivos}</span>
                  <span>Eventos totais: {c.eventosTotais}</span>
                  <span>Último evento: {c.ultimoEvento}</span>
                </div>
                <div className="clientes-item-acoes">
                  <button className="btn-icone btn-adicionar" title="Novo cliente" onClick={() => { setForm(clienteVazio); setModalCriar(true); }}>
                    <Plus size={16} />
                  </button>
                  <button className="btn-icone btn-editar" title="Editar" onClick={() => setModalEditar({ ...c })}>
                    <Pencil size={16} />
                  </button>
                  <button className="btn-icone btn-deletar" title="Excluir" onClick={() => setModalDeletar(c.id)}>
                    <Trash2 size={16} />
                  </button>
                </div>
              </li>
            ))}
          </ul>

        </div>
      </div>

      {/* FAB – Novo cliente */}
      <button className="clientes-fab" title="Novo cliente" onClick={() => { setForm(clienteVazio); setModalCriar(true); }}>
        <Plus size={24} />
      </button>

      {/* Modal: Criar */}
      {modalCriar && (
        <Modal titulo="Novo Cliente" onConfirmar={handleCriar} onCancelar={() => { setModalCriar(false); setForm(clienteVazio); }} confirmLabel="Criar">
          <FormCliente dados={form} onChange={setForm} />
        </Modal>
      )}

      {/* Modal: Editar */}
      {modalEditar && (
        <Modal titulo="Editar Cliente" onConfirmar={handleEditar} onCancelar={() => setModalEditar(null)} confirmLabel="Salvar">
          <FormCliente dados={modalEditar} onChange={setModalEditar} />
        </Modal>
      )}

      {/* Modal: Deletar */}
      {modalDeletar && (
        <Modal titulo="Excluir Cliente" onConfirmar={handleDeletar} onCancelar={() => setModalDeletar(null)} confirmLabel="Excluir" danger>
          <p className="modal-texto">Tem certeza que deseja excluir este cliente? Esta ação não pode ser desfeita.</p>
        </Modal>
      )}
    </section>
  );
}

export default Clientes;