import { useAdministradores } from "../../hooks/useAdministradores";
import { useAtores } from "../../hooks/useAtores";
import Card_linha from "../card_linha";
import "./index.css";
import { Search, ChevronDown, Plus } from "lucide-react";
import Swal from "sweetalert2";
import { useState } from "react";
import { useTranslation } from "react-i18next";

const adminVazio = {
  nome: "",
  email: "",
  telefone: "",
  senha: "",
  tipo: "ADMIN",
};
const atorVazio = {
  nome: "",
  email: "",
  senha: "",
  telefone: "",
  genero: "",
  altura: "",
  peso: "",
  observacao: "",
};

function FormAdministrador({ dados, onChange }) {
  const { t } = useTranslation();

  return (
    <div className="form-colaborador">
      <label className="campo-largo">
        {t('common.fields.name')}
        <input
          value={dados.nome}
          onChange={(e) => onChange({ ...dados, nome: e.target.value })}
          placeholder={t('collaborators.placeholders.name')}
        />
      </label>
      <label className="campo-largo">
        {t('common.fields.email')}
        <input
          value={dados.email}
          onChange={(e) => onChange({ ...dados, email: e.target.value })}
          placeholder={t('collaborators.placeholders.email')}
        />
      </label>
      <label>
        {t('common.fields.phone')}
        <input
          value={dados.telefone}
          onChange={(e) => onChange({ ...dados, telefone: e.target.value })}
          placeholder={t('collaborators.placeholders.phone')}
        />
      </label>
      <label>
        {t('common.fields.password')}
        <input
          type="password"
          value={dados.senha}
          onChange={(e) => onChange({ ...dados, senha: e.target.value })}
          placeholder={t('collaborators.placeholders.password')}
        />
      </label>
    </div>
  );
}

function FormAtor({ dados, onChange }) {
  const { t } = useTranslation();

  return (
    <div className="form-colaborador">
      <label className="campo-largo">
        {t('common.fields.name')}
        <input
          value={dados.nome}
          onChange={(e) => onChange({ ...dados, nome: e.target.value })}
          placeholder={t('collaborators.placeholders.name')}
        />
      </label>
      <label className="campo-largo">
        {t('common.fields.email')}
        <input
          value={dados.email}
          onChange={(e) => onChange({ ...dados, email: e.target.value })}
          placeholder={t('collaborators.placeholders.email')}
        />
      </label>
      <label>
        {t('common.fields.password')}
        <input
          type="password"
          value={dados.senha}
          onChange={(e) => onChange({ ...dados, senha: e.target.value })}
          placeholder={t('collaborators.placeholders.password')}
        />
      </label>
      <label>
        {t('common.fields.phone')}
        <input
          value={dados.telefone}
          onChange={(e) => onChange({ ...dados, telefone: e.target.value })}
          placeholder={t('collaborators.placeholders.phone')}
        />
      </label>
      <label>
        {t('common.fields.gender')}
        <select
          value={dados.genero}
          onChange={(e) => onChange({ ...dados, genero: e.target.value })}
        >
          <option value="">{t('collaborators.gender.select')}</option>
          <option value="Masculino">{t('collaborators.gender.male')}</option>
          <option value="Feminino">{t('collaborators.gender.female')}</option>
          <option value="Outro">{t('collaborators.gender.other')}</option>
        </select>
      </label>
      <label>
        {t('common.fields.height')} (m)
        <input
          type="number"
          step="0.01"
          value={dados.altura}
          onChange={(e) => onChange({ ...dados, altura: e.target.value })}
          placeholder={t('collaborators.placeholders.height')}
        />
      </label>
      <label>
        {t('common.fields.weight')} (kg)
        <input
          type="number"
          value={dados.peso}
          onChange={(e) => onChange({ ...dados, peso: e.target.value })}
          placeholder={t('collaborators.placeholders.weight')}
        />
      </label>
      <label className="campo-largo">
        {t('common.fields.observation')}
        <textarea
          value={dados.observacao}
          onChange={(e) => onChange({ ...dados, observacao: e.target.value })}
          placeholder={t('collaborators.placeholders.observations')}
        />
      </label>
    </div>
  );
}

