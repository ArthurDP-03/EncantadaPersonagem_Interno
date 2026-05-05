// components/Personagens.jsx
import { usePersonagens } from "../../hooks/usePersonagem";
import './index.css'
import { Search, ChevronDown, Plus, Pencil, Trash2 } from "lucide-react";
import logo from '../../assets/logo.png'
import { useState } from "react";

const personagemVazio = {
  nome: "",
  descricao: "",
  foto: "",
};

function FormPersonagem({ dados, onChange }) {
  return (
    <div className="form-personagem">
      <label>Nome
        <input value={dados.nome} onChange={e => onChange({ ...dados, nome: e.target.value })} placeholder="Nome do personagem" />
      </label>
      <label>Descrição
        <textarea value={dados.descricao} onChange={e => onChange({ ...dados, descricao: e.target.value })} placeholder="Descrição do personagem" rows={3} />
      </label>
      <label>Foto (URL)
        <input value={dados.foto} onChange={e => onChange({ ...dados, foto: e.target.value })} placeholder="URL da foto" />
      </label>
    </div>
  );
}

function Personagens() {
  const [busca, setBusca] = useState("");
  const [ordem, setOrdem] = useState("");
  const [modalCriar, setModalCriar] = useState(false);
  const [modalEditar, setModalEditar] = useState(null);
  const [form, setForm] = useState(personagemVazio);
  const [imagem] = useState(logo);

  const { personagens, carregando, erro, criar, editar, deletar } = usePersonagens();

  if (carregando) return <p>Carregando...</p>;
  if (erro) return <p>Erro: {erro}</p>;

  const personagensFiltrados = personagens
    .filter(p =>
      p.nome.toLowerCase().includes(busca.toLowerCase()) ||
      p.descricao?.toLowerCase().includes(busca.toLowerCase())
    )
    .sort((a, b) => {
      if (ordem === "az") return a.nome.localeCompare(b.nome);
      if (ordem === "za") return b.nome.localeCompare(a.nome);
      return 0;
    });

  function handleCriar(event) {
    event.preventDefault();
    if (!form.nome.trim()) return;

    criar(form).then(() => {
      setModalCriar(false);
      setForm(personagemVazio);
    });
  }

  function handleEditar(event) {
    event.preventDefault();
    if (!modalEditar) return;

    editar(modalEditar.id, {
      nome: modalEditar.nome,
      descricao: modalEditar.descricao,
      foto: modalEditar.foto,
    }).then(() => setModalEditar(null));
  }

  return (
    <section className="section-personagens">
      <div className="conteudo-95 layout">
        <div className="conteudo">

          <h1 className="titulo t1">Personagens</h1>
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
          <ul className="lista-personagem">
            {personagensFiltrados.map(personagem => (
              <li key={personagem.id} className="card-personagem">
                <div className="imagem-container">
                  <img src={personagem.foto || imagem} alt="" className="imagem" />
                  <div className="card-personagem-acoes">
                    <button
                      className="btn-icone btn-editar"
                      type="button"
                      title="Editar"
                      onClick={() => setModalEditar({ ...personagem })}
                    >
                      <Pencil size={16} />
                    </button>
                    <button
                      className="btn-icone btn-deletar"
                      type="button"
                      title="Deletar"
                      onClick={() => deletar(personagem.id)}
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>
                <div className="textos">
                  <div className="texto t1"><p>{personagem.nome}</p></div>
                  <div className="texto t2"><p>{personagem.descricao || "Sem descrição"}</p></div>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* FAB – Novo personagem */}
      <button className="personagens-fab" title="Novo personagem" onClick={() => { setForm(personagemVazio); setModalCriar(true); }}>
        <Plus size={24} />
      </button>

      {/* Modal: Criar */}
      {modalCriar && (
        <div className="modal">
          <form onSubmit={handleCriar}>
            <h2 className="modal-titulo">Novo Personagem</h2>
            <FormPersonagem dados={form} onChange={setForm} />
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => { setModalCriar(false); setForm(personagemVazio); }}>Cancelar</button>
              <button type="submit" className="btn-primario">Criar</button>
            </div>
          </form>
        </div>
      )}

      {/* Modal: Editar */}
      {modalEditar && (
        <div className="modal">
          <form onSubmit={handleEditar}>
            <h2 className="modal-titulo">Editar Personagem</h2>
            <FormPersonagem dados={modalEditar} onChange={setModalEditar} />
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

export default Personagens;