// components/Clientes/Clientes.jsx
import './index.css'
import { Search, ChevronDown, Plus, AlertCircle } from "lucide-react";
import { useState } from "react";
import { useClientes } from '../../hooks/useClientes';
import Card_linha from '../card_linha';

const clienteVazio = { nome: "", email: "", telefone: "" };

function ErroInline({ mensagem }) {
  if (!mensagem) return null;
  return (
    <div className="erro-inline" role="alert">
      <AlertCircle size={16} />
      <span>{mensagem}</span>
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
    </div>
  );
}

function Clientes() {
  const [busca, setBusca]             = useState("");
  const [ordem, setOrdem]             = useState("");
  const [modalCriar, setModalCriar]   = useState(false);
  const [modalEditar, setModalEditar] = useState(null);
  const [form, setForm]               = useState(clienteVazio);
  const [erroCriar, setErroCriar]     = useState("");  
  const [erroEditar, setErroEditar]   = useState("");   
  const [salvando, setSalvando]       = useState(false);

  const { clientes, carregando, erro, criar: adicionarCliente, editar: editarCliente, deletar: removerCliente } = useClientes();
  if (carregando) return <p>Carregando...</p>;
  if (erro) return <p>Erro: {erro}</p>;

  const clientesFiltrados = clientes
    .filter(c =>
      c.nome.toLowerCase().includes(busca.toLowerCase()) ||
      c.email.toLowerCase().includes(busca.toLowerCase())
    )
    .sort((a, b) => {
      if (ordem === "az") return a.nome.localeCompare(b.nome);
      if (ordem === "za") return b.nome.localeCompare(a.nome);
      return 0;
    });

  async function handleCriar(event) {
    event.preventDefault();
    if (!form.nome.trim()) return;
    setErroCriar("");
    setSalvando(true);

    try {
      await adicionarCliente({ nome: form.nome, telefone: form.telefone, email: form.email });
      setModalCriar(false);
      setForm(clienteVazio);
    } catch (mensagem) {
      // mensagem é a string extraída pelo hook (400/422) ou um Error (500)
      setErroCriar(typeof mensagem === "string" ? mensagem : "Erro inesperado. Tente novamente.");
    } finally {
      setSalvando(false);
    }
  }

  async function handleEditar(event) {
    event.preventDefault();
    if (!modalEditar) return;
    setErroEditar("");
    setSalvando(true);

    try {
      await editarCliente(modalEditar.id, { nome: modalEditar.nome, telefone: modalEditar.telefone, email: modalEditar.email });
      setModalEditar(null);
    } catch (mensagem) {
      setErroEditar(typeof mensagem === "string" ? mensagem : "Erro inesperado. Tente novamente.");
    } finally {
      setSalvando(false);
    }
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

          <div className="cards">
            {clientesFiltrados.length === 0 ? (
              <div className="clientes-vazio">Nenhum cliente encontrado.</div>
            ) : (
              clientesFiltrados.map((c) => (
                <Card_linha
                  key={c.id}
                  titulo={c.nome}
                  informacoes={{ Email: c.email, Telefone: c.telefone }}
                  onEditar={() => { setErroEditar(""); setModalEditar({ ...c }); }}
                  onDeletar={() => removerCliente(c.id)}
                />
              ))
            )}
          </div>

        </div>
      </div>

      {/* FAB */}
      <button className="clientes-fab" title="Novo cliente" onClick={() => { setErroCriar(""); setForm(clienteVazio); setModalCriar(true); }}>
        <Plus size={24} />
      </button>

      {/* Modal: Criar */}
      {modalCriar && (
        <div className="modal">
          <form onSubmit={handleCriar}>
            <h2 className="modal-titulo">Novo Cliente</h2>
            <FormCliente dados={form} onChange={setForm} />
            <ErroInline mensagem={erroCriar} />
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => { setModalCriar(false); setForm(clienteVazio); }}>Cancelar</button>
              <button type="submit" className="btn-primario" disabled={salvando}>{salvando ? "Salvando…" : "Criar"}</button>
            </div>
          </form>
        </div>
      )}

      {/* Modal: Editar */}
      {modalEditar && (
        <div className="modal">
          <form onSubmit={handleEditar}>
            <h2 className="modal-titulo">Editar Cliente</h2>
            <FormCliente dados={modalEditar} onChange={setModalEditar} />
            <ErroInline mensagem={erroEditar} />
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => setModalEditar(null)}>Cancelar</button>
              <button type="submit" className="btn-primario" disabled={salvando}>{salvando ? "Salvando…" : "Salvar"}</button>
            </div>
          </form>
        </div>
      )}

    </section>
  );
}

export default Clientes;