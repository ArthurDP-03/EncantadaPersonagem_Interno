// components/Personagens.jsx
import { usePersonagens } from "../../hooks/usePersonagem";
import './index.css'
import { Search, ChevronDown, Plus, Pencil, Trash2 } from "lucide-react";
import Swal from "sweetalert2";
import logo from '../../assets/logo.png'
import { useState } from "react";
import { useTranslation } from "react-i18next";

const personagemVazio = {
  nome: "",
  descricao: "",
  foto: "",
};

function FormPersonagem({ dados, onChange }) {
  const { t } = useTranslation();

  return (
    <div className="form-personagem">
      <label>{t('common.fields.name')}
        <input value={dados.nome} onChange={e => onChange({ ...dados, nome: e.target.value })} placeholder={t('characters.placeholders.name')} />
      </label>
      <label>{t('common.fields.description')}
        <textarea value={dados.descricao} onChange={e => onChange({ ...dados, descricao: e.target.value })} placeholder={t('characters.placeholders.description')} rows={3} />
      </label>
      <label>{t('common.fields.photo')} ({t('common.fields.url')})
        <input value={dados.foto} onChange={e => onChange({ ...dados, foto: e.target.value })} placeholder={t('characters.placeholders.photo')} />
      </label>
    </div>
  );
}

function Personagens() {
  const { t } = useTranslation();
  const [busca, setBusca] = useState("");
  const [ordem, setOrdem] = useState("");
  const [modalCriar, setModalCriar] = useState(false);
  const [modalEditar, setModalEditar] = useState(null);
  const [form, setForm] = useState(personagemVazio);
  const [imagem] = useState(logo);

  const { personagens, carregando, erro, criar, editar, deletar } = usePersonagens();

  if (carregando) return <p>{t('common.loading')}</p>;
  if (erro) return <p>{t('common.error', { message: erro })}</p>;

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

  function mostrarValidacao() {
    Swal.fire({
      icon: "warning",
      title: t('common.validation.title'),
      text: t('common.validation.required', { field: t('common.fields.name') }),
    });
  }

  function handleCriar(event) {
    event.preventDefault();
    if (!form.nome.trim()) {
      mostrarValidacao();
      return;
    }

    criar(form)
      .then(() => {
        setModalCriar(false);
        setForm(personagemVazio);
      })
      .catch(err => {
        console.error("Erro ao criar personagem:", err);
        // O erro é tratado no hook com Swal
      });
  }

  function handleEditar(event) {
    event.preventDefault();
    if (!modalEditar) return;

    if (!modalEditar.nome.trim()) {
      mostrarValidacao();
      return;
    }

    editar(modalEditar.id, {
      nome: modalEditar.nome,
      descricao: modalEditar.descricao,
      foto: modalEditar.foto,
    })
      .then(() => {
        setModalEditar(null);
      })
      .catch(err => {
        console.error("Erro ao editar personagem:", err);
        // O erro é tratado no hook com Swal
      });
  }

  return (
    <section className="section-personagens">
      <div className="conteudo-95 layout">
        <div className="conteudo">

          <h1 className="titulo t1">{t('characters.title')}</h1>
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
          <ul className="lista-personagem">
            {personagensFiltrados.map(personagem => (
              <li key={personagem.id} className="card-personagem">
                <div className="imagem-container">
                  <img src={imagem} alt="" className="imagem" />
                  <div className="card-personagem-acoes">
                    <button
                      className="btn-icone btn-editar"
                      type="button"
                      title={t('common.edit')}
                      onClick={() => setModalEditar({ ...personagem })}
                    >
                      <Pencil size={16} />
                    </button>
                    <button
                      className="btn-icone btn-deletar"
                      type="button"
                      title={t('common.delete')}
                      onClick={() => deletar(personagem.id)}
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>
                <div className="textos">
                  <div className="texto t1"><p>{personagem.nome}</p></div>
                  <div className="texto t2"><p>{personagem.descricao || t('characters.noDescription')}</p></div>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* FAB – Novo personagem */}
      <button className="personagens-fab" title={t('characters.newButton')} onClick={() => { setForm(personagemVazio); setModalCriar(true); }}>
        <Plus size={24} />
      </button>

      {/* Modal: Criar */}
      {modalCriar && (
        <div className="modal">
          <form onSubmit={handleCriar}>
            <h2 className="modal-titulo">{t('characters.newTitle')}</h2>
            <FormPersonagem dados={form} onChange={setForm} />
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => { setModalCriar(false); setForm(personagemVazio); }}>{t('common.cancel')}</button>
              <button type="submit" className="btn-primario">{t('common.create')}</button>
            </div>
          </form>
        </div>
      )}

      {/* Modal: Editar */}
      {modalEditar && (
        <div className="modal">
          <form onSubmit={handleEditar}>
            <h2 className="modal-titulo">{t('characters.editTitle')}</h2>
            <FormPersonagem dados={modalEditar} onChange={setModalEditar} />
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => setModalEditar(null)}>{t('common.cancel')}</button>
              <button type="submit" className="btn-primario">{t('common.save')}</button>
            </div>
          </form>
        </div>
      )}

    </section>
  );
}

export default Personagens;
