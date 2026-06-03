// components/Clientes/Clientes.jsx
import './index.css'
import { Search, ChevronDown, Plus } from "lucide-react";
import { useState } from "react";
import Swal from "sweetalert2";
import { useClientes } from '../../hooks/useClientes';
import Card_linha from '../card_linha';

const clienteVazio = { nome: "", email: "", telefone: "" };

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
  const [busca, setBusca]               = useState("");
  const [ordem, setOrdem]               = useState("");
  const [modalCriar, setModalCriar]     = useState(false);
  const [modalEditar, setModalEditar]   = useState(null);
  const [form, setForm]                 = useState(clienteVazio);
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

  function handleCriar(event) {
    event.preventDefault();
    // Validações obrigatórias
    if (!form.nome.trim()) {
      Swal.fire({ icon: "warning", title: "Validação", text: "O campo Nome é obrigatório para preenchimento" });
      return;
    }
    if (!form.email.trim()) {
      Swal.fire({ icon: "warning", title: "Validação", text: "O campo Email é obrigatório para preenchimento" });
      return;
    }
    if (!form.telefone.trim()) {
      Swal.fire({ icon: "warning", title: "Validação", text: "O campo Telefone é obrigatório para preenchimento" });
      return;
    }

    adicionarCliente({ nome: form.nome, telefone: form.telefone, email: form.email })
      .then(() => {
        setModalCriar(false);
        setForm(clienteVazio);
      })
      .catch(err => {
        console.error("Erro ao criar cliente:", err);
        // O erro é tratado no hook com Swal
      });
  }

  function handleEditar(event) {
    event.preventDefault();
    if (!modalEditar) return;

    // Validações obrigatórias para edição
    if (!modalEditar.nome.trim()) {
      Swal.fire({ icon: "warning", title: "Validação", text: "O campo Nome é obrigatório para preenchimento" });
      return;
    }
    if (!modalEditar.email.trim()) {
      Swal.fire({ icon: "warning", title: "Validação", text: "O campo Email é obrigatório para preenchimento" });
      return;
    }
    if (!modalEditar.telefone.trim()) {
      Swal.fire({ icon: "warning", title: "Validação", text: "O campo Telefone é obrigatório para preenchimento" });
      return;
    }

    editarCliente(modalEditar.id, { nome: modalEditar.nome, telefone: modalEditar.telefone, email: modalEditar.email })
      .then(() => {
        setModalEditar(null);
      })
      .catch(err => {
        console.error("Erro ao editar cliente:", err);
        // O erro é tratado no hook com Swal
      });
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

          {/*Tabela CRUD*/}
          <div className="cards">
            {clientesFiltrados.length === 0 ? (
              <div className="clientes-vazio">Nenhum cliente encontrado.</div>
            ) : (
              clientesFiltrados.map((c) => (
                <Card_linha
                  key={c.id}
                  titulo={c.nome}
                  informacoes={{
                    Email: c.email,
                    Telefone: c.telefone,
                  }}
                  onEditar={() => setModalEditar({ ...c })}
                  onDeletar={() => removerCliente(c.id)}
                />
              ))
            )}
          </div>

        </div>
      </div>

      {/* FAB – Novo cliente */}
      <button className="clientes-fab" title="Novo cliente" onClick={() => { setForm(clienteVazio); setModalCriar(true); }}>
        <Plus size={24} />
      </button>

      {/* Modal: Criar */}
      {modalCriar && (
        <div className="modal">
          <form onSubmit={handleCriar}>
            <h2 className="modal-titulo">Novo Cliente</h2>
            <FormCliente dados={form} onChange={setForm} />
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => { setModalCriar(false); setForm(clienteVazio); }}>Cancelar</button>
              <button type="submit" className="btn-primario">Criar</button>
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

export default Clientes;