function Colaboradores() {
  const { t } = useTranslation();
  const {
    administradores,
    carregando: carregandoAdministradores,
    erro: erroAdministradores,
    deletar: deletarAdmin,
    criar: criarAdmin,
    editar: editarAdmin,
  } = useAdministradores();
  const {
    atores,
    carregando: carregandoAtores,
    erro: erroAtores,
    deletar: deletarAtor,
    criar: criarAtor,
    editar: editarAtor,
  } = useAtores();

  const [modalCriar, setModalCriar] = useState(false);
  const [modalEditar, setModalEditar] = useState(null);
  const [tipoForm, setTipoForm] = useState("admin");
  const [formAdmin, setFormAdmin] = useState(adminVazio);
  const [formAtor, setFormAtor] = useState(atorVazio);
  const [busca, setBusca] = useState("");
  const [ordem, setOrdem] = useState("");

  if (carregandoAdministradores || carregandoAtores)
    return <p>{t('common.loading')}</p>;
  if (erroAdministradores || erroAtores)
    return <p>{t('common.error', { message: erroAdministradores || erroAtores })}</p>;

  function filtrarEOrdenar(lista) {
    return lista
      .filter(
        (c) =>
          c.nome.toLowerCase().includes(busca.toLowerCase()) ||
          c.email.toLowerCase().includes(busca.toLowerCase()),
      )
      .sort((a, b) => {
        if (ordem === "az") return a.nome.localeCompare(b.nome);
        if (ordem === "za") return b.nome.localeCompare(a.nome);
        return 0;
      });
  }

  const administradoresFiltrados = filtrarEOrdenar(administradores);
  const atoresFiltrados = filtrarEOrdenar(atores);

  function mostrarValidacao(fieldKey, requiredNumber = false) {
    Swal.fire({
      icon: "warning",
      title: t('common.validation.title'),
      text: requiredNumber
        ? t('common.validation.requiredNumber', { field: t(fieldKey) })
        : t('common.validation.required', { field: t(fieldKey) }),
    });
  }

  function handleCriar(event) {
    event.preventDefault();
    if (tipoForm === "admin") {
      if (!formAdmin.nome.trim()) {
        mostrarValidacao('common.fields.name');
        return;
      }
      if (!formAdmin.email.trim()) {
        mostrarValidacao('common.fields.email');
        return;
      }
      if (!formAdmin.telefone.trim()) {
        mostrarValidacao('common.fields.phone');
        return;
      }
      if (!formAdmin.senha.trim()) {
        mostrarValidacao('common.fields.password');
        return;
      }
      if (!formAdmin.tipo?.trim()) {
        mostrarValidacao('common.fields.type');
        return;
      }

      criarAdmin(formAdmin)
        .then(() => {
          setModalCriar(false);
          setFormAdmin(adminVazio);
        })
        .catch(err => {
          console.error("Erro ao criar administrador:", err);
          // O erro é tratado no hook com Swal
        });
    } else {
      if (!formAtor.nome.trim()) {
        mostrarValidacao('common.fields.name');
        return;
      }
      if (!formAtor.email.trim()) {
        mostrarValidacao('common.fields.email');
        return;
      }
      if (!formAtor.telefone.trim()) {
        mostrarValidacao('common.fields.phone');
        return;
      }
      if (!formAtor.senha.trim()) {
        mostrarValidacao('common.fields.password');
        return;
      }
      if (!formAtor.genero.trim()) {
        mostrarValidacao('common.fields.gender');
        return;
      }
      if (formAtor.altura === "" || formAtor.altura === null || formAtor.altura === undefined || isNaN(Number(formAtor.altura))) {
        mostrarValidacao('common.fields.height', true);
        return;
      }
      if (formAtor.peso === "" || formAtor.peso === null || formAtor.peso === undefined || isNaN(Number(formAtor.peso))) {
        mostrarValidacao('common.fields.weight', true);
        return;
      }

      criarAtor({
        ...formAtor,
        altura: parseFloat(formAtor.altura),
        peso: parseFloat(formAtor.peso),
        ativo: true
      })
        .then(() => {
          setModalCriar(false);
          setFormAtor(atorVazio);
        })
        .catch(err => {
          console.error("Erro ao criar ator:", err);
          // O erro é tratado no hook com Swal
        });
    }
  }

  function handleEditar(event) {
    event.preventDefault();
    if (!modalEditar) return;

    const dados = modalEditar.dados || {};
    if (!dados.nome?.trim()) {
      mostrarValidacao('common.fields.name');
      return;
    }
    if (!dados.email?.trim()) {
      mostrarValidacao('common.fields.email');
      return;
    }
    if (!dados.telefone?.trim()) {
      mostrarValidacao('common.fields.phone');
      return;
    }

    if (modalEditar.tipo === "admin") {
      if (!dados.senha?.trim()) {
        mostrarValidacao('common.fields.password');
        return;
      }
      if (!dados.tipo?.trim()) {
        mostrarValidacao('common.fields.type');
        return;
      }

      editarAdmin(modalEditar.dados.id, modalEditar.dados)
        .then(() => {
          setModalEditar(null);
        })
        .catch(err => {
          console.error("Erro ao editar administrador:", err);
          // O erro é tratado no hook com Swal
        });
    } else {
      if (!dados.senha?.trim()) {
        mostrarValidacao('common.fields.password');
        return;
      }
      if (!dados.genero?.trim()) {
        mostrarValidacao('common.fields.gender');
        return;
      }
      if (dados.altura === "" || dados.altura === null || dados.altura === undefined || isNaN(Number(dados.altura))) {
        mostrarValidacao('common.fields.height', true);
        return;
      }
      if (dados.peso === "" || dados.peso === null || dados.peso === undefined || isNaN(Number(dados.peso))) {
        mostrarValidacao('common.fields.weight', true);
        return;
      }

      editarAtor(modalEditar.dados.id, {
        ...modalEditar.dados,
        altura: parseFloat(modalEditar.dados.altura),
        peso: parseFloat(modalEditar.dados.peso),
      })
        .then(() => {
          setModalEditar(null);
        })
        .catch(err => {
          console.error("Erro ao editar ator:", err);
          // O erro é tratado no hook com Swal
        });
    }
  }

  return (
    <section className="section-colaboradores">
      <div className="conteudo-95 layout">
        <div className="conteudo">
          <h1 className="titulo t1">{t('collaborators.title')}</h1>
          <div className="filtros">
            <div className="input-container">
              <select
                className="input"
                value={ordem}
                onChange={(e) => setOrdem(e.target.value)}
              >
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
                onChange={(e) => setBusca(e.target.value)}
              />
              <Search className="icon" size={18} />
            </div>
          </div>

          <div className="lista lista-administradores">
            <div className="titulo t2">
              <p>{t('collaborators.sections.admins')}</p>
            </div>
            <div className="cards">
              {administradoresFiltrados.length === 0 ? (
                <div className="clientes-vazio">{t('collaborators.empty.admins')}</div>
              ) : (
                administradoresFiltrados.map((admin) => (
                  <Card_linha
                    key={admin.id}
                    titulo={admin.nome}
                    informacoes={{ [t('common.fields.email')]: admin.email, [t('common.fields.phone')]: admin.telefone }}
                    onEditar={() =>
                      setModalEditar({
                        tipo: "admin",
                        dados: { ...admin, senha: "" },
                      })
                    }
                    onDeletar={() => deletarAdmin(admin.id)}
                  />
                ))
              )}
            </div>
          </div>

          <div className="lista lista-atores">
            <div className="titulo t2">
              <p>{t('collaborators.sections.actors')}</p>
            </div>
            <div className="cards">
              {atoresFiltrados.length === 0 ? (
                <div className="clientes-vazio">{t('collaborators.empty.actors')}</div>
              ) : (
                atoresFiltrados.map((ator) => (
                  <Card_linha
                    key={ator.id}
                    titulo={ator.nome}
                    informacoes={{
                      [t('common.fields.email')]: ator.email,
                      [t('common.fields.phone')]: ator.telefone,
                      [t('common.fields.gender')]: ator.genero,
                      [t('common.fields.height')]: ator.altura,
                      [t('common.fields.weight')]: ator.peso,
                      [t('common.fields.observation')]: ator.observacao,
                    }}
                    onEditar={() =>
                      setModalEditar({
                        tipo: "ator",
                        dados: { ...ator, senha: "" },
                      })
                    }
                    onDeletar={() => deletarAtor(ator.id)}
                  />
                ))
              )}
            </div>
          </div>
        </div>
      </div>

      <button className="btn-create" title={t('collaborators.newButton')} onClick={() => setModalCriar(true)}>
        <Plus size={24} />
      </button>

      {modalCriar && (
        <div className="modal">
          <form onSubmit={handleCriar}>
            <h2 className="modal-titulo">{t('collaborators.newTitle')}</h2>
            <div className="modal-abas">
              <button
                type="button"
                className={tipoForm === "admin" ? "aba-ativa" : ""}
                onClick={() => setTipoForm("admin")}
              >
                {t('collaborators.tabs.admin')}
              </button>
              <button
                type="button"
                className={tipoForm === "ator" ? "aba-ativa" : ""}
                onClick={() => setTipoForm("ator")}
              >
                {t('collaborators.tabs.actor')}
              </button>
            </div>
            {tipoForm === "admin" ? (
              <FormAdministrador dados={formAdmin} onChange={setFormAdmin} />
            ) : (
              <FormAtor dados={formAtor} onChange={setFormAtor} />
            )}
            <div className="modal-acoes">
              <button
                type="button"
                className="btn-secundario"
                onClick={() => {
                  setModalCriar(false);
                  setFormAdmin(adminVazio);
                  setFormAtor(atorVazio);
                }}
              >
                {t('common.cancel')}
              </button>
              <button type="submit" className="btn-primario">
                {t('common.create')}
              </button>
            </div>
          </form>
        </div>
      )}

      {modalEditar && (
        <div className="modal">
          <form onSubmit={handleEditar}>
            <h2 className="modal-titulo">
              {t('collaborators.editTitle', {
                item: modalEditar.tipo === "admin"
                  ? t('collaborators.tabs.admin')
                  : t('collaborators.tabs.actor'),
              })}
            </h2>
            {modalEditar.tipo === "admin" ? (
              <FormAdministrador
                dados={modalEditar.dados}
                onChange={(dados) => setModalEditar({ ...modalEditar, dados })}
              />
            ) : (
              <FormAtor
                dados={modalEditar.dados}
                onChange={(dados) => setModalEditar({ ...modalEditar, dados })}
              />
            )}
            <div className="modal-acoes">
              <button
                type="button"
                className="btn-secundario"
                onClick={() => setModalEditar(null)}
              >
                {t('common.cancel')}
              </button>
              <button type="submit" className="btn-primario">
                {t('common.save')}
              </button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}

export default Colaboradores;